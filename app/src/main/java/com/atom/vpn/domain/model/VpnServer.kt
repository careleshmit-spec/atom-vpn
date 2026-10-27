package com.atom.vpn.domain.model

data class VpnServer(
    val id: String,                        // hostname used as stable ID
    val hostname: String,
    val ip: String,
    val countryName: String,
    val countryCode: String,               // ISO 3166-1 alpha-2
    val operator: String,
    val protocols: List<Protocol>,
    val openVpnConfigBase64: String?,      // raw OpenVPN config (base64) from VPN Gate CSV
    val sessions: Long,
    val uptimeMs: Long,
    val speedBps: Long,                    // bits per second
    val pingMs: Int,                       // -1 if unknown
    val score: Long,
    val isFavorite: Boolean = false,
    val lastCheckedMs: Long = 0L
) {
    /** Quality tier derived from real metrics (not invented). */
    val qualityTier: QualityTier get() = when {
        speedBps <= 0 -> QualityTier.UNKNOWN
        speedBps >= 50_000_000 -> QualityTier.EXCELLENT
        speedBps >= 10_000_000 -> QualityTier.GOOD
        speedBps >= 1_000_000  -> QualityTier.FAIR
        else                   -> QualityTier.POOR
    }

    val flagEmoji: String get() = countryCode
        .uppercase()
        .map { char -> 0x1F1E5 + (char.code - 'A'.code) }
        .joinToString("") { String(Character.toChars(it)) }

    val speedMbps: Float get() = speedBps / 1_000_000f
}

enum class QualityTier { EXCELLENT, GOOD, FAIR, POOR, UNKNOWN }
