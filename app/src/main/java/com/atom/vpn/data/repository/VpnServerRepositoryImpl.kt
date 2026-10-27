package com.atom.vpn.data.repository

import com.atom.vpn.data.remote.api.VpnGateApi
import com.atom.vpn.data.remote.parser.VpnGateCsvParser
import com.atom.vpn.domain.model.VpnServer
import com.atom.vpn.domain.repository.VpnServerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VpnServerRepositoryImpl @Inject constructor(
    private val api: VpnGateApi
) : VpnServerRepository {

    @Volatile private var cachedServers: List<VpnServer> = emptyList()
    @Volatile private var lastFetchMs: Long = 0L
    private val minRefreshIntervalMs = 60_000L // respect VPN Gate usage terms

    override fun getServers(forceRefresh: Boolean): Flow<Result<List<VpnServer>>> = flow {
        if (cachedServers.isNotEmpty()) emit(Result.success(cachedServers))
        val shouldFetch = forceRefresh || cachedServers.isEmpty() ||
                (System.currentTimeMillis() - lastFetchMs) > minRefreshIntervalMs
        if (shouldFetch) {
            val result = refreshServers()
            emit(result)
        }
    }

    override suspend fun getServer(id: String): VpnServer? =
        cachedServers.firstOrNull { it.id == id }

    override suspend fun refreshServers(): Result<List<VpnServer>> = runCatching {
        val csv = api.getServersCsv()
        val servers = VpnGateCsvParser.parse(csv)
        cachedServers = servers
        lastFetchMs = System.currentTimeMillis()
        servers
    }
}
