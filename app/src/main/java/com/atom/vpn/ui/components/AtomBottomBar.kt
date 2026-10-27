package com.atom.vpn.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atom.vpn.ui.navigation.Screen
import com.atom.vpn.ui.theme.AtomColors
import com.atom.vpn.ui.theme.AtomShapes

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

private val navItems = listOf(
    BottomNavItem(Screen.Dashboard.route,  Icons.Default.Home,         "Home"),
    BottomNavItem(Screen.Servers.route,    Icons.Default.Public,       "Servers"),
    BottomNavItem(Screen.Statistics.route, Icons.Default.BarChart,     "Stats"),
    BottomNavItem(Screen.Settings.route,   Icons.Default.Settings,     "Settings")
)

@Composable
fun AtomBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(AtomColors.Surface)
            .border(1.dp, AtomColors.CardBorder, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        navItems.forEach { item ->
            val selected = currentRoute == item.route
            val iconColor by animateColorAsState(
                targetValue = if (selected) AtomColors.Cyan else AtomColors.TextTertiary,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "navColor"
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(AtomShapes.Medium)
                    .clickable { onNavigate(item.route) }
                    .padding(vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                AtomColors.CyanGlow,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(item.icon, contentDescription = item.label, tint = iconColor, modifier = Modifier.size(18.dp))
                    }
                } else {
                    Icon(item.icon, contentDescription = item.label, tint = iconColor, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    item.label,
                    color = iconColor,
                    fontSize = 10.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}
