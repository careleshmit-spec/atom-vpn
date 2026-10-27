package com.atom.vpn.domain.model

sealed class ConnectionState {
    object Idle : ConnectionState()
    data class Preparing(val server: VpnServer) : ConnectionState()
    data class Connecting(val server: VpnServer, val attempt: Int = 1) : ConnectionState()
    data class Connected(
        val server: VpnServer,
        val connectedAtMs: Long,
        val localIp: String? = null,
        val exitIp: String? = null,
        val uploadBytes: Long = 0L,
        val downloadBytes: Long = 0L,
        val latencyMs: Int = -1
    ) : ConnectionState()
    data class Reconnecting(val server: VpnServer, val attempt: Int) : ConnectionState()
    data class Disconnecting(val server: VpnServer) : ConnectionState()
    data class Failed(val server: VpnServer?, val reason: String) : ConnectionState()

    val isActive: Boolean get() = this is Connected || this is Connecting || this is Reconnecting
    val server: VpnServer? get() = when (this) {
        is Preparing     -> server
        is Connecting    -> server
        is Connected     -> server
        is Reconnecting  -> server
        is Disconnecting -> server
        is Failed        -> server
        else             -> null
    }
}
