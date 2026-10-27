package com.atom.vpn.ui.screens.diagnostics

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atom.vpn.domain.model.ConnectionState
import com.atom.vpn.service.IpLookupService
import com.atom.vpn.service.VpnConnectionManager
import com.atom.vpn.ui.theme.AtomColors
import com.atom.vpn.ui.theme.AtomShapes
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticsScreen(
    onBack: () -> Unit,
    viewModel: DiagnosticsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = AtomColors.Background,
        topBar = {
            TopAppBar(
                title = { Text("DIAGNOSTICS", color = AtomColors.TextPrimary, fontWeight = FontWeight.Bold, letterSpacing = 3.sp) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = AtomColors.TextSecondary) } },
                actions = {
                    IconButton(onClick = viewModel::refresh) { Icon(Icons.Default.Refresh, null, tint = AtomColors.Cyan) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AtomColors.Surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DiagRow("VPN Status", if (state.isConnected) "Connected" else "Disconnected", if (state.isConnected) AtomColors.SecureGreen else AtomColors.Error)
            DiagRow("Tunnel Interface", if (state.isConnected) "Active" else "None", if (state.isConnected) AtomColors.SecureGreen else AtomColors.TextTertiary)
            DiagRow("DNS Servers", if (state.isConnected) "1.1.1.1, 8.8.8.8 (via tunnel)" else "System default", AtomColors.TextPrimary)
            DiagRow("IPv6", if (state.isConnected) "Blocked (leak protection)" else "Enabled", AtomColors.TextPrimary)
            DiagRow("Public IP", state.publicIp ?: "Fetching…", AtomColors.Cyan)
            DiagRow("Exit IP", state.exitIp ?: if (state.isConnected) "Fetching…" else "N/A (not connected)", AtomColors.Cyan)
            if (state.isConnected) {
                DiagRow("Server", state.serverName ?: "—", AtomColors.TextPrimary)
                DiagRow("Protocol", state.protocol ?: "—", AtomColors.TextPrimary)
            }
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, AtomColors.Warning.copy(0.3f), AtomShapes.Card),
                shape = AtomShapes.Card,
                colors = CardDefaults.cardColors(containerColor = AtomColors.WarningGlow)
            ) {
                Row(Modifier.padding(16.dp)) {
                    Icon(Icons.Default.Warning, null, tint = AtomColors.Warning, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "VPN Gate relay servers are operated by volunteers worldwide. Do not transmit sensitive credentials or personal data through public VPN relays.",
                        style = MaterialTheme.typography.bodySmall, color = AtomColors.Warning
                    )
                }
            }
            if (state.isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = AtomColors.Cyan, trackColor = AtomColors.CardBorder)
        }
    }
}

@Composable
private fun DiagRow(label: String, value: String, valueColor: Color = AtomColors.TextPrimary) {
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, AtomColors.CardBorder, AtomShapes.Medium),
        shape = AtomShapes.Medium,
        colors = CardDefaults.cardColors(containerColor = AtomColors.Card)
    ) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = AtomColors.TextSecondary, modifier = Modifier.weight(1f))
            Text(value, style = MaterialTheme.typography.bodySmall, color = valueColor, fontWeight = FontWeight.Medium)
        }
    }
}
