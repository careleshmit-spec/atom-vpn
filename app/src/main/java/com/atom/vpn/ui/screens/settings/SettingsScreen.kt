package com.atom.vpn.ui.screens.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atom.vpn.domain.model.AnimationIntensity
import com.atom.vpn.domain.model.Protocol
import com.atom.vpn.ui.theme.AtomColors
import com.atom.vpn.ui.theme.AtomShapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = AtomColors.Background,
        topBar = {
            TopAppBar(
                title = { Text("SETTINGS", color = AtomColors.TextPrimary, fontWeight = FontWeight.Bold, letterSpacing = 3.sp) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = AtomColors.TextSecondary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AtomColors.Surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Protocol
            SectionHeader("PROTOCOL")
            Protocol.supported().forEach { proto ->
                SettingRadioRow(
                    label = proto.displayName,
                    selected = settings?.preferredProtocol == proto,
                    onClick = { viewModel.setProtocol(proto) }
                )
            }
            Spacer(Modifier.height(8.dp))
            SectionHeader("CONNECTION")
            SettingToggleRow("Auto-connect on launch", settings?.autoConnect == true) { viewModel.setAutoConnect(it) }
            SettingToggleRow("Reconnect on drop", settings?.reconnectOnDrop == true) { viewModel.setReconnectOnDrop(it) }
            Spacer(Modifier.height(8.dp))
            SectionHeader("SECURITY")
            SettingToggleRow("DNS Leak Protection", settings?.enableDnsLeakProtection == true) { viewModel.setDnsLeakProtection(it) }
            SettingToggleRow("IPv6 Leak Protection", settings?.enableIpv6LeakProtection == true) { viewModel.setIpv6LeakProtection(it) }
            SettingToggleRow("Kill Switch (experimental)", settings?.enableKillSwitch == true) { viewModel.setKillSwitch(it) }
            Spacer(Modifier.height(8.dp))
            SectionHeader("APP")
            SettingToggleRow("Notifications", settings?.showNotifications == true) { viewModel.setNotifications(it) }
            AnimationIntensity.values().let { intensities ->
                SectionHeader("ANIMATION")
                intensities.forEach { intensity ->
                    SettingRadioRow(
                        label = intensity.name.lowercase().replaceFirstChar { it.uppercase() },
                        selected = settings?.animationIntensity == intensity,
                        onClick = { viewModel.setAnimationIntensity(intensity) }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            // About
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, AtomColors.CardBorder, AtomShapes.Card),
                shape = AtomShapes.Card,
                colors = CardDefaults.cardColors(containerColor = AtomColors.Card)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("ATOM VPN", style = MaterialTheme.typography.titleMedium, color = AtomColors.Cyan, fontWeight = FontWeight.Bold)
                    Text("Version 1.0.0", style = MaterialTheme.typography.bodySmall, color = AtomColors.TextSecondary)
                    Spacer(Modifier.height(8.dp))
                    Text("Public VPN servers are operated by volunteers and are NOT vetted by ATOM. Do not transmit sensitive data over public VPN relays.", style = MaterialTheme.typography.bodySmall, color = AtomColors.TextTertiary)
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.labelMedium, color = AtomColors.Cyan, letterSpacing = 2.sp, modifier = Modifier.padding(vertical = 4.dp))
}

@Composable
private fun SettingToggleRow(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = AtomColors.TextPrimary, modifier = Modifier.weight(1f))
        Switch(
            checked = checked, onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(checkedThumbColor = AtomColors.Cyan, checkedTrackColor = AtomColors.CyanGlow, uncheckedThumbColor = AtomColors.TextTertiary, uncheckedTrackColor = AtomColors.SurfaceVariant)
        )
    }
}

@Composable
private fun SettingRadioRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = AtomColors.TextPrimary, modifier = Modifier.weight(1f))
        RadioButton(selected = selected, onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = AtomColors.Cyan, unselectedColor = AtomColors.TextTertiary))
    }
}
