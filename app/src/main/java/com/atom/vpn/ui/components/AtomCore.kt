package com.atom.vpn.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.atom.vpn.domain.model.ConnectionState
import com.atom.vpn.ui.theme.AtomColors
import kotlin.math.*

/**
 * The signature ATOM energy core — an animated nucleus with orbital rings,
 * rotating particles, and dynamic color based on [ConnectionState].
 *
 * Design goals:
 * - Smooth and lifecycle-aware (uses Compose infinite animations).
 * - Color shifts: dark/gray when idle, pulsing cyan/purple when connecting,
 *   green/cyan when connected, red when failed.
 * - Battery-conscious: animations run at 60fps via Compose transitions.
 */
@Composable
fun AtomCore(
    state: ConnectionState,
    modifier: Modifier = Modifier,
    size: Dp = 260.dp
) {
    val isConnected    = state is ConnectionState.Connected
    val isConnecting   = state is ConnectionState.Connecting || state is ConnectionState.Reconnecting || state is ConnectionState.Preparing
    val isFailed       = state is ConnectionState.Failed

    // Colors
    val coreColor = when {
        isConnected  -> AtomColors.SecureGreen
        isConnecting -> AtomColors.Cyan
        isFailed     -> AtomColors.Error
        else         -> AtomColors.CyanDim
    }
    val ringColor = when {
        isConnected  -> AtomColors.SecureGreen
        isConnecting -> AtomColors.Cyan
        isFailed     -> AtomColors.Error
        else         -> AtomColors.Blue
    }
    val particleColor = when {
        isConnected  -> AtomColors.SecureGreenGlow
        isConnecting -> AtomColors.CyanGlow
        isFailed     -> AtomColors.ErrorGlow
        else         -> AtomColors.BlueGlow
    }

    // Ring rotation
    val ring1Rotation by rememberInfiniteTransition(label = "ring1").animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Restart),
        label = "ring1Rot"
    )
    val ring2Rotation by rememberInfiniteTransition(label = "ring2").animateFloat(
        initialValue = 360f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing), RepeatMode.Restart),
        label = "ring2Rot"
    )
    val ring3Rotation by rememberInfiniteTransition(label = "ring3").animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label = "ring3Rot"
    )

    // Pulse
    val pulseScale by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.95f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            tween(if (isConnecting) 700 else 1800, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Scanning line (only while connecting)
    val scanAngle by rememberInfiniteTransition(label = "scan").animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart),
        label = "scan"
    )

    // Particle orbit angle
    val particleAngle by rememberInfiniteTransition(label = "particle").animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Restart),
        label = "particleAngle"
    )

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .size(size)
                .graphicsLayer { scaleX = pulseScale; scaleY = pulseScale }
        ) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val r  = minOf(cx, cy)

            // -- Outer glow halo
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(coreColor.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(cx, cy), radius = r
                ),
                radius = r
            )

            // -- Orbital ring 1 (tilted ellipse, outermost)
            drawOvalRing(cx, cy, r * 0.85f, r * 0.28f, ring1Rotation, ringColor.copy(alpha = 0.55f), 1.5f)

            // -- Orbital ring 2 (more circular, mid)
            drawOvalRing(cx, cy, r * 0.65f, r * 0.20f, ring2Rotation + 60f, ringColor.copy(alpha = 0.45f), 1.2f)

            // -- Orbital ring 3 (small inner)
            drawOvalRing(cx, cy, r * 0.45f, r * 0.14f, ring3Rotation + 120f, ringColor.copy(alpha = 0.40f), 1.0f)

            // -- Scanning arc (only connecting)
            if (isConnecting) {
                val sweepBrush = Brush.sweepGradient(
                    0f to Color.Transparent,
                    0.6f to coreColor.copy(alpha = 0.3f),
                    1f to coreColor.copy(alpha = 0.0f),
                    center = Offset(cx, cy)
                )
                rotate(scanAngle, Offset(cx, cy)) {
                    drawArc(
                        brush = sweepBrush,
                        startAngle = 0f, sweepAngle = 180f,
                        useCenter = true,
                        topLeft = Offset(cx - r * 0.75f, cy - r * 0.75f),
                        size = androidx.compose.ui.geometry.Size(r * 1.5f, r * 1.5f)
                    )
                }
            }

            // -- Orbit particles (4 nodes on ring 1)
            repeat(4) { i ->
                val angle = Math.toRadians((particleAngle + i * 90.0).toDouble())
                val px = cx + (r * 0.85f) * cos(angle).toFloat()
                val py = cy + (r * 0.28f) * sin(angle).toFloat()
                drawCircle(
                    color = coreColor.copy(alpha = 0.9f),
                    radius = 5f,
                    center = Offset(px, py)
                )
                // Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(coreColor.copy(alpha = 0.5f), Color.Transparent),
                        center = Offset(px, py), radius = 18f
                    ),
                    radius = 18f,
                    center = Offset(px, py)
                )
            }

            // -- Central nucleus glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(coreColor.copy(alpha = 0.6f), coreColor.copy(alpha = 0.1f), Color.Transparent),
                    center = Offset(cx, cy), radius = r * 0.25f
                ),
                radius = r * 0.25f
            )

            // -- Central nucleus solid
            drawCircle(
                color = coreColor,
                radius = r * 0.10f,
                center = Offset(cx, cy)
            )

            // -- Inner nucleus bright point
            drawCircle(
                color = Color.White.copy(alpha = 0.8f),
                radius = r * 0.035f,
                center = Offset(cx, cy)
            )
        }
    }
}

/** Draws a rotated oval ring (ellipse) on the canvas. */
private fun DrawScope.drawOvalRing(
    cx: Float, cy: Float,
    rx: Float, ry: Float,
    rotationDeg: Float,
    color: Color,
    strokeWidth: Float
) {
    rotate(rotationDeg, Offset(cx, cy)) {
        drawOval(
            color = color,
            topLeft = Offset(cx - rx, cy - ry),
            size = androidx.compose.ui.geometry.Size(rx * 2, ry * 2),
            style = Stroke(width = strokeWidth)
        )
    }
}
