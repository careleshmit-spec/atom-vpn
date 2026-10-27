package com.atom.vpn.service.engine

import android.net.VpnService
import android.util.Log
import com.atom.vpn.domain.model.VpnServer

/**
 * SoftEther VPN protocol adapter stub.
 *
 * There is no maintained, open-source SoftEther Android client library
 * compatible with modern Android. This stub allows the protocol to appear
 * in server metadata without claiming it can actually connect.
 *
 * If a compatible library becomes available, implement connect() here and
 * return true only when a genuine tunnel is established.
 */
class SoftEtherAdapter : ProtocolAdapter {
    override val name = "SoftEther (library not available)"
    private val tag = "SoftEtherAdapter"

    override suspend fun connect(service: VpnService, server: VpnServer): Boolean {
        Log.w(tag, "SoftEther protocol library is not available.")
        return false
    }

    override fun disconnect() = Unit
}
