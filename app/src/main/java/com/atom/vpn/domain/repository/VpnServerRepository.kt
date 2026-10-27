package com.atom.vpn.domain.repository

import com.atom.vpn.domain.model.VpnServer
import kotlinx.coroutines.flow.Flow

interface VpnServerRepository {
    /** Emits the cached list immediately, then fetches fresh data from VPN Gate. */
    fun getServers(forceRefresh: Boolean = false): Flow<Result<List<VpnServer>>>
    suspend fun getServer(id: String): VpnServer?
    suspend fun refreshServers(): Result<List<VpnServer>>
}
