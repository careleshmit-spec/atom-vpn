package com.atom.vpn.service.engine

import android.net.VpnService
import com.atom.vpn.domain.model.VpnServer

/**
 * Abstraction over a VPN protocol implementation.
 * Each adapter is responsible for establishing, maintaining, and tearing
 * down a single VPN tunnel for a given protocol.
 */
interface ProtocolAdapter {
    /**
     * Establishes the VPN tunnel synchronously (runs on IO dispatcher).
     * Returns true if the tunnel was successfully established, false on failure.
     */
    suspend fun connect(service: VpnService, server: VpnServer): Boolean

    /**
     * Tears down the tunnel. Must be safe to call multiple times and from any thread.
     */
    fun disconnect()

    /**
     * Human-readable name of this adapter, for logging and diagnostics.
     */
    val name: String
}
