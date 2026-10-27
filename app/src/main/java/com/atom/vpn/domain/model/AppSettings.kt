package com.atom.vpn.domain.model

data class AppSettings(
    val preferredProtocol: Protocol = Protocol.OPENVPN_UDP,
    val autoConnect: Boolean = false,
    val reconnectOnDrop: Boolean = true,
    val maxReconnectAttempts: Int = 5,
    val enableKillSwitch: Boolean = false,
    val enableDnsLeakProtection: Boolean = true,
    val enableIpv6LeakProtection: Boolean = true,
    val showNotifications: Boolean = true,
    val animationIntensity: AnimationIntensity = AnimationIntensity.NORMAL,
    val onboardingCompleted: Boolean = false
)

enum class AnimationIntensity { REDUCED, NORMAL, FULL }
