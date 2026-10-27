package com.atom.vpn.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.atom.vpn.ui.theme.AtomColors
import com.atom.vpn.ui.theme.AtomShapes

@Composable
fun StatCard(
    label: String,
    value: String?,
    icon: ImageVector,
    iconTint: Color = AtomColors.Cyan,
    modifier: Modifier = Modifier,
    loading: Boolean = false
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, AtomColors.CardBorder, AtomShapes.Card),
        shape = AtomShapes.Card,
        colors = CardDefaults.cardColors(containerColor = AtomColors.Card)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = iconTint.copy(alpha = 0.12f),
                        shape = AtomShapes.Medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(label, style = MaterialTheme.typography.bodySmall, color = AtomColors.TextSecondary)
                Spacer(Modifier.height(2.dp))
                if (loading) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(16.dp)
                            .background(AtomColors.SurfaceVariant, AtomShapes.Small)
                    )
                } else {
                    Text(
                        value ?: "—",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (value != null) AtomColors.TextPrimary else AtomColors.TextTertiary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
