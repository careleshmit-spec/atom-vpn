package com.atom.vpn

import com.atom.vpn.domain.model.ConnectionState
import com.atom.vpn.domain.model.Protocol
import com.atom.vpn.domain.model.VpnServer
import org.junit.Assert.*
import org.junit.Test

class ConnectionStateTest {

    private val dummyServer = VpnServer(
        id = "test", hostname = "test.vpn", ip = "1.2.3.4",
        countryName = "Japan", countryCode = "JP", operator = "",
        protocols = listOf(Protocol.OPENVPN_UDP),
        openVpnConfigBase64 = null,
        sessions = 10, uptimeMs = 9999, speedBps = 10_000_000, pingMs = 30, score = 9999
    )

    @Test
    fun `Idle is not active`() {
        assertFalse(ConnectionState.Idle.isActive)
    }

    @Test
    fun `Connecting is active`() {
        assertTrue(ConnectionState.Connecting(dummyServer).isActive)
    }

    @Test
    fun `Connected is active`() {
        assertTrue(ConnectionState.Connected(dummyServer, System.currentTimeMillis()).isActive)
    }

    @Test
    fun `Reconnecting is active`() {
        assertTrue(ConnectionState.Reconnecting(dummyServer, 2).isActive)
    }

    @Test
    fun `Disconnecting is not active`() {
        assertFalse(ConnectionState.Disconnecting(dummyServer).isActive)
    }

    @Test
    fun `Failed is not active`() {
        assertFalse(ConnectionState.Failed(dummyServer, "error").isActive)
    }

    @Test
    fun `server property returns correct server for Connected`() {
        val state = ConnectionState.Connected(dummyServer, 0L)
        assertEquals(dummyServer, state.server)
    }

    @Test
    fun `server property returns null for Idle`() {
        assertNull(ConnectionState.Idle.server)
    }
}
