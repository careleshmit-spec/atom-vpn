package com.atom.vpn.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.atom.vpn.ui.components.AtomCore
import com.atom.vpn.domain.model.ConnectionState
import com.atom.vpn.ui.theme.AtomColors
import com.atom.vpn.ui.theme.AtomShapes
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val body: String,
    val icon: ImageVector
)

private val pages = listOf(
    OnboardingPage("Welcome to ATOM", "A premium cyberpunk VPN client powered by real public relay servers from VPN Gate.", Icons.Default.Security),
    OnboardingPage("Public Relay Servers", "ATOM connects through volunteer-operated servers worldwide. These are NOT vetted private servers — do not transmit sensitive credentials through them.", Icons.Default.Warning),
    OnboardingPage("OpenVPN Tunnels", "ATOM uses real OpenVPN TCP/UDP tunnels to encrypt your traffic between your device and the relay endpoint.", Icons.Default.Lock),
    OnboardingPage("No Account Needed", "No registration, no data collection. ATOM does not log your browsing history or packet contents.", Icons.Default.PrivacyTip)
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val pager = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    Scaffold(containerColor = AtomColors.Background) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Text("ATOM", style = MaterialTheme.typography.headlineLarge, color = AtomColors.Cyan, fontWeight = FontWeight.Bold, letterSpacing = 6.sp)
            Spacer(Modifier.height(24.dp))
            AtomCore(state = ConnectionState.Idle, size = 180.dp)
            Spacer(Modifier.height(24.dp))
            HorizontalPager(state = pager, modifier = Modifier.weight(1f)) { idx ->
                val page = pages[idx]
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(page.icon, null, tint = AtomColors.Cyan, modifier = Modifier.size(40.dp))
                    Spacer(Modifier.height(16.dp))
                    Text(page.title, style = MaterialTheme.typography.headlineSmall, color = AtomColors.TextPrimary, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(12.dp))
                    Text(page.body, style = MaterialTheme.typography.bodyMedium, color = AtomColors.TextSecondary, textAlign = TextAlign.Center)
                }
            }
            // Dots
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(pages.size) { i ->
                    Box(
                        modifier = Modifier
                            .size(if (pager.currentPage == i) 20.dp else 8.dp, 8.dp)
                            .background(if (pager.currentPage == i) AtomColors.Cyan else AtomColors.CardBorder, AtomShapes.Circular)
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    if (pager.currentPage < pages.size - 1) scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
                    else onComplete()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = AtomShapes.Medium,
                colors = ButtonDefaults.buttonColors(containerColor = AtomColors.Cyan)
            ) {
                Text(
                    if (pager.currentPage < pages.size - 1) "Next" else "Get Started",
                    color = AtomColors.Background, fontWeight = FontWeight.Bold, fontSize = 16.sp
                )
            }
            if (pager.currentPage < pages.size - 1) {
                TextButton(onClick = onComplete) {
                    Text("Skip", color = AtomColors.TextTertiary)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
