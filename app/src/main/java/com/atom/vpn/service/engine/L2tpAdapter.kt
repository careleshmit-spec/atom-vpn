package com.atom.vpn.service.engine

import android.net.VpnService
import android.util.Log
import com.atom.vpn.domain.model.VpnServer

/**
 * L2TP/IPSec protocol adapter stub.
 *
 * Android 12+ (API 32+) removed system support for VpnService-based
 * L2TP/IPSec VPN on new app targets. This adapter exists as a placeholder
 * so the architecture remains extensible. It always returns false to
 * correctly communicate to the UI that this protocol is unavailable.
 *
 * To support L2TP/IPSec:
 * - Target API 28-31 and use the system VPN settings API.
 * - Or integrate a third-party L2TP/IPSec library that works via VpnService.
 */
class L2tpAdapter : ProtocolAdapter {
    override val name = "L2TP/IPSec (unavailable on Android 12+)"
    private val tag = "L2tpAdapter"

    override suspend fun connect(service: VpnService, server: VpnServer): Boolean {
        Log.w(tag, "L2TP/IPSec is not supported on this Android version via VpnService.")
        return false
    }

    override fun disconnect() = Unit
}
