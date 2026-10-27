package com.atom.vpn.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.atom.vpn.AtomApplication
import com.atom.vpn.MainActivity
import com.atom.vpn.R
import com.atom.vpn.domain.model.ConnectionState
import com.atom.vpn.domain.model.Protocol
import com.atom.vpn.domain.model.VpnServer
import com.atom.vpn.domain.model.QualityTier
import com.atom.vpn.service.engine.OpenVpnAdapter
import com.atom.vpn.service.engine.ProtocolAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Android VpnService that manages the VPN tunnel lifecycle.
 *
 * Responsibilities:
 * - Foreground service lifecycle.
 * - Delegates actual tunnel work to [ProtocolAdapter] implementations.
 * - Feeds state changes back to [VpnConnectionManager].
 * - Reconnection with bounded exponential backoff.
 */
@AndroidEntryPoint
class AtomVpnService : VpnService() {

    @Inject lateinit var connectionManager: VpnConnectionManager

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var adapter: ProtocolAdapter? = null
    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 5

    // Server being connected to (reconstructed from Intent extras)
    private var currentServer: VpnServer? = null
    private var currentProtocol: Protocol = Protocol.OPENVPN_UDP

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_CONNECT    -> handleConnect(intent)
            ACTION_DISCONNECT -> handleDisconnect()
        }
        return START_STICKY
    }

    private fun handleConnect(intent: Intent) {
        val server = serverFromIntent(intent) ?: run {
            Log.e(TAG, "handleConnect: missing server extras")
            return
        }
        val protocol = runCatching {
            Protocol.valueOf(intent.getStringExtra(EXTRA_PROTOCOL) ?: Protocol.OPENVPN_UDP.name)
        }.getOrDefault(Protocol.OPENVPN_UDP)
        val openVpnConfig = intent.getStringExtra(EXTRA_OPENVPN_CONFIG)

        // Prevent concurrent sessions.
        adapter?.disconnect()
        adapter = null
        reconnectAttempts = 0
        currentServer = server
        currentProtocol = protocol

        startForeground(NOTIFICATION_ID, buildNotification(getString(R.string.notification_connecting)))
        connectionManager.updateState(ConnectionState.Preparing(server))

        serviceScope.launch {
            connectWithBackoff(server, protocol, openVpnConfig)
        }
    }

    private suspend fun connectWithBackoff(
        server: VpnServer,
        protocol: Protocol,
        openVpnConfigBase64: String?
    ) {
        while (reconnectAttempts <= maxReconnectAttempts) {
            if (reconnectAttempts > 0) {
                connectionManager.updateState(ConnectionState.Reconnecting(server, reconnectAttempts))
                val delayMs = minOf(2000L * (1 shl (reconnectAttempts - 1)), 32_000L)
                Log.i(TAG, "Reconnect attempt $reconnectAttempts, waiting ${delayMs}ms")
                delay(delayMs)
            } else {
                connectionManager.updateState(ConnectionState.Connecting(server, 1))
            }

            val currentAdapter = buildAdapter(protocol, openVpnConfigBase64)
            adapter = currentAdapter

            val success = currentAdapter.connect(this@AtomVpnService, server)
            if (success) {
                reconnectAttempts = 0
                connectionManager.updateState(
                    ConnectionState.Connected(
                        server = server,
                        connectedAtMs = System.currentTimeMillis()
                    )
                )
                updateNotification(getString(R.string.notification_connected))
                return
            }

            reconnectAttempts++
        }

        // Exhausted retries.
        connectionManager.updateState(ConnectionState.Failed(server, "Max reconnect attempts reached"))
        updateNotification(getString(R.string.notification_disconnected))
        stopSelf()
    }

    private fun handleDisconnect() {
        val server = currentServer
        if (server != null) {
            connectionManager.updateState(ConnectionState.Disconnecting(server))
        }
        serviceScope.launch {
            adapter?.disconnect()
            adapter = null
            withContext(Dispatchers.Main) {
                connectionManager.updateState(ConnectionState.Idle)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
    }

    private fun buildAdapter(protocol: Protocol, openVpnConfigBase64: String?): ProtocolAdapter {
        return when (protocol) {
            Protocol.OPENVPN_TCP, Protocol.OPENVPN_UDP ->
                OpenVpnAdapter(protocol, openVpnConfigBase64)
            else -> OpenVpnAdapter(Protocol.OPENVPN_UDP, openVpnConfigBase64)
        }
    }

    private fun serverFromIntent(intent: Intent): VpnServer? {
        val id       = intent.getStringExtra(EXTRA_SERVER_ID) ?: return null
        val hostname = intent.getStringExtra(EXTRA_SERVER_HOSTNAME) ?: return null
        val ip       = intent.getStringExtra(EXTRA_SERVER_IP) ?: return null
        val country  = intent.getStringExtra(EXTRA_COUNTRY_NAME) ?: "Unknown"
        return VpnServer(
            id = id, hostname = hostname, ip = ip,
            countryName = country, countryCode = "",
            operator = "", protocols = listOf(),
            openVpnConfigBase64 = null,
            sessions = 0, uptimeMs = 0, speedBps = 0, pingMs = -1, score = 0
        )
    }

    private fun buildNotification(contentText: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, AtomApplication.CHANNEL_VPN)
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentTitle("ATOM VPN")
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(text: String) {
        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(NOTIFICATION_ID, buildNotification(text))
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        adapter?.disconnect()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val TAG = "AtomVpnService"
        const val ACTION_CONNECT    = "com.atom.vpn.action.CONNECT"
        const val ACTION_DISCONNECT = "com.atom.vpn.action.DISCONNECT"
        const val EXTRA_SERVER_ID       = "server_id"
        const val EXTRA_SERVER_HOSTNAME = "server_hostname"
        const val EXTRA_SERVER_IP       = "server_ip"
        const val EXTRA_COUNTRY_NAME    = "country_name"
        const val EXTRA_PROTOCOL        = "protocol"
        const val EXTRA_OPENVPN_CONFIG  = "openvpn_config"
        private const val NOTIFICATION_ID = 1001
    }
}
