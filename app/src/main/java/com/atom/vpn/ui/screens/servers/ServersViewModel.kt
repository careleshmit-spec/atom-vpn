package com.atom.vpn.ui.screens.servers

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atom.vpn.domain.model.Protocol
import com.atom.vpn.domain.model.QualityTier
import com.atom.vpn.domain.model.VpnServer
import com.atom.vpn.domain.repository.FavoriteRepository
import com.atom.vpn.domain.repository.VpnServerRepository
import com.atom.vpn.service.VpnConnectionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortOption(val label: String) {
    SPEED("Speed"), PING("Ping"), COUNTRY("Country"), SCORE("Score")
}

data class ServersUiState(
    val servers: List<VpnServer> = emptyList(),
    val filteredServers: List<VpnServer> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val query: String = "",
    val selectedProtocol: Protocol? = null,
    val selectedQuality: QualityTier? = null,
    val sortOption: SortOption = SortOption.SCORE,
    val lastRefreshedMs: Long = 0L
)

@HiltViewModel
class ServersViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val serverRepository: VpnServerRepository,
    private val favoriteRepository: FavoriteRepository,
    private val connectionManager: VpnConnectionManager
) : ViewModel() {

    private val _state = MutableStateFlow(ServersUiState())
    val state: StateFlow<ServersUiState> = _state.asStateFlow()

    init {
        loadServers()
        observeFavorites()
    }

    private fun loadServers(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            serverRepository.getServers(forceRefresh).collect { result ->
                result.fold(
                    onSuccess = { servers ->
                        _state.update { s ->
                            val withFav = servers.map { it.copy(isFavorite = it.id in s.favoriteIds) }
                            s.copy(
                                servers = withFav,
                                isLoading = false,
                                lastRefreshedMs = System.currentTimeMillis()
                            ).withFilter()
                        }
                    },
                    onFailure = { e ->
                        _state.update { it.copy(isLoading = false, error = e.message ?: "Failed to load servers") }
                    }
                )
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoriteRepository.getFavorites().collect { ids ->
                val idSet = ids.toSet()
                _state.update { s ->
                    s.copy(
                        favoriteIds = idSet,
                        servers = s.servers.map { it.copy(isFavorite = it.id in idSet) }
                    ).withFilter()
                }
            }
        }
    }

    fun onQueryChanged(q: String) = _state.update { it.copy(query = q).withFilter() }
    fun onSortChanged(sort: SortOption) = _state.update { it.copy(sortOption = sort).withFilter() }
    fun onProtocolFilter(p: Protocol?) = _state.update { it.copy(selectedProtocol = p).withFilter() }
    fun onQualityFilter(q: QualityTier?) = _state.update { it.copy(selectedQuality = q).withFilter() }
    fun onRefresh() = loadServers(forceRefresh = true)

    fun onFavoriteToggle(server: VpnServer) {
        viewModelScope.launch {
            if (server.isFavorite) favoriteRepository.removeFavorite(server.id)
            else favoriteRepository.addFavorite(server.id)
        }
    }

    fun onConnect(server: VpnServer, protocol: Protocol) {
        connectionManager.connect(context, server, protocol)
    }

    private fun ServersUiState.withFilter(): ServersUiState {
        var filtered = servers
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            filtered = filtered.filter {
                it.countryName.lowercase().contains(q) ||
                it.hostname.lowercase().contains(q) ||
                it.ip.contains(q)
            }
        }
        selectedProtocol?.let { proto -> filtered = filtered.filter { proto in it.protocols } }
        selectedQuality?.let { tier -> filtered = filtered.filter { it.qualityTier == tier } }
        filtered = when (sortOption) {
            SortOption.SPEED   -> filtered.sortedByDescending { it.speedBps }
            SortOption.PING    -> filtered.sortedBy { if (it.pingMs < 0) Int.MAX_VALUE else it.pingMs }
            SortOption.COUNTRY -> filtered.sortedBy { it.countryName }
            SortOption.SCORE   -> filtered.sortedByDescending { it.score }
        }
        return copy(filteredServers = filtered)
    }
}
