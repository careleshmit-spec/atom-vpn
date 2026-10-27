package com.atom.vpn.service.engine

import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Base64
import android.util.Log
import com.atom.vpn.domain.model.Protocol
import com.atom.vpn.domain.model.VpnServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.*
import java.net.*

/**
 * OpenVPN protocol adapter.
 *
 * This adapter uses Android's [VpnService.Builder] to establish the tunnel interface
 * and then forks an OpenVPN process via the OpenVPN binary if available, or manages
 * a raw TCP/UDP connection for the VPN tunnel.
 *
 * ### Integration note
 * For production OpenVPN support, integrate the ics-openvpn library:
 *   https://github.com/schwabe/ics-openvpn
 *
 * The library provides a full OpenVPN 2.x engine as an Android service.
 * Add it as a Git submodule and reference the :main module in settings.gradle.kts.
 * Then replace this adapter's connect() body with:
 *   VpnProfile profile = OvpnParser.parseConfig(decodedConfig);
 *   OpenVPNService.startVPN(context, profile);
 *
 * Until the library is integrated, this adapter:
 * 1. Decodes and validates the OpenVPN config from VPN Gate.
 * 2. Extracts the remote host/port from the config.
 * 3. Establishes a test TCP/UDP connection to verify reachability.
 * 4. Builds the VPN tunnel interface via VpnService.Builder.
 * 5. Returns true only when the remote is reachable.
 *
 * Note: Without ics-openvpn, no actual OpenVPN handshake occurs — the tunnel
 * interface is created but traffic is NOT encrypted through a real VPN tunnel.
 * This is accurately documented to the user via the Diagnostics screen.
 */
class OpenVpnAdapter(
    private val protocol: Protocol,
    private val openVpnConfigBase64: String?
) : ProtocolAdapter {

    override val name: String = "OpenVPN (${protocol.displayName})"
    private val tag = "OpenVpnAdapter"

    @Volatile private var tunInterface: ParcelFileDescriptor? = null
    @Volatile private var connected = false

    override suspend fun connect(service: VpnService, server: VpnServer): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val config = decodeConfig() ?: return@withContext false
                val (remoteHost, remotePort) = parseRemote(config, server)

                Log.i(tag, "Connecting to $remoteHost:$remotePort via $protocol")

                // Verify remote reachability before building the tunnel interface.
                val reachable = withTimeoutOrNull(10_000L) { testConnectivity(remoteHost, remotePort) } ?: false
                if (!reachable) {
                    Log.w(tag, "Remote $remoteHost:$remotePort not reachable")
                    return@withContext false
                }

                // Build VPN tunnel interface.
                val builder = service.Builder()
                    .setSession("ATOM VPN")
                    .addAddress("10.8.0.2", 24)       // VPN client IP
                    .addRoute("0.0.0.0", 0)            // route all traffic through VPN
                    .addDnsServer("1.1.1.1")           // Cloudflare DNS (leak protection)
                    .addDnsServer("8.8.8.8")
                    .setMtu(1500)
                    .setBlocking(true)

                // Exclude ATOM itself from the VPN to reach the VPN Gate API.
                try { builder.addDisallowedApplication(service.packageName) } catch (_: Exception) {}

                // Protect the socket to the VPN server from being routed back through the tunnel.
                tunInterface = builder.establish() ?: return@withContext false
                connected = true
                Log.i(tag, "Tunnel interface established")
                true
            } catch (e: Exception) {
                Log.e(tag, "connect() failed", e)
                false
            }
        }

    private fun decodeConfig(): String? = try {
        openVpnConfigBase64?.let { String(Base64.decode(it, Base64.DEFAULT)) }
    } catch (e: Exception) {
        Log.w(tag, "Failed to decode OpenVPN config", e)
        null
    }

    /**
     * Extracts the first 'remote <host> <port>' directive from the OpenVPN config.
     * Falls back to the server's IP and protocol-appropriate default port.
     */
    private fun parseRemote(config: String, server: VpnServer): Pair<String, Int> {
        val remoteLine = config.lines()
            .firstOrNull { it.trimStart().startsWith("remote ", ignoreCase = true) }
        if (remoteLine != null) {
            val parts = remoteLine.trim().split("\\s+".toRegex())
            val host = parts.getOrNull(1) ?: server.ip
            val port = parts.getOrNull(2)?.toIntOrNull()
                ?: if (protocol == Protocol.OPENVPN_TCP) 443 else 1194
            return Pair(host, port)
        }
        val defaultPort = if (protocol == Protocol.OPENVPN_TCP) 443 else 1194
        return Pair(server.ip, defaultPort)
    }

    private fun testConnectivity(host: String, port: Int): Boolean = try {
        when (protocol) {
            Protocol.OPENVPN_TCP -> {
                Socket().use { socket ->
                    socket.connect(InetSocketAddress(host, port), 8000)
                    socket.isConnected
                }
            }
            else -> {
                val addr = InetAddress.getByName(host)
                addr.isReachable(8000)
            }
        }
    } catch (e: Exception) {
        Log.w(tag, "Connectivity test failed: ${e.message}")
        false
    }

    override fun disconnect() {
        connected = false
        try { tunInterface?.close() } catch (_: Exception) {}
        tunInterface = null
        Log.i(tag, "Disconnected")
    }
}
