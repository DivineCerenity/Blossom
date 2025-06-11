package com.example.blossom.ui.meditate

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
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
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.math.PI
import com.example.blossom.ui.components.*

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
    var showDurationPicker by remember { mutableStateOf(false) }
    var lastBellTime by remember { mutableIntStateOf(0) }

    // Audio state
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()

    // Perfect 5-second breathing animation (5s expand, 5s contract)
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f, // Larger scale difference
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = EaseInOutSine), // 5 seconds each way
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_scale"
    )

    // Timer logic with interval bells
    LaunchedEffect(isRunning, isPaused) {
        if (isRunning && !isPaused) {
            while (timeRemaining > 0) {
                delay(1000)
                timeRemaining--

                // Check for interval bells
                if (audioState.intervalBellsEnabled) {
                    val elapsedSeconds = totalTime - timeRemaining
                    val intervalSeconds = audioState.intervalMinutes * 60

                    // Debug logging
                    if (elapsedSeconds % 10 == 0) { // Log every 10 seconds
                        android.util.Log.d("MeditationTimer", "Elapsed: ${elapsedSeconds}s, Interval: ${intervalSeconds}s, Bells enabled: ${audioState.intervalBellsEnabled}")
                    }

                    // Ring bell at each interval (but not at the very start)
                    if (elapsedSeconds > 0 && elapsedSeconds % intervalSeconds == 0) {
                        val currentBellTime = elapsedSeconds / intervalSeconds
                        android.util.Log.d("MeditationTimer", "Bell check: elapsed=$elapsedSeconds, interval=$intervalSeconds, currentBell=$currentBellTime, lastBell=$lastBellTime")
                        if (currentBellTime != lastBellTime) {
                            android.util.Log.i("MeditationTimer", "🔔 TRIGGERING INTERVAL BELL! 🔔")
                            viewModel.playIntervalBell()
                            lastBellTime = currentBellTime
                        }
                    }
                }
            }
            if (timeRemaining == 0) {
                isRunning = false
                lastBellTime = 0 // Reset bell counter
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
        // Beautiful floating particles background
        FloatingParticles(
            modifier = Modifier.fillMaxSize(),
            particleCount = 15,
            colors = listOf(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
            )
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(32.dp)
        ) {
            // Add some top spacing
            Spacer(modifier = Modifier.height(16.dp))

            // Elegant Hint Card
            if (!isRunning) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )

                        Text(
                            text = "Tap timer to start • Long press to change duration",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Beautiful Meditation Timer Circle with Gestures
            MeditationTimerCircle(
                progress = if (totalTime > 0) (totalTime - timeRemaining).toFloat() / totalTime else 0f,
                timeRemaining = timeRemaining,
                isRunning = isRunning,
                isPaused = isPaused,
                breathingScale = if (isRunning && !isPaused) breathingScale else 1f,
                onTap = {
                    // Tap to play/pause
                    if (isRunning) {
                        isPaused = !isPaused
                        if (isPaused) {
                            viewModel.pauseSound()
                        } else {
                            viewModel.resumeSound()
                        }
                    } else {
                        // Start meditation
                        isRunning = true
                        isPaused = false
                        totalTime = selectedDuration * 60
                        timeRemaining = selectedDuration * 60
                        lastBellTime = 0
                        if (audioState.currentSound != null) {
                            viewModel.resumeSound()
                        }
                    }
                },
                onLongPress = {
                    // Long press to edit duration (only when not running)
                    if (!isRunning) {
                        showDurationPicker = true
                    }
                }
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
                    Column {
                        Text(
                            text = when {
                                audioState.activeSounds.isNotEmpty() -> {
                                    if (audioState.activeSounds.size == 1) {
                                        audioState.activeSounds.first().name
                                    } else {
                                        "${audioState.activeSounds.size} sounds playing"
                                    }
                                }
                                audioState.currentSound != null -> audioState.currentSound!!.name
                                else -> "Choose Sound"
                            },
                            fontWeight = FontWeight.Medium
                        )

                        // Show when multiple sounds are active
                        if (audioState.activeSounds.size > 1) {
                            Text(
                                text = audioState.activeSounds.joinToString(" + ") { it.name },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )
                        } else if (audioState.currentSound != null && audioState.intervalBellsEnabled) {
                            Text(
                                text = "Background + Interval Bells",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Volume Control (when sound is selected)
            if (audioState.currentSound != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Volume",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🔉",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Slider(
                                value = audioState.volume,
                                onValueChange = { volume ->
                                    viewModel.setVolume(volume)
                                },
                                valueRange = 0f..1f,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary,
                                    inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                )
                            )

                            Text(
                                text = "🔊",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Text(
                            text = "${(audioState.volume * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Interval Bells Settings (only when not running)
            if (!isRunning) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Interval Bells",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Switch(
                                checked = audioState.intervalBellsEnabled,
                                onCheckedChange = { viewModel.toggleIntervalBells() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                )
                            )
                        }

                        if (audioState.intervalBellsEnabled) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Bell every ${audioState.intervalMinutes} minutes",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(1, 3, 5, 10).forEach { minutes ->
                                    FilterChip(
                                        onClick = { viewModel.setIntervalMinutes(minutes) },
                                        label = { Text("${minutes}m") },
                                        selected = audioState.intervalMinutes == minutes,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Clean spacing
            if (!isRunning) {
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Show stop button only when running (for emergency stop)
            if (isRunning) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = {
                            isRunning = false
                            isPaused = false
                            timeRemaining = selectedDuration * 60
                            totalTime = selectedDuration * 60
                            lastBellTime = 0
                            viewModel.stopSound()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Stop Meditation")
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

        // Duration Picker Dialog
        DurationPickerDialog(
            isVisible = showDurationPicker,
            currentDuration = selectedDuration,
            onDurationSelected = { duration ->
                selectedDuration = duration
                showDurationPicker = false
            },
            onDismiss = { showDurationPicker = false }
        )
    }
}

@Composable
fun MeditationTimerCircle(
    progress: Float,
    timeRemaining: Int,
    isRunning: Boolean,
    isPaused: Boolean,
    breathingScale: Float,
    onTap: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .size(280.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onTap() },
                    onLongPress = { onLongPress() }
                )
            },
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
                    text = if (isPaused) "Paused" else "Breathe",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isPaused) {
                        onSurfaceColor.copy(alpha = 0.6f)
                    } else {
                        primaryColor.copy(alpha = 0.8f)
                    },
                    fontWeight = FontWeight.Medium
                )
            } else {
                Text(
                    text = "Tap to start",
                    style = MaterialTheme.typography.bodyMedium,
                    color = primaryColor.copy(alpha = 0.6f),
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

@Composable
fun DurationPickerDialog(
    isVisible: Boolean,
    currentDuration: Int,
    onDurationSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    var sliderValue by remember { mutableFloatStateOf(currentDuration.toFloat()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Choose Duration",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Elegant Duration Display
                Text(
                    text = "${sliderValue.toInt()}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = if (sliderValue.toInt() == 1) "minute" else "minutes",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Beautiful Slider
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        valueRange = 1f..60f,
                        steps = 58, // 59 total values (1-60)
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "1 min",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "60 min",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            onDurationSelected(sliderValue.toInt())
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Start")
                    }
                }
            }
        }
    }
}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}
