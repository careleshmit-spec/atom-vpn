package com.atom.vpn.domain.model

data class SessionStatistics(
    val uploadBytes: Long = 0L,
    val downloadBytes: Long = 0L,
    val durationMs: Long = 0L,
    val uploadBps: Long = 0L,
    val downloadBps: Long = 0L,
    val latencyMs: Int = -1
) {
    val uploadMb: Float get() = uploadBytes / 1_048_576f
    val downloadMb: Float get() = downloadBytes / 1_048_576f
    val durationSeconds: Long get() = durationMs / 1000
    val durationFormatted: String get() {
        val h = durationSeconds / 3600
        val m = (durationSeconds % 3600) / 60
        val s = durationSeconds % 60
        return if (h > 0) "%02d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
    }
}

data class SpeedSample(val timestampMs: Long, val uploadBps: Long, val downloadBps: Long)
