package com.atom.vpn.ui.screens.servers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atom.vpn.domain.model.Protocol
import com.atom.vpn.domain.model.QualityTier
import com.atom.vpn.ui.components.ServerCard
import com.atom.vpn.ui.theme.AtomColors
import com.atom.vpn.ui.theme.AtomShapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServersScreen(
    onBack: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    viewModel: ServersViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showSortSheet by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = AtomColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Text("SERVERS", style = MaterialTheme.typography.titleLarge,
                        color = AtomColors.TextPrimary, fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = AtomColors.TextSecondary)
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(Icons.Default.FilterList, null,
                            tint = if (state.selectedProtocol != null || state.selectedQuality != null) AtomColors.Cyan else AtomColors.TextSecondary)
                    }
                    IconButton(onClick = { showSortSheet = true }) {
                        Icon(Icons.Default.Sort, null, tint = AtomColors.TextSecondary)
                    }
                    IconButton(onClick = viewModel::onRefresh) {
                        Icon(Icons.Default.Refresh, null, tint = AtomColors.TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AtomColors.Surface)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Search bar
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::onQueryChanged,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = { Text("Search country, hostname, IP…", color = AtomColors.TextTertiary) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = AtomColors.TextTertiary) },
                trailingIcon = {
                    if (state.query.isNotEmpty())
                        IconButton(onClick = { viewModel.onQueryChanged("") }) {
                            Icon(Icons.Default.Clear, null, tint = AtomColors.TextTertiary)
                        }
                },
                singleLine = true,
                shape = AtomShapes.Medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AtomColors.Cyan,
                    unfocusedBorderColor = AtomColors.CardBorder,
                    focusedContainerColor = AtomColors.Card,
                    unfocusedContainerColor = AtomColors.Card,
                    focusedTextColor = AtomColors.TextPrimary,
                    unfocusedTextColor = AtomColors.TextPrimary
                )
            )

            // Status row
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${state.filteredServers.size} servers",
                    style = MaterialTheme.typography.bodySmall,
                    color = AtomColors.TextTertiary
                )
                if (state.lastRefreshedMs > 0) {
                    Text(
                        "Sort: ${state.sortOption.label}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AtomColors.Cyan
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            when {
                state.isLoading && state.servers.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = AtomColors.Cyan)
                            Spacer(Modifier.height(16.dp))
                            Text("Fetching servers from VPN Gate…",
                                color = AtomColors.TextSecondary,
                                style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                state.error != null && state.servers.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CloudOff, null, tint = AtomColors.Error, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("Failed to load servers", color = AtomColors.Error, fontWeight = FontWeight.SemiBold)
                            Text(state.error ?: "", color = AtomColors.TextTertiary, style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = viewModel::onRefresh,
                                colors = ButtonDefaults.buttonColors(containerColor = AtomColors.CyanDim)) {
                                Text("Retry")
                            }
                        }
                    }
                }
                state.filteredServers.isEmpty() && state.query.isNotBlank() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.SearchOff, null, tint = AtomColors.TextTertiary, modifier = Modifier.size(40.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("No servers match \"${state.query}\"", color = AtomColors.TextSecondary)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (state.isLoading) {
                            item {
                                LinearProgressIndicator(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = AtomColors.Cyan,
                                    trackColor = AtomColors.CardBorder
                                )
                                Spacer(Modifier.height(4.dp))
                            }
                        }
                        items(state.filteredServers, key = { it.id }) { server ->
                            ServerCard(
                                server = server,
                                onConnect = {
                                    val proto = server.protocols.firstOrNull { it == com.atom.vpn.domain.model.Protocol.OPENVPN_UDP }
                                        ?: server.protocols.firstOrNull()
                                        ?: Protocol.OPENVPN_UDP
                                    viewModel.onConnect(server, proto)
                                    onNavigateToDashboard()
                                },
                                onFavoriteToggle = { viewModel.onFavoriteToggle(server) }
                            )
                        }
                        item { Spacer(Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }

    // Sort bottom sheet
    if (showSortSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSortSheet = false },
            containerColor = AtomColors.Surface
        ) {
            Text("Sort by", modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium, color = AtomColors.TextPrimary)
            SortOption.values().forEach { opt ->
                ListItem(
                    headlineContent = { Text(opt.label, color = AtomColors.TextPrimary) },
                    trailingContent = {
                        if (opt == state.sortOption)
                            Icon(Icons.Default.Check, null, tint = AtomColors.Cyan)
                    },
                    modifier = Modifier.clickable {
                        viewModel.onSortChanged(opt)
                        showSortSheet = false
                    },
                    colors = ListItemDefaults.colors(containerColor = AtomColors.Surface)
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    // Filter bottom sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            containerColor = AtomColors.Surface
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Filter", style = MaterialTheme.typography.titleMedium, color = AtomColors.TextPrimary)
                Spacer(Modifier.height(16.dp))
                Text("Protocol", style = MaterialTheme.typography.labelMedium, color = AtomColors.TextSecondary)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = state.selectedProtocol == null,
                        onClick = { viewModel.onProtocolFilter(null) },
                        label = { Text("All") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AtomColors.CyanGlow,
                            selectedLabelColor = AtomColors.Cyan
                        )
                    )
                    Protocol.supported().forEach { p ->
                        FilterChip(
                            selected = state.selectedProtocol == p,
                            onClick = { viewModel.onProtocolFilter(p) },
                            label = { Text(p.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AtomColors.CyanGlow,
                                selectedLabelColor = AtomColors.Cyan
                            )
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Quality", style = MaterialTheme.typography.labelMedium, color = AtomColors.TextSecondary)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(null to "All", QualityTier.EXCELLENT to "Excellent", QualityTier.GOOD to "Good").forEach { (tier, label) ->
                        FilterChip(
                            selected = state.selectedQuality == tier,
                            onClick = { viewModel.onQualityFilter(tier) },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AtomColors.CyanGlow,
                                selectedLabelColor = AtomColors.Cyan
                            )
                        )
                    }
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
