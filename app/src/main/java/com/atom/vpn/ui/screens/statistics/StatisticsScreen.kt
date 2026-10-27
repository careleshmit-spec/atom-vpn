package com.atom.vpn.ui.screens.statistics

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atom.vpn.domain.model.ConnectionState
import com.atom.vpn.service.VpnConnectionManager
import com.atom.vpn.ui.components.StatCard
import com.atom.vpn.ui.theme.AtomColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onBack: () -> Unit,
    connectionManager: VpnConnectionManager = androidx.hilt.navigation.compose.hiltViewModel<StatisticsViewModel>().let {
        it.connectionManager
    }
) {
    val vm: StatisticsViewModel = hiltViewModel()
    val state by vm.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = AtomColors.Background,
        topBar = {
            TopAppBar(
                title = { Text("STATISTICS", color = AtomColors.TextPrimary, fontWeight = FontWeight.Bold, letterSpacing = 3.sp) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = AtomColors.TextSecondary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AtomColors.Surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!state.isConnected) {
                Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SignalWifiOff, null, tint = AtomColors.TextTertiary, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("Connect to a server to see live statistics", color = AtomColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                Text("LIVE SESSION", style = MaterialTheme.typography.labelLarge, color = AtomColors.Cyan, letterSpacing = 2.sp)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("DURATION", state.duration, Icons.Default.Timer, AtomColors.Purple, Modifier.weight(1f))
                    StatCard("LATENCY", state.latency, Icons.Default.Speed, AtomColors.Cyan, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("UPLOAD", state.uploadRate, Icons.Default.Upload, AtomColors.Magenta, Modifier.weight(1f))
                    StatCard("DOWNLOAD", state.downloadRate, Icons.Default.Download, AtomColors.Cyan, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("DATA SENT", state.uploadTotal, Icons.Default.CloudUpload, AtomColors.Purple, Modifier.weight(1f))
                    StatCard("DATA RECEIVED", state.downloadTotal, Icons.Default.CloudDownload, AtomColors.Blue, Modifier.weight(1f))
                }
            }
        }
    }
}
