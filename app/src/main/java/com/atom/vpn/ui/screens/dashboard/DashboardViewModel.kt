package com.atom.vpn.ui.screens.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atom.vpn.domain.model.*
import com.atom.vpn.domain.repository.FavoriteRepository
import com.atom.vpn.domain.repository.VpnServerRepository
import com.atom.vpn.service.IpLookupService
import com.atom.vpn.service.VpnConnectionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class DashboardUiState(
    val connectionState: ConnectionState = ConnectionState.Idle,
    val selectedServer: VpnServer? = null,
    val publicIp: String? = null,
    val exitIp: String? = null,
    val isLoadingIp: Boolean = false,
    val sessionStats: SessionStatistics = SessionStatistics(),
    val speedHistory: List<SpeedSample> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val connectionManager: VpnConnectionManager,
    private val serverRepository: VpnServerRepository,
    private val favoriteRepository: FavoriteRepository,
    private val ipLookupService: IpLookupService
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var statsJob: Job? = null
    private var ipJob: Job? = null
    private var connectedAtMs = 0L
    private var lastUpload = 0L
    private var lastDownload = 0L
    private var lastStatTime = 0L
    private val speedHistory = mutableListOf<SpeedSample>()

    init {
        observeConnectionState()
        fetchPublicIp()
    }

    private fun observeConnectionState() {
        connectionManager.state
            .onEach { state ->
                _uiState.update { it.copy(connectionState = state, selectedServer = state.server ?: it.selectedServer) }
                when (state) {
                    is ConnectionState.Connected -> {
                        connectedAtMs = state.connectedAtMs
                        lastStatTime = System.currentTimeMillis()
                        startStatsPolling(state)
                        fetchExitIp()
                    }
                    is ConnectionState.Idle, is ConnectionState.Failed -> {
                        stopStatsPolling()
                        clearExitIp()
                    }
                    else -> Unit
                }
            }
            .launchIn(viewModelScope)
    }

    private fun startStatsPolling(initial: ConnectionState.Connected) {
        stopStatsPolling()
        statsJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                val now = System.currentTimeMillis()
                val duration = now - connectedAtMs
                // In a real integration these would come from the VPN adapter.
                // Here we track simulated deltas to demonstrate the data model.
                val currentState = connectionManager.state.value
                if (currentState is ConnectionState.Connected) {
                    val elapsed = (now - lastStatTime).coerceAtLeast(1L)
                    val uploadBps = 0L   // replace with adapter.stats.uploadBps
                    val downloadBps = 0L // replace with adapter.stats.downloadBps
                    lastStatTime = now

                    val sample = SpeedSample(now, uploadBps, downloadBps)
                    if (speedHistory.size >= 60) speedHistory.removeAt(0)
                    speedHistory.add(sample)

                    _uiState.update {
                        it.copy(
                            sessionStats = SessionStatistics(
                                durationMs = duration,
                                uploadBps = uploadBps,
                                downloadBps = downloadBps
                            ),
                            speedHistory = speedHistory.toList()
                        )
                    }
                }
            }
        }
    }

    private fun stopStatsPolling() {
        statsJob?.cancel()
        statsJob = null
        speedHistory.clear()
    }

    private fun fetchPublicIp() {
        ipJob?.cancel()
        ipJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingIp = true) }
            val ip = ipLookupService.getPublicIp()
            _uiState.update { it.copy(publicIp = ip, isLoadingIp = false) }
        }
    }

    private fun fetchExitIp() {
        viewModelScope.launch {
            delay(2000) // Wait for tunnel to establish
            val ip = ipLookupService.getPublicIp()
            _uiState.update { it.copy(exitIp = ip) }
        }
    }

    private fun clearExitIp() {
        _uiState.update { it.copy(exitIp = null) }
        fetchPublicIp()
    }

    fun onConnectClicked(protocol: Protocol = Protocol.OPENVPN_UDP) {
        val state = _uiState.value.connectionState
        val server = _uiState.value.selectedServer
        when {
            state.isActive -> connectionManager.disconnect(context)
            server != null -> connectionManager.connect(context, server, protocol)
        }
    }

    fun onServerSelected(server: VpnServer) {
        _uiState.update { it.copy(selectedServer = server) }
    }

    override fun onCleared() {
        super.onCleared()
        stopStatsPolling()
    }
}
