package com.atom.vpn.ui.screens.diagnostics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atom.vpn.domain.model.ConnectionState
import com.atom.vpn.service.IpLookupService
import com.atom.vpn.service.VpnConnectionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DiagnosticsUiState(
    val isConnected: Boolean = false,
    val publicIp: String? = null,
    val exitIp: String? = null,
    val serverName: String? = null,
    val protocol: String? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class DiagnosticsViewModel @Inject constructor(
    private val connectionManager: VpnConnectionManager,
    private val ipService: IpLookupService
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiagnosticsUiState())
    val uiState: StateFlow<DiagnosticsUiState> = _uiState.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val state = connectionManager.state.value
            val isConnected = state is ConnectionState.Connected
            val publicIp = ipService.getPublicIp()
            _uiState.update {
                it.copy(
                    isConnected = isConnected,
                    publicIp = publicIp,
                    exitIp = if (isConnected) publicIp else null,
                    serverName = state.server?.hostname,
                    protocol = state.server?.protocols?.firstOrNull()?.displayName,
                    isLoading = false
                )
            }
        }
    }
}
