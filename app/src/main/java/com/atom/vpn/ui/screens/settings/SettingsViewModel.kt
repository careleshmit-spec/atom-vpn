package com.atom.vpn.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atom.vpn.domain.model.AnimationIntensity
import com.atom.vpn.domain.model.AppSettings
import com.atom.vpn.domain.model.Protocol
import com.atom.vpn.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings?> = repo.getSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private fun update(block: AppSettings.() -> AppSettings) {
        viewModelScope.launch { settings.value?.let { repo.updateSettings(it.block()) } }
    }

    fun setProtocol(p: Protocol) = update { copy(preferredProtocol = p) }
    fun setAutoConnect(v: Boolean) = update { copy(autoConnect = v) }
    fun setReconnectOnDrop(v: Boolean) = update { copy(reconnectOnDrop = v) }
    fun setDnsLeakProtection(v: Boolean) = update { copy(enableDnsLeakProtection = v) }
    fun setIpv6LeakProtection(v: Boolean) = update { copy(enableIpv6LeakProtection = v) }
    fun setKillSwitch(v: Boolean) = update { copy(enableKillSwitch = v) }
    fun setNotifications(v: Boolean) = update { copy(showNotifications = v) }
    fun setAnimationIntensity(v: AnimationIntensity) = update { copy(animationIntensity = v) }
}
