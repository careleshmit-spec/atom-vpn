package com.atom.vpn.data.remote.parser

import com.atom.vpn.domain.model.Protocol
import com.atom.vpn.domain.model.VpnServer
import android.util.Log

object VpnGateCsvParser {

    private const val TAG = "VpnGateCsvParser"

    /**
     * Parses the VPN Gate CSV response.
     *
     * Expected header (line starting with '#'):
     * #HostName,IP,Score,Ping,Speed,CountryLong,CountryShort,NumVpnSessions,
     * Uptime,TotalUsers,TotalTraffic,LogType,Operator,Message,OpenVPN_ConfigData_Base64
     *
     * All data lines follow. Lines starting with '*' or '#' are skipped.
     */
    fun parse(csv: String): List<VpnServer> {
        val lines = csv.lines()
        // Find the header line
        val headerLine = lines.firstOrNull { it.startsWith("#") && it.contains("HostName") }
            ?: return emptyList()

        val headers = headerLine.removePrefix("#").split(",")
            .map { it.trim() }

        fun idx(name: String): Int = headers.indexOfFirst {
            it.equals(name, ignoreCase = true)
        }

        val iHostName       = idx("HostName")
        val iIp             = idx("IP")
        val iScore          = idx("Score")
        val iPing           = idx("Ping")
        val iSpeed          = idx("Speed")
        val iCountryLong    = idx("CountryLong")
        val iCountryShort   = idx("CountryShort")
        val iSessions       = idx("NumVpnSessions")
        val iUptime         = idx("Uptime")
        val iOperator       = idx("Operator")
        val iOpenVpnConfig  = idx("OpenVPN_ConfigData_Base64")
        val iSupportedL2tp  = headers.indexOfFirst { it.equals("SupportL2TP", ignoreCase = true) }

        val servers = mutableListOf<VpnServer>()

        for (line in lines) {
            if (line.isBlank() || line.startsWith("*") || line.startsWith("#")) continue

            val cols = line.split(",")
            if (cols.size < headers.size) continue

            fun col(i: Int): String = if (i in cols.indices) cols[i].trim() else ""

            try {
                val hostname    = col(iHostName)
                val ip          = col(iIp)
                val countryLong = col(iCountryLong)
                val countryShort = col(iCountryShort)
                if (hostname.isBlank() || ip.isBlank()) continue

                val openVpnConfigB64 = col(iOpenVpnConfig).takeIf { it.isNotBlank() }

                // Detect supported protocols from real CSV data.
                val protocols = mutableListOf<Protocol>()
                if (openVpnConfigB64 != null) {
                    // Check config to determine TCP vs UDP
                    val decoded = try {
                        String(android.util.Base64.decode(openVpnConfigB64, android.util.Base64.DEFAULT))
                    } catch (e: Exception) { "" }
                    when {
                        decoded.contains("proto tcp", ignoreCase = true)  -> protocols.add(Protocol.OPENVPN_TCP)
                        decoded.contains("proto udp", ignoreCase = true)  -> protocols.add(Protocol.OPENVPN_UDP)
                        else -> { protocols.add(Protocol.OPENVPN_TCP); protocols.add(Protocol.OPENVPN_UDP) }
                    }
                }
                if (iSupportedL2tp >= 0 && col(iSupportedL2tp) == "1") {
                    // L2TP is listed as supported; we note it but it cannot be established
                    // on Android 12+ via VpnService. See README for details.
                    protocols.add(Protocol.L2TP_IPSEC)
                }

                servers.add(VpnServer(
                    id               = hostname,
                    hostname         = hostname,
                    ip               = ip,
                    countryName      = countryLong,
                    countryCode      = countryShort,
                    operator         = col(iOperator),
                    protocols        = protocols,
                    openVpnConfigBase64 = openVpnConfigB64,
                    sessions         = col(iSessions).toLongOrNull() ?: 0L,
                    uptimeMs         = col(iUptime).toLongOrNull() ?: 0L,
                    speedBps         = col(iSpeed).toLongOrNull() ?: 0L,
                    pingMs           = col(iPing).toIntOrNull() ?: -1,
                    score            = col(iScore).toLongOrNull() ?: 0L
                ))
            } catch (e: Exception) {
                Log.w(TAG, "Skipping malformed row: $line", e)
            }
        }
        return servers
    }
}
