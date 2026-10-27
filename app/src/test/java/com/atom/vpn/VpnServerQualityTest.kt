package com.atom.vpn

import com.atom.vpn.domain.model.Protocol
import com.atom.vpn.domain.model.QualityTier
import com.atom.vpn.domain.model.VpnServer
import org.junit.Assert.assertEquals
import org.junit.Test

class VpnServerQualityTest {

    private fun server(speedBps: Long) = VpnServer(
        id = "x", hostname = "h", ip = "1.1.1.1",
        countryName = "US", countryCode = "US", operator = "",
        protocols = emptyList(), openVpnConfigBase64 = null,
        sessions = 0, uptimeMs = 0, speedBps = speedBps, pingMs = -1, score = 0
    )

    @Test fun `zero speed is UNKNOWN`()     { assertEquals(QualityTier.UNKNOWN,   server(0L).qualityTier) }
    @Test fun `50 Mbps is EXCELLENT`()      { assertEquals(QualityTier.EXCELLENT, server(50_000_000L).qualityTier) }
    @Test fun `20 Mbps is GOOD`()           { assertEquals(QualityTier.GOOD,      server(20_000_000L).qualityTier) }
    @Test fun `5 Mbps is FAIR`()            { assertEquals(QualityTier.FAIR,      server(5_000_000L).qualityTier) }
    @Test fun `500 Kbps is POOR`()          { assertEquals(QualityTier.POOR,      server(500_000L).qualityTier) }

    @Test fun `flag emoji for JP`() {
        val s = server(0L).copy(countryCode = "JP")
        assertEquals("🇯🇵", s.flagEmoji)
    }

    @Test fun `flag emoji for US`() {
        val s = server(0L).copy(countryCode = "US")
        assertEquals("🇺🇸", s.flagEmoji)
    }
}
