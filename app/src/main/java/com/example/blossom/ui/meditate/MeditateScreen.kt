package com.example.blossom.ui.meditate

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.math.PI

@Composable
fun MeditateScreen() {
    // Get ViewModel
    val viewModel: MeditateViewModel = hiltViewModel()
    var selectedDuration by remember { mutableIntStateOf(5) } // minutes
    var isRunning by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var timeRemaining by remember { mutableIntStateOf(selectedDuration * 60) } // seconds
    var totalTime by remember { mutableIntStateOf(selectedDuration * 60) }
    var showSoundPicker by remember { mutableStateOf(false) }

    // Audio state
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()

    // Enhanced breathing animation - more noticeable
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f, // Larger scale difference
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine), // Slightly faster
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_scale"
    )

    // Timer logic
    LaunchedEffect(isRunning, isPaused) {
        if (isRunning && !isPaused) {
            while (timeRemaining > 0) {
                delay(1000)
                timeRemaining--
            }
            if (timeRemaining == 0) {
                isRunning = false
                // Timer completed!
            }
        }
    }

    // Reset timer when duration changes
    LaunchedEffect(selectedDuration) {
        if (!isRunning) {
            timeRemaining = selectedDuration * 60
            totalTime = selectedDuration * 60
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(32.dp)
        ) {
            // Add some top spacing
            Spacer(modifier = Modifier.height(32.dp))

            // Beautiful Meditation Timer Circle
            MeditationTimerCircle(
                progress = if (totalTime > 0) (totalTime - timeRemaining).toFloat() / totalTime else 0f,
                timeRemaining = timeRemaining,
                isRunning = isRunning,
                breathingScale = if (isRunning && !isPaused) breathingScale else 1f
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Sound Control Button
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { showSoundPicker = true },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (audioState.currentSound != null) Icons.Default.PlayArrow else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = audioState.currentSound?.name ?: "Choose Sound",
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Duration Selection (only when not running)
            if (!isRunning) {
                Text(
                    text = "Choose Duration",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Two rows of duration chips to prevent overflow
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf(3, 5, 10).forEach { duration ->
                            DurationChip(
                                duration = duration,
                                isSelected = selectedDuration == duration,
                                onClick = { selectedDuration = duration }
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf(15, 20).forEach { duration ->
                            DurationChip(
                                duration = duration,
                                isSelected = selectedDuration == duration,
                                onClick = { selectedDuration = duration }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Control Buttons
            if (isRunning) {
                // Running state - show pause/resume and stop buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Pause/Resume Button
                    FloatingActionButton(
                        onClick = {
                            isPaused = !isPaused
                            // Handle audio pause/resume
                            if (isPaused) {
                                viewModel.pauseSound()
                            } else {
                                viewModel.resumeSound()
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (isPaused) "Resume" else "Pause",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Stop Button
                    FloatingActionButton(
                        onClick = {
                            isRunning = false
                            isPaused = false
                            timeRemaining = selectedDuration * 60
                            totalTime = selectedDuration * 60
                            // Stop audio
                            viewModel.stopSound()
                        },
                        containerColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            } else {
                // Not running state - show large centered start button
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FloatingActionButton(
                        onClick = {
                            isRunning = true
                            isPaused = false
                            totalTime = selectedDuration * 60
                            timeRemaining = selectedDuration * 60
                            // Resume audio if there was a selected sound
                            if (audioState.currentSound != null) {
                                viewModel.resumeSound()
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(80.dp) // Made it bigger and more prominent
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start Meditation",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            // Add bottom spacing to ensure button is visible
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Sound Picker Dialog
        SoundPickerDialog(
            isVisible = showSoundPicker,
            currentSound = audioState.currentSound,
            onSoundSelected = { sound ->
                if (sound != null) {
                    viewModel.playSound(sound)
                } else {
                    viewModel.stopSound()
                }
                showSoundPicker = false
            },
            onDismiss = { showSoundPicker = false }
        )
    }
}

@Composable
fun MeditationTimerCircle(
    progress: Float,
    timeRemaining: Int,
    isRunning: Boolean,
    breathingScale: Float,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val center = size.center
            val radius = size.minDimension / 2 - 20.dp.toPx()
            val strokeWidth = 16.dp.toPx() // Thicker stroke for better visibility

            // Background circle - much more visible
            drawCircle(
                color = onSurfaceColor.copy(alpha = 0.15f),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )

            // Progress circle with breathing animation - much more vibrant
            val animatedRadius = radius * breathingScale
            val progressColor = if (isRunning) {
                // Brighter, more saturated color when running
                primaryColor.copy(alpha = 1f)
            } else {
                primaryColor.copy(alpha = 0.8f)
            }

            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = progress * 360f,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth + 2.dp.toPx(), // Slightly thicker for progress
                    cap = StrokeCap.Round
                ),
                topLeft = Offset(
                    center.x - animatedRadius,
                    center.y - animatedRadius
                ),
                size = androidx.compose.ui.geometry.Size(
                    animatedRadius * 2,
                    animatedRadius * 2
                )
            )

            // Inner breathing circle - more visible
            if (isRunning) {
                drawCircle(
                    color = primaryColor.copy(alpha = 0.3f * breathingScale),
                    radius = radius * 0.5f * breathingScale,
                    center = center
                )

                // Additional outer glow effect
                drawCircle(
                    color = primaryColor.copy(alpha = 0.1f * breathingScale),
                    radius = radius * 0.8f * breathingScale,
                    center = center
                )
            }
        }

        // Time display
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatTime(timeRemaining),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Light
                ),
                color = onSurfaceColor,
                textAlign = TextAlign.Center
            )

            if (isRunning) {
                Text(
                    text = "Breathe",
                    style = MaterialTheme.typography.bodyLarge,
                    color = primaryColor.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun DurationChip(
    duration: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        onClick = onClick,
        label = {
            Text(
                text = "${duration}m",
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        },
        selected = isSelected,
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}
