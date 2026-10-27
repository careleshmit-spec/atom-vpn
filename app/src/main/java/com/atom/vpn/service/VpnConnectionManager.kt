package com.atom.vpn.service

import android.content.Context
import android.content.Intent
import com.atom.vpn.domain.model.ConnectionState
import com.atom.vpn.domain.model.VpnServer
import com.atom.vpn.domain.model.Protocol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton that owns the authoritative [ConnectionState] and dispatches
 * connect/disconnect commands to [AtomVpnService].
 */
@Singleton
class VpnConnectionManager @Inject constructor() {

    private val _state = MutableStateFlow<ConnectionState>(ConnectionState.Idle)
    val state: StateFlow<ConnectionState> = _state.asStateFlow()

    fun updateState(newState: ConnectionState) {
        _state.value = newState
    }

    fun connect(context: Context, server: VpnServer, protocol: Protocol) {
        val intent = Intent(context, AtomVpnService::class.java).apply {
            action = AtomVpnService.ACTION_CONNECT
            putExtra(AtomVpnService.EXTRA_SERVER_ID, server.id)
            putExtra(AtomVpnService.EXTRA_SERVER_HOSTNAME, server.hostname)
            putExtra(AtomVpnService.EXTRA_SERVER_IP, server.ip)
            putExtra(AtomVpnService.EXTRA_COUNTRY_NAME, server.countryName)
            putExtra(AtomVpnService.EXTRA_PROTOCOL, protocol.name)
            putExtra(AtomVpnService.EXTRA_OPENVPN_CONFIG, server.openVpnConfigBase64)
        }
        context.startForegroundService(intent)
    }

    fun disconnect(context: Context) {
        val intent = Intent(context, AtomVpnService::class.java).apply {
            action = AtomVpnService.ACTION_DISCONNECT
        }
        context.startService(intent)
    }
}
