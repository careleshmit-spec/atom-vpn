package com.atom.vpn.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.atom.vpn.domain.model.ConnectionState
import com.atom.vpn.ui.theme.AtomColors

@Composable
fun ConnectButton(
    state: ConnectionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val isConnected  = state is ConnectionState.Connected
    val isTransition = state is ConnectionState.Connecting ||
            state is ConnectionState.Reconnecting ||
            state is ConnectionState.Preparing ||
            state is ConnectionState.Disconnecting

    val innerColor = when {
        isConnected  -> AtomColors.SecureGreen
        isTransition -> AtomColors.Cyan
        else         -> AtomColors.CyanDim
    }
    val outerColor = innerColor.copy(alpha = 0.15f)
    val label = when {
        isConnected  -> "DISCONNECT"
        isTransition -> "CANCEL"
        else         -> "CONNECT"
    }

    val pulse by rememberInfiniteTransition(label = "btnPulse").animateFloat(
        initialValue = 0.9f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            tween(if (isTransition) 600 else 1500, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "btnPulseVal"
    )

    Box(
        modifier = modifier
            .size(80.dp)
            .scale(pulse)
            .border(2.dp, innerColor.copy(alpha = 0.6f), CircleShape)
            .background(outerColor, CircleShape)
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    Brush.radialGradient(listOf(innerColor.copy(0.25f), Color.Transparent)),
                    CircleShape
                )
                .border(1.5.dp, innerColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                label,
                color = innerColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}
