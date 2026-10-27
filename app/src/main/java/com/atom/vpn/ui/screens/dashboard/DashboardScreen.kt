package com.atom.vpn.ui.screens.dashboard

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atom.vpn.domain.model.ConnectionState
import com.atom.vpn.domain.model.Protocol
import com.atom.vpn.ui.components.*
import com.atom.vpn.ui.navigation.Screen
import com.atom.vpn.ui.theme.AtomColors
import com.atom.vpn.ui.theme.AtomShapes

@Composable
fun DashboardScreen(
    onNavigateToServers: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToDiagnostics: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // VPN permission launcher
    val vpnPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onConnectClicked()
        }
    }

    Scaffold(
        containerColor = AtomColors.Background,
        bottomBar = {
            AtomBottomBar(
                currentRoute = Screen.Dashboard.route,
                onNavigate = { route ->
                    when (route) {
                        Screen.Servers.route    -> onNavigateToServers()
                        Screen.Statistics.route -> onNavigateToStatistics()
                        Screen.Settings.route   -> onNavigateToSettings()
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // ATOM logo / header
            Text(
                "ATOM",
                style = MaterialTheme.typography.headlineLarge,
                color = AtomColors.Cyan,
                fontWeight = FontWeight.Bold,
                letterSpacing = 8.sp
            )
            Text(
                "SECURE NETWORK",
                style = MaterialTheme.typography.labelMedium,
                color = AtomColors.TextTertiary,
                letterSpacing = 4.sp
            )

            Spacer(Modifier.height(28.dp))

            // Energy Core
            AtomCore(
                state = uiState.connectionState,
                size = 260.dp
            )

            Spacer(Modifier.height(16.dp))

            // Status label
            AnimatedContent(
                targetState = uiState.connectionState,
                transitionSpec = {
                    fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 2 } togetherWith
                    fadeOut(tween(200))
                },
                label = "statusLabel"
            ) { state ->
                val (label, color) = when (state) {
                    is ConnectionState.Connected   -> "TUNNEL ACTIVE" to AtomColors.SecureGreen
                    is ConnectionState.Connecting  -> "ESTABLISHING SECURE TUNNEL" to AtomColors.Cyan
                    is ConnectionState.Reconnecting-> "RECONNECTING · ATTEMPT ${state.attempt}" to AtomColors.Warning
                    is ConnectionState.Preparing   -> "PREPARING…" to AtomColors.Cyan
                    is ConnectionState.Disconnecting -> "DISCONNECTING…" to AtomColors.TextSecondary
                    is ConnectionState.Failed      -> "CONNECTION FAILED" to AtomColors.Error
                    else                           -> "SYSTEM OFFLINE" to AtomColors.TextTertiary
                }
                Text(
                    label,
                    style = MaterialTheme.typography.labelLarge,
                    color = color,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(20.dp))

            // Connect button
            ConnectButton(
                state = uiState.connectionState,
                onClick = {
                    val intent = VpnService.prepare(context)
                    if (intent != null) {
                        vpnPermissionLauncher.launch(intent)
                    } else {
                        viewModel.onConnectClicked()
                    }
                }
            )

            Spacer(Modifier.height(24.dp))

            // Server selector
            if (uiState.selectedServer != null) {
                val server = uiState.selectedServer!!
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AtomColors.CardBorder, AtomShapes.Card)
                        .clickable { onNavigateToServers() },
                    shape = AtomShapes.Card,
                    colors = CardDefaults.cardColors(containerColor = AtomColors.Card)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(server.flagEmoji, fontSize = 28.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(server.countryName, style = MaterialTheme.typography.titleMedium, color = AtomColors.TextPrimary, fontWeight = FontWeight.SemiBold)
                            Text(server.hostname, style = MaterialTheme.typography.bodySmall, color = AtomColors.TextSecondary)
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = AtomColors.TextTertiary)
                    }
                }
            } else {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth().clickable { onNavigateToServers() },
                    shape = AtomShapes.Card,
                    border = BorderStroke(1.dp, AtomColors.CardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Public, null, tint = AtomColors.Cyan)
                        Spacer(Modifier.width(12.dp))
                        Text("Select a server", style = MaterialTheme.typography.bodyMedium, color = AtomColors.TextSecondary, modifier = Modifier.weight(1f))
                        Icon(Icons.Default.ChevronRight, null, tint = AtomColors.TextTertiary)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Info cards grid
            val isConnected = uiState.connectionState is ConnectionState.Connected

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    label = "PUBLIC IP",
                    value = uiState.publicIp,
                    icon = Icons.Default.Language,
                    iconTint = AtomColors.Blue,
                    loading = uiState.isLoadingIp,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "EXIT IP",
                    value = if (isConnected) uiState.exitIp else null,
                    icon = Icons.Default.VpnKey,
                    iconTint = AtomColors.SecureGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            if (isConnected) {
                val stats = uiState.sessionStats
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        label = "DURATION",
                        value = stats.durationFormatted,
                        icon = Icons.Default.Timer,
                        iconTint = AtomColors.Purple,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "LATENCY",
                        value = if (stats.latencyMs > 0) "${stats.latencyMs}ms" else null,
                        icon = Icons.Default.Speed,
                        iconTint = AtomColors.Cyan,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        label = "UPLOAD",
                        value = formatBps(stats.uploadBps),
                        icon = Icons.Default.Upload,
                        iconTint = AtomColors.Magenta,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "DOWNLOAD",
                        value = formatBps(stats.downloadBps),
                        icon = Icons.Default.Download,
                        iconTint = AtomColors.Cyan,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Quick action row
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                QuickActionChip(Icons.Default.BugReport, "Diagnostics", onClick = onNavigateToDiagnostics, modifier = Modifier.weight(1f))
                QuickActionChip(Icons.Default.Favorite, "Favorites", onClick = { /* handled via bottom bar */ }, modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QuickActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.border(1.dp, AtomColors.CardBorder, AtomShapes.Medium),
        shape = AtomShapes.Medium,
        colors = CardDefaults.cardColors(containerColor = AtomColors.Card)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = AtomColors.TextSecondary, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, color = AtomColors.TextSecondary)
        }
    }
}

private fun formatBps(bps: Long): String = when {
    bps <= 0         -> "0 B/s"
    bps < 1_000      -> "$bps B/s"
    bps < 1_000_000  -> "%.1f KB/s".format(bps / 1_000f)
    else             -> "%.1f MB/s".format(bps / 1_000_000f)
}
