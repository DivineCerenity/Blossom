package com.example.blossom.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.center
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.blossom.data.BreathingGuideState
import com.example.blossom.data.BreathingPhase
import kotlin.math.sin
import kotlin.math.PI

/**
 * 🌬️ UNIFIED BREATHING TIMER 🌬️
 * Beautiful breathing guide integrated with meditation timer
 */

@Composable
fun UnifiedBreathingTimer(
    breathingState: BreathingGuideState,
    timerProgress: Float, // 0.0 to 1.0
    timeRemaining: Int, // seconds
    isTimerRunning: Boolean,
    onTap: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface

    // Breathing animation - smooth scale based on phase
    val targetScale = if (breathingState.isActive) {
        when (breathingState.currentPhase) {
            BreathingPhase.INHALE -> 1.0f + (breathingState.phaseProgress * 0.3f) // Grow from 1.0 to 1.3
            BreathingPhase.HOLD_IN -> 1.3f // Stay expanded
            BreathingPhase.EXHALE -> 1.3f - (breathingState.phaseProgress * 0.3f) // Shrink from 1.3 to 1.0
            BreathingPhase.HOLD_OUT -> 1.0f // Stay contracted
        }
    } else {
        1.0f // No breathing animation when not active
    }

    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "breathing_scale"
    )

    // Pulsing glow effect
    val infiniteTransition = rememberInfiniteTransition(label = "breathing_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = modifier
            .size(320.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onTap() },
                    onLongPress = { onLongPress() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Outer glow rings (only when breathing is active)
        if (breathingState.isActive) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size((340 + index * 20).dp)
                        .clip(CircleShape)
                        .background(
                            primaryColor.copy(alpha = glowAlpha * (0.08f - index * 0.02f))
                        )
                )
            }
        }

        // Main unified circle with timer and breathing
        Canvas(
            modifier = Modifier
                .size(280.dp)
                .graphicsLayer {
                    scaleX = animatedScale
                    scaleY = animatedScale
                }
        ) {
            val center = size.center
            val radius = size.minDimension / 2 - 20.dp.toPx()
            val strokeWidth = 12.dp.toPx()

            // Background circle
            drawCircle(
                color = primaryColor.copy(alpha = 0.1f),
                radius = radius,
                center = center
            )

            // Timer progress ring (outer)
            if (isTimerRunning) {
                drawArc(
                    color = primaryColor.copy(alpha = 0.8f),
                    startAngle = -90f,
                    sweepAngle = timerProgress * 360f,
                    useCenter = false,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
                )
            }

            // Breathing phase indicator (inner circle)
            if (breathingState.isActive) {
                val innerRadius = radius * 0.7f
                val phaseColor = when (breathingState.currentPhase) {
                    BreathingPhase.INHALE -> primaryColor.copy(alpha = 0.8f) // Theme primary for inhale
                    BreathingPhase.HOLD_IN -> primaryColor.copy(alpha = 0.6f) // Muted primary for hold
                    BreathingPhase.EXHALE -> primaryColor.copy(alpha = 0.9f) // Strong primary for exhale
                    BreathingPhase.HOLD_OUT -> primaryColor.copy(alpha = 0.4f) // Soft primary for pause
                }

                // Phase background
                drawCircle(
                    color = phaseColor,
                    radius = innerRadius,
                    center = center
                )

                // Phase progress arc
                drawArc(
                    color = phaseColor.copy(alpha = 1f),
                    startAngle = -90f,
                    sweepAngle = breathingState.phaseProgress * 360f,
                    useCenter = false,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6.dp.toPx())
                )
            }
        }

        // Center content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Timer display
            Text(
                text = formatTime(timeRemaining),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Breathing phase (when active)
            if (breathingState.isActive) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = breathingState.currentPhaseDisplay,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = primaryColor
                )

                Text(
                    text = breathingState.currentInstruction,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            } else if (!isTimerRunning) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap to start",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * 🌬️ ORIGINAL BREATHING GUIDE (for standalone use)
 */

