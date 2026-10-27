package com.atom.vpn.domain.model

enum class Protocol(val displayName: String) {
    OPENVPN_TCP("OpenVPN TCP"),
    OPENVPN_UDP("OpenVPN UDP"),
    L2TP_IPSEC("L2TP/IPSec"),
    SOFTETHER("SoftEther");

    companion object {
        fun supported(): List<Protocol> = listOf(OPENVPN_TCP, OPENVPN_UDP)
    }
}
