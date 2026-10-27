package com.atom.vpn.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.atom.vpn.domain.model.QualityTier
import com.atom.vpn.domain.model.VpnServer
import com.atom.vpn.ui.theme.AtomColors
import com.atom.vpn.ui.theme.AtomShapes

@Composable
fun ServerCard(
    server: VpnServer,
    isSelected: Boolean = false,
    onConnect: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val borderColor = if (isSelected) AtomColors.Cyan else AtomColors.CardBorder
    val bgColor = if (isSelected) AtomColors.CyanGlow else AtomColors.Card

    val qualityColor = when (server.qualityTier) {
        QualityTier.EXCELLENT -> AtomColors.SecureGreen
        QualityTier.GOOD      -> AtomColors.Cyan
        QualityTier.FAIR      -> AtomColors.Warning
        QualityTier.POOR      -> AtomColors.Error
        QualityTier.UNKNOWN   -> AtomColors.TextTertiary
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, AtomShapes.Card)
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onConnect()
            },
        shape = AtomShapes.Card,
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flag + country
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(server.flagEmoji, fontSize = 22.sp)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            server.countryName,
                            style = MaterialTheme.typography.titleMedium,
                            color = AtomColors.TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            server.hostname,
                            style = MaterialTheme.typography.bodySmall,
                            color = AtomColors.TextSecondary,
                            maxLines = 1
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                // Metrics row
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (server.pingMs > 0) {
                        MetricChip(
                            icon = Icons.Default.Speed,
                            value = "${server.pingMs}ms",
                            color = when {
                                server.pingMs < 50  -> AtomColors.SecureGreen
                                server.pingMs < 150 -> AtomColors.Cyan
                                server.pingMs < 300 -> AtomColors.Warning
                                else                -> AtomColors.Error
                            }
                        )
                    }
                    if (server.speedBps > 0) {
                        MetricChip(
                            icon = Icons.Default.CloudDownload,
                            value = "%.1f Mbps".format(server.speedMbps),
                            color = qualityColor
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                // Protocol tags
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    server.protocols.forEach { proto ->
                        ProtocolTag(proto.displayName)
                    }
                }
            }

            // Actions column
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Favorite
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onFavoriteToggle()
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        if (server.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (server.isFavorite) AtomColors.Magenta else AtomColors.TextTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                // Quality dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(qualityColor, shape = androidx.compose.foundation.shape.CircleShape)
                )
            }
        }
    }
}

@Composable
private fun MetricChip(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        Icon(icon, null, tint = color, modifier = Modifier.size(12.dp))
        Text(value, color = color, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun ProtocolTag(name: String) {
    Box(
        modifier = Modifier
            .background(AtomColors.SurfaceVariant, RoundedCornerShape(4.dp))
            .border(0.5.dp, AtomColors.CardBorder, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            name,
            style = MaterialTheme.typography.labelSmall,
            color = AtomColors.TextSecondary,
            fontSize = 9.sp
        )
    }
}