@Composable
fun BreathingGuide(
    state: BreathingGuideState,
    modifier: Modifier = Modifier,
    onToggleGuide: () -> Unit = {}
) {
    if (!state.isGuideVisible) return
    
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    
    // Breathing animation - smooth scale based on phase
    val targetScale = when (state.currentPhase) {
        BreathingPhase.INHALE -> 1.0f + (state.phaseProgress * 0.4f) // Grow from 1.0 to 1.4
        BreathingPhase.HOLD_IN -> 1.4f // Stay expanded
        BreathingPhase.EXHALE -> 1.4f - (state.phaseProgress * 0.4f) // Shrink from 1.4 to 1.0
        BreathingPhase.HOLD_OUT -> 1.0f // Stay contracted
    }
    
    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "breathing_scale"
    )
    
    // Pulsing glow effect
    val infiniteTransition = rememberInfiniteTransition(label = "breathing_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Breathing Circle with Glow
        Box(
            modifier = Modifier.size(280.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer glow rings
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size((300 + index * 20).dp)
                        .clip(CircleShape)
                        .background(
                            primaryColor.copy(alpha = glowAlpha * (0.1f - index * 0.02f))
                        )
                )
            }
            
            // Main breathing circle
            Box(
                modifier = Modifier
                    .size((200 * animatedScale).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.8f),
                                primaryColor.copy(alpha = 0.4f),
                                primaryColor.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Inner circle with phase indicator
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            when (state.currentPhase) {
                                BreathingPhase.INHALE -> primaryColor.copy(alpha = 0.6f)
                                BreathingPhase.HOLD_IN -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                                BreathingPhase.EXHALE -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
                                BreathingPhase.HOLD_OUT -> surfaceColor.copy(alpha = 0.6f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Phase progress indicator
                    Canvas(
                        modifier = Modifier.size(100.dp)
                    ) {
                        drawBreathingProgress(
                            progress = state.phaseProgress,
                            phase = state.currentPhase,
                            primaryColor = primaryColor
                        )
                    }
                }
            }
        }
        
        // Phase Information
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Current phase
            Text(
                text = state.currentPhaseDisplay,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = primaryColor,
                textAlign = TextAlign.Center
            )
            
            // Instruction
            Text(
                text = state.currentInstruction,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
            
            // Pattern info
            state.currentPattern?.let { pattern ->
                Text(
                    text = "${pattern.name} • Cycle ${state.cycleCount + 1}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // Breathing rhythm visualization
        BreathingRhythmBar(
            pattern = state.currentPattern,
            currentPhase = state.currentPhase,
            phaseProgress = state.phaseProgress,
            primaryColor = primaryColor,
            secondaryColor = MaterialTheme.colorScheme.secondary,
            tertiaryColor = MaterialTheme.colorScheme.tertiary,
            outlineColor = MaterialTheme.colorScheme.outline,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .padding(horizontal = 32.dp)
        )
    }
}

/**
 * 🎨 Draw breathing progress indicator with theme colors
 */
private fun DrawScope.drawBreathingProgress(
    progress: Float,
    phase: BreathingPhase,
    primaryColor: Color
) {
    val center = this.center
    val radius = size.minDimension / 3

    // Background circle
    drawCircle(
        color = primaryColor.copy(alpha = 0.2f),
        radius = radius,
        center = center
    )

    // Progress arc with theme colors
    val sweepAngle = 360f * progress
    val phaseColor = when (phase) {
        BreathingPhase.INHALE -> primaryColor // Theme primary for inhale
        BreathingPhase.HOLD_IN -> primaryColor.copy(alpha = 0.7f) // Slightly muted primary for hold
        BreathingPhase.EXHALE -> primaryColor.copy(alpha = 0.9f) // Strong primary for exhale
        BreathingPhase.HOLD_OUT -> primaryColor.copy(alpha = 0.5f) // Soft primary for pause
    }

    drawArc(
        color = phaseColor,
        startAngle = -90f,
        sweepAngle = sweepAngle,
        useCenter = false,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8.dp.toPx())
    )
}

/**
 * 📊 Breathing rhythm visualization bar
 */
@Composable
private fun BreathingRhythmBar(
    pattern: com.example.blossom.data.BreathingPattern?,
    currentPhase: BreathingPhase,
    phaseProgress: Float,
    primaryColor: Color,
    secondaryColor: Color,
    tertiaryColor: Color,
    outlineColor: Color,
    modifier: Modifier = Modifier
) {
    pattern ?: return
    
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        // Phase segments
        Row(modifier = Modifier.fillMaxSize()) {
            // Inhale segment
            Box(
                modifier = Modifier
                    .weight(pattern.inhaleSeconds.toFloat())
                    .fillMaxHeight()
                    .background(
                        if (currentPhase == BreathingPhase.INHALE) {
                            primaryColor.copy(alpha = 0.6f + phaseProgress * 0.4f)
                        } else {
                            primaryColor.copy(alpha = 0.2f) // Theme primary for inhale
                        }
                    )
            )
            
            // Hold in segment (if exists)
            if (pattern.holdInSeconds > 0) {
                Box(
                    modifier = Modifier
                        .weight(pattern.holdInSeconds.toFloat())
                        .fillMaxHeight()
                        .background(
                            if (currentPhase == BreathingPhase.HOLD_IN) {
                                secondaryColor.copy(alpha = 0.6f + phaseProgress * 0.4f)
                            } else {
                                secondaryColor.copy(alpha = 0.2f) // Theme secondary for hold
                            }
                        )
                )
            }
            
            // Exhale segment
            Box(
                modifier = Modifier
                    .weight(pattern.exhaleSeconds.toFloat())
                    .fillMaxHeight()
                    .background(
                        if (currentPhase == BreathingPhase.EXHALE) {
                            tertiaryColor.copy(alpha = 0.6f + phaseProgress * 0.4f)
                        } else {
                            tertiaryColor.copy(alpha = 0.2f) // Theme tertiary for exhale
                        }
                    )
            )
            
            // Hold out segment (if exists)
            if (pattern.holdOutSeconds > 0) {
                Box(
                    modifier = Modifier
                        .weight(pattern.holdOutSeconds.toFloat())
                        .fillMaxHeight()
                        .background(
                            if (currentPhase == BreathingPhase.HOLD_OUT) {
                                outlineColor.copy(alpha = 0.6f + phaseProgress * 0.4f)
                            } else {
                                outlineColor.copy(alpha = 0.2f) // Theme outline for pause
                            }
                        )
                )
            }
        }
    }
}

