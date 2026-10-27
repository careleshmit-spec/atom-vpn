package com.atom.vpn.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.atom.vpn.domain.model.AnimationIntensity
import com.atom.vpn.domain.model.AppSettings
import com.atom.vpn.domain.model.Protocol
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "atom_prefs")

@Singleton
class AtomPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val PREFERRED_PROTOCOL    = stringPreferencesKey("preferred_protocol")
        val AUTO_CONNECT          = booleanPreferencesKey("auto_connect")
        val RECONNECT_ON_DROP     = booleanPreferencesKey("reconnect_on_drop")
        val MAX_RECONNECT         = intPreferencesKey("max_reconnect_attempts")
        val KILL_SWITCH           = booleanPreferencesKey("kill_switch")
        val DNS_LEAK_PROTECTION   = booleanPreferencesKey("dns_leak_protection")
        val IPV6_LEAK_PROTECTION  = booleanPreferencesKey("ipv6_leak_protection")
        val SHOW_NOTIFICATIONS    = booleanPreferencesKey("show_notifications")
        val ANIMATION_INTENSITY   = stringPreferencesKey("animation_intensity")
        val ONBOARDING_DONE       = booleanPreferencesKey("onboarding_completed")
    }

    val settings: Flow<AppSettings> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            AppSettings(
                preferredProtocol        = Protocol.valueOf(prefs[Keys.PREFERRED_PROTOCOL] ?: Protocol.OPENVPN_UDP.name),
                autoConnect              = prefs[Keys.AUTO_CONNECT] ?: false,
                reconnectOnDrop          = prefs[Keys.RECONNECT_ON_DROP] ?: true,
                maxReconnectAttempts     = prefs[Keys.MAX_RECONNECT] ?: 5,
                enableKillSwitch         = prefs[Keys.KILL_SWITCH] ?: false,
                enableDnsLeakProtection  = prefs[Keys.DNS_LEAK_PROTECTION] ?: true,
                enableIpv6LeakProtection = prefs[Keys.IPV6_LEAK_PROTECTION] ?: true,
                showNotifications        = prefs[Keys.SHOW_NOTIFICATIONS] ?: true,
                animationIntensity       = AnimationIntensity.valueOf(prefs[Keys.ANIMATION_INTENSITY] ?: AnimationIntensity.NORMAL.name),
                onboardingCompleted      = prefs[Keys.ONBOARDING_DONE] ?: false
            )
        }

    suspend fun updateSettings(settings: AppSettings) {
        context.dataStore.edit { prefs ->
            prefs[Keys.PREFERRED_PROTOCOL]   = settings.preferredProtocol.name
            prefs[Keys.AUTO_CONNECT]         = settings.autoConnect
            prefs[Keys.RECONNECT_ON_DROP]    = settings.reconnectOnDrop
            prefs[Keys.MAX_RECONNECT]        = settings.maxReconnectAttempts
            prefs[Keys.KILL_SWITCH]          = settings.enableKillSwitch
            prefs[Keys.DNS_LEAK_PROTECTION]  = settings.enableDnsLeakProtection
            prefs[Keys.IPV6_LEAK_PROTECTION] = settings.enableIpv6LeakProtection
            prefs[Keys.SHOW_NOTIFICATIONS]   = settings.showNotifications
            prefs[Keys.ANIMATION_INTENSITY]  = settings.animationIntensity.name
            prefs[Keys.ONBOARDING_DONE]      = settings.onboardingCompleted
        }
    }
}
