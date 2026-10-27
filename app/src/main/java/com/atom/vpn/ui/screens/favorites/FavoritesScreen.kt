package com.atom.vpn.ui.screens.favorites

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.atom.vpn.domain.model.Protocol
import com.atom.vpn.domain.model.VpnServer
import com.atom.vpn.domain.repository.FavoriteRepository
import com.atom.vpn.domain.repository.VpnServerRepository
import com.atom.vpn.service.VpnConnectionManager
import com.atom.vpn.ui.components.ServerCard
import com.atom.vpn.ui.theme.AtomColors
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val favoriteRepo: FavoriteRepository,
    private val serverRepo: VpnServerRepository,
    private val connectionManager: VpnConnectionManager
) : ViewModel() {
    private val _servers = MutableStateFlow<List<VpnServer>>(emptyList())
    val servers: StateFlow<List<VpnServer>> = _servers.asStateFlow()

    init {
        viewModelScope.launch {
            favoriteRepo.getFavorites().collect { ids ->
                serverRepo.getServers().firstOrNull()?.getOrNull()?.let { all ->
                    _servers.value = all.filter { it.id in ids }.map { it.copy(isFavorite = true) }
                }
            }
        }
    }
    fun removeFavorite(id: String) { viewModelScope.launch { favoriteRepo.removeFavorite(id) } }
    fun connect(server: VpnServer) {
        connectionManager.connect(context, server, server.protocols.firstOrNull() ?: Protocol.OPENVPN_UDP)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(onBack: () -> Unit, viewModel: FavoritesViewModel = hiltViewModel()) {
    val servers by viewModel.servers.collectAsStateWithLifecycle()
    Scaffold(
        containerColor = AtomColors.Background,
        topBar = {
            TopAppBar(
                title = { Text("FAVORITES", color = AtomColors.TextPrimary, fontWeight = FontWeight.Bold, letterSpacing = 3.sp) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = AtomColors.TextSecondary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AtomColors.Surface)
            )
        }
    ) { padding ->
        if (servers.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FavoriteBorder, null, tint = AtomColors.TextTertiary, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("No favorites yet", color = AtomColors.TextSecondary)
                    Text("Tap ♥ on any server to save it here", color = AtomColors.TextTertiary, style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(servers, key = { it.id }) { server ->
                    ServerCard(server = server, isSelected = false, onConnect = { viewModel.connect(server) }, onFavoriteToggle = { viewModel.removeFavorite(server.id) })
                }
            }
        }
    }
}
