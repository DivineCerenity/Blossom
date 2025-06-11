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
import com.example.blossom.data.BreathingPattern
import com.example.blossom.data.BreathingPatterns
import com.example.blossom.ui.components.MeditationPhase
import com.example.blossom.ui.components.PreparationCountdown
import com.example.blossom.ui.components.CompletionCelebration
import com.example.blossom.ui.components.MeditationBottomSheet
import com.example.blossom.ui.components.MeditationSettings
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Air

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
    var meditationPhase by remember { mutableStateOf(MeditationPhase.PREPARATION) }
    var showPreparation by remember { mutableStateOf(false) }
    var showCompletion by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    // Audio state
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()

    // Meditation settings state
    var meditationSettings by remember {
        mutableStateOf(
            MeditationSettings(
                duration = selectedDuration,
                selectedSound = null,
                volume = 0.7f,
                intervalBellsEnabled = false,
                intervalMinutes = 5,
                breathingGuideEnabled = false,
                breathingPattern = BreathingPatterns.BOX_BREATHING
            )
        )
    }

    // Update settings when audio state changes
    LaunchedEffect(audioState) {
        meditationSettings = meditationSettings.copy(
            selectedSound = audioState.currentSound,
            volume = audioState.volume,
            intervalBellsEnabled = audioState.intervalBellsEnabled,
            intervalMinutes = audioState.intervalMinutes
        )
    }

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
                meditationPhase = MeditationPhase.COMPLETION
                showCompletion = true
                viewModel.stopSound() // Stop any playing sounds
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

        // 🌟 CLEAN NON-SCROLLABLE MEDITATION SCREEN
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            // 💡 SMART CONTEXTUAL HINT
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
                        text = when {
                            isRunning && isPaused -> "Tap to resume • Long press to stop"
                            isRunning -> "Tap to pause • Long press to stop"
                            else -> "Tap timer to start • Long press for settings"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 🌬️⏰ UNIFIED BREATHING TIMER - The Heart of Meditation
            UnifiedBreathingTimer(
                breathingState = if (meditationSettings.breathingGuideEnabled && meditationSettings.breathingPattern != null) {
                    // Create breathing state when guide is enabled
                    val pattern = meditationSettings.breathingPattern!!
                    val elapsed = totalTime - timeRemaining
                    val cycleElapsed = elapsed % pattern.totalCycleSeconds
                    com.example.blossom.data.BreathingGuideState(
                        isActive = isRunning && !isPaused,
                        currentPattern = pattern,
                        elapsedSeconds = elapsed,
                        currentPhase = pattern.getCurrentPhase(cycleElapsed),
                        phaseProgress = pattern.getPhaseProgress(cycleElapsed),
                        cycleCount = elapsed / pattern.totalCycleSeconds,
                        isGuideVisible = true
                    )
                } else {
                    // Empty breathing state when guide is disabled
                    com.example.blossom.data.BreathingGuideState()
                },
                timerProgress = if (totalTime > 0) (totalTime - timeRemaining).toFloat() / totalTime else 0f,
                timeRemaining = timeRemaining,
                isTimerRunning = isRunning,
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
                        // Start preparation countdown
                        meditationPhase = MeditationPhase.PREPARATION
                        showPreparation = true
                        totalTime = meditationSettings.duration * 60
                        timeRemaining = meditationSettings.duration * 60
                        selectedDuration = meditationSettings.duration
                        lastBellTime = 0
                    }
                },
                onLongPress = {
                    if (isRunning) {
                        // Long press to stop meditation
                        isRunning = false
                        isPaused = false
                        timeRemaining = selectedDuration * 60
                        totalTime = selectedDuration * 60
                        lastBellTime = 0
                        viewModel.stopSound()
                        meditationPhase = MeditationPhase.PREPARATION
                    } else {
                        // Long press to open settings
                        showBottomSheet = true
                    }
                }
            )



            // 📊 SMART STATUS DISPLAY
            SmartStatusDisplay(
                settings = meditationSettings,
                isRunning = isRunning,
                isPaused = isPaused,
                audioState = audioState,
                onVolumeChanged = { newVolume ->
                    meditationSettings = meditationSettings.copy(volume = newVolume)
                    viewModel.setVolume(newVolume)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 🎭 PREPARATION COUNTDOWN OVERLAY
        if (showPreparation) {
            PreparationCountdown(
                onCountdownComplete = {
                    showPreparation = false
                    meditationPhase = MeditationPhase.ACTIVE
                    isRunning = true
                    isPaused = false
                    if (audioState.currentSound != null) {
                        viewModel.resumeSound()
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // 🎉 COMPLETION CELEBRATION OVERLAY
        if (showCompletion) {
            CompletionCelebration(
                sessionDuration = selectedDuration,
                breathingPattern = if (meditationSettings.breathingGuideEnabled) meditationSettings.breathingPattern?.name ?: "" else "",
                onCelebrationComplete = {
                    showCompletion = false
                    meditationPhase = MeditationPhase.PREPARATION
                    // Reset for next session
                    timeRemaining = selectedDuration * 60
                    totalTime = selectedDuration * 60
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // 🌟 MEDITATION BOTTOM SHEET
        MeditationBottomSheet(
            isVisible = showBottomSheet,
            currentSettings = meditationSettings,
            availableSounds = com.example.blossom.data.MeditationSounds.allSounds,
            onSettingsChanged = { newSettings ->
                meditationSettings = newSettings
                // Apply settings to ViewModel
                if (newSettings.selectedSound != null) {
                    viewModel.playSound(newSettings.selectedSound!!)
                } else {
                    viewModel.stopSound()
                }
                viewModel.setVolume(newSettings.volume)
                if (newSettings.intervalBellsEnabled) {
                    viewModel.toggleIntervalBells()
                }
                viewModel.setIntervalMinutes(newSettings.intervalMinutes)
            },
            onSave = { settings ->
                showBottomSheet = false
                // Save settings and return to main screen
                meditationSettings = settings
                selectedDuration = settings.duration
                // Settings are now visible in smart status display
            },
            onDismiss = { showBottomSheet = false }
        )
    }
}

/**
 * 📊 SMART STATUS DISPLAY
 * Shows relevant information based on current state
 */
@Composable
private fun SmartStatusDisplay(
    settings: MeditationSettings,
    isRunning: Boolean,
    isPaused: Boolean,
    audioState: Any, // TODO: Fix type
    onVolumeChanged: (Float) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Always provide a consistent height container to maintain timer centering
    Box(
        modifier = modifier.height(48.dp), // Fixed height to maintain layout
        contentAlignment = Alignment.Center
    ) {
        if (!isRunning) {
            // Show settings preview when not running
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
            // Duration chip
            item {
                AssistChip(
                    onClick = { },
                    label = { Text("${settings.duration}m") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }

            // Sound chip (if selected)
            if (settings.selectedSound != null) {
                item {
                    AssistChip(
                        onClick = { },
                        label = { Text(settings.selectedSound!!.name) },
                        leadingIcon = {
                            Text("🎵", style = MaterialTheme.typography.bodyMedium)
                        }
                    )
                }
            }

            // Breathing guide chip (if enabled)
            if (settings.breathingGuideEnabled) {
                item {
                    AssistChip(
                        onClick = { },
                        label = { Text(settings.breathingPattern?.name ?: "Breathing") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Air,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }

            // Interval bells chip (if enabled)
            if (settings.intervalBellsEnabled) {
                item {
                    AssistChip(
                        onClick = { },
                        label = { Text("Bells ${settings.intervalMinutes}m") },
                        leadingIcon = {
                            Text("🔔", style = MaterialTheme.typography.bodyMedium)
                        }
                    )
                }
            }
            }
        } else {
            // Show volume control when running
            if (settings.selectedSound != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "🔉", style = MaterialTheme.typography.bodyMedium)

                    Slider(
                        value = settings.volume,
                        onValueChange = onVolumeChanged,
                        valueRange = 0f..1f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                    )

                    Text(text = "🔊", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
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
