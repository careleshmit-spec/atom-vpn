package com.atom.vpn.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atom.vpn.domain.model.ConnectionState
import com.atom.vpn.service.VpnConnectionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class StatisticsUiState(
    val isConnected: Boolean = false,
    val duration: String? = null,
    val latency: String? = null,
    val uploadRate: String? = null,
    val downloadRate: String? = null,
    val uploadTotal: String? = null,
    val downloadTotal: String? = null
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    val connectionManager: VpnConnectionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        connectionManager.state.onEach { state ->
            when (state) {
                is ConnectionState.Connected -> {
                    val elapsed = System.currentTimeMillis() - state.connectedAtMs
                    val h = elapsed / 3600000; val m = (elapsed % 3600000) / 60000; val s = (elapsed % 60000) / 1000
                    _uiState.update {
                        it.copy(
                            isConnected = true,
                            duration = if (h > 0) "%02d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s),
                            latency = if (state.latencyMs > 0) "${state.latencyMs}ms" else null,
                            uploadRate = formatBps(0L),
                            downloadRate = formatBps(0L),
                            uploadTotal = formatBytes(state.uploadBytes),
                            downloadTotal = formatBytes(state.downloadBytes)
                        )
                    }
                }
                else -> _uiState.update { StatisticsUiState() }
            }
        }.launchIn(viewModelScope)
    }

    private fun formatBps(bps: Long) = when {
        bps < 1_000 -> "$bps B/s"
        bps < 1_000_000 -> "%.1f KB/s".format(bps / 1_000f)
        else -> "%.1f MB/s".format(bps / 1_000_000f)
    }
    private fun formatBytes(b: Long) = when {
        b < 1024 -> "$b B"
        b < 1_048_576 -> "%.1f KB".format(b / 1024f)
        else -> "%.1f MB".format(b / 1_048_576f)
    }
}
