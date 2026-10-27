package com.atom.vpn

import com.atom.vpn.data.remote.parser.VpnGateCsvParser
import com.atom.vpn.domain.model.Protocol
import org.junit.Assert.*
import org.junit.Test

class VpnGateCsvParserTest {

    private val sampleCsv = """
        *vpn_servers
        #HostName,IP,Score,Ping,Speed,CountryLong,CountryShort,NumVpnSessions,Uptime,TotalUsers,TotalTraffic,LogType,Operator,Message,OpenVPN_ConfigData_Base64
        vpn123.example.com,1.2.3.4,123456,30,50000000,Japan,JP,100,9999999,1000,99999,2weeks,SomeOperator,Hello,
        vpn456.example.com,5.6.7.8,654321,80,10000000,United States,US,50,8888888,500,55555,2weeks,OtherOperator,World,
        *
    """.trimIndent()

    @Test
    fun `parse returns empty list for empty csv`() {
        val result = VpnGateCsvParser.parse("")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `parse returns empty list when no header found`() {
        val result = VpnGateCsvParser.parse("some,data,without,header")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `parse extracts correct server count`() {
        val result = VpnGateCsvParser.parse(sampleCsv)
        assertEquals(2, result.size)
    }

    @Test
    fun `parse extracts correct hostname`() {
        val result = VpnGateCsvParser.parse(sampleCsv)
        assertEquals("vpn123.example.com", result[0].hostname)
        assertEquals("vpn456.example.com", result[1].hostname)
    }

    @Test
    fun `parse extracts correct country`() {
        val result = VpnGateCsvParser.parse(sampleCsv)
        assertEquals("Japan", result[0].countryName)
        assertEquals("JP", result[0].countryCode)
        assertEquals("United States", result[1].countryName)
        assertEquals("US", result[1].countryCode)
    }

    @Test
    fun `parse extracts correct speed`() {
        val result = VpnGateCsvParser.parse(sampleCsv)
        assertEquals(50_000_000L, result[0].speedBps)
        assertEquals(10_000_000L, result[1].speedBps)
    }

    @Test
    fun `parse extracts correct ping`() {
        val result = VpnGateCsvParser.parse(sampleCsv)
        assertEquals(30, result[0].pingMs)
        assertEquals(80, result[1].pingMs)
    }

    @Test
    fun `parse skips lines starting with asterisk`() {
        val csv = """
            *vpn_servers
            #HostName,IP,Score,Ping,Speed,CountryLong,CountryShort,NumVpnSessions,Uptime,TotalUsers,TotalTraffic,LogType,Operator,Message,OpenVPN_ConfigData_Base64
            valid.host.com,1.1.1.1,100,20,5000000,Germany,DE,10,1000000,100,10000,2weeks,Op,Msg,
            *end
        """.trimIndent()
        val result = VpnGateCsvParser.parse(csv)
        assertEquals(1, result.size)
    }

    @Test
    fun `parse handles missing optional fields gracefully`() {
        val result = VpnGateCsvParser.parse(sampleCsv)
        // No OpenVPN config in sample — protocols list should be empty
        assertTrue(result[0].protocols.isEmpty())
    }
}