/**
 * 🎯 BREATHING PATTERN SELECTOR
 */
@Composable
fun BreathingPatternSelector(
    selectedPattern: com.example.blossom.data.BreathingPattern?,
    onPatternSelected: (com.example.blossom.data.BreathingPattern) -> Unit,
    onCreateCustom: () -> Unit,
    modifier: Modifier = Modifier
) {
    val patterns = com.example.blossom.data.BreathingPatterns.getAllPresets()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Choose Breathing Technique",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        // Preset patterns
        patterns.forEach { pattern ->
            BreathingPatternCard(
                pattern = pattern,
                isSelected = selectedPattern?.id == pattern.id,
                onClick = { onPatternSelected(pattern) }
            )
        }

        // Create custom pattern button
        OutlinedButton(
            onClick = onCreateCustom,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Custom Pattern")
        }
    }
}

/**
 * 🎴 BREATHING PATTERN CARD
 */
@Composable
private fun BreathingPatternCard(
    pattern: com.example.blossom.data.BreathingPattern,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(
                2.dp,
                MaterialTheme.colorScheme.primary
            )
        } else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pattern.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                // Pattern timing display
                Text(
                    text = buildString {
                        append(pattern.inhaleSeconds)
                        if (pattern.holdInSeconds > 0) append("-${pattern.holdInSeconds}")
                        append("-${pattern.exhaleSeconds}")
                        if (pattern.holdOutSeconds > 0) append("-${pattern.holdOutSeconds}")
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = pattern.description,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                }
            )
        }
    }
}

/**
 * 🕐 Format time helper function
 */
private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%d:%02d", minutes, remainingSeconds)
}
