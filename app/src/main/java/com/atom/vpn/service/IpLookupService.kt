package com.atom.vpn.service

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IpLookupService @Inject constructor() {

    private val tag = "IpLookupService"

    /**
     * Returns the device's current public IP address, or null if unavailable.
     * Uses ipify.org — a free, minimal, and privacy-friendly IP service.
     */
    suspend fun getPublicIp(): String? = withContext(Dispatchers.IO) {
        runCatching {
            URL("https://api.ipify.org").readText(Charsets.UTF_8).trim()
                .takeIf { it.isNotBlank() && it.length < 40 } // basic sanity check
        }.onFailure { Log.w(tag, "IP lookup failed", it) }.getOrNull()
    }
}
