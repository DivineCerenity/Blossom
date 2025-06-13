package com.jonathon.blossom.ui.meditate

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
import com.jonathon.blossom.ui.components.*
import com.jonathon.blossom.data.BreathingPattern
import com.jonathon.blossom.data.BreathingPatterns
import com.jonathon.blossom.audio.BinauralBeatsManager
import com.jonathon.blossom.ui.components.MeditationPhase
import com.jonathon.blossom.ui.components.PreparationCountdown
import com.jonathon.blossom.ui.components.CompletionCelebration
import com.jonathon.blossom.ui.components.MeditationBottomSheet
import com.jonathon.blossom.ui.components.MeditationSettings
import com.jonathon.blossom.ui.components.AchievementCelebrationDialog
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Air
import com.jonathon.blossom.ui.insights.InsightsViewModel
import com.jonathon.blossom.data.Achievement

@Composable
fun MeditateScreen() {
    // Get ViewModels
    val viewModel: MeditateViewModel = hiltViewModel()
    val insightsViewModel: InsightsViewModel = hiltViewModel() // 📊 ANALYTICS TRACKING
    val settingsViewModel: com.jonathon.blossom.ui.settings.SettingsViewModel = hiltViewModel() // 🎨 THEME TRACKING
    val settingsUiState by settingsViewModel.uiState.collectAsState()
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

    // 📊 ANALYTICS TRACKING VARIABLES
    var sessionStartTime by remember { mutableLongStateOf(0L) }
    var sessionEndTime by remember { mutableLongStateOf(0L) }

    // 🎉 MILESTONE CELEBRATION STATE
    var newAchievements by remember { mutableStateOf<List<Achievement>>(emptyList()) }
    var showAchievementCelebration by remember { mutableStateOf(false) }

    // Audio state
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()

    // Meditation settings state - SYNC WITH AUDIO STATE
    var meditationSettings by remember {
        mutableStateOf(
            MeditationSettings(
                duration = selectedDuration,
                selectedSound = audioState.currentSound,
                volume = audioState.volume,
                intervalBellsEnabled = audioState.intervalBellsEnabled,
                intervalMinutes = audioState.intervalMinutes, // 🔧 USE ACTUAL AUDIO STATE!
                breathingGuideEnabled = false,
                breathingPattern = BreathingPatterns.BOX_BREATHING,
                // 🧠 BINAURAL BEATS DEFAULTS
                binauralBeatsEnabled = false,
                selectedBinauralBeat = null,
                binauralVolume = 0.5f,
                            )
        )
    }

    // 🔄 SYNC INITIAL SETTINGS TO AUDIO MANAGER
    LaunchedEffect(Unit) {
        android.util.Log.d("MeditationTracking", "🔄 Syncing initial settings to audio manager")
        viewModel.setIntervalMinutes(meditationSettings.intervalMinutes)
        android.util.Log.d("MeditationTracking", "Initial interval set to: ${meditationSettings.intervalMinutes}m")
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

                // 🎵 SMOOTH FADE OUT OF BINAURAL BEATS
                viewModel.fadeOutBinauralBeats() // Smooth fade instead of hard stop
                viewModel.stopSound() // Stop any playing sounds

                // 📊 RECORD COMPLETED MEDITATION SESSION
                sessionEndTime = System.currentTimeMillis()
                val actualDuration = (sessionEndTime - sessionStartTime) / 1000 // Convert to seconds

                // 🎉 RECORD SESSION (milestones will be checked in ViewModel)
                insightsViewModel.recordMeditationSessionWithAchievements(
                    startTime = sessionStartTime,
                    endTime = sessionEndTime,
                    duration = actualDuration.toInt(),
                    breathingPattern = if (meditationSettings.breathingGuideEnabled)
                        meditationSettings.breathingPattern?.name ?: "None" else "None",
                    binauralBeat = meditationSettings.selectedBinauralBeat?.name,
                    backgroundSound = meditationSettings.selectedSound?.name,
                    theme = settingsUiState.selectedTheme.displayName, // 🎨 ACTUAL THEME NAME!
                    completed = true,
                    onAchievementsUnlocked = { achievements ->
                        if (achievements.isNotEmpty()) {
                            newAchievements = achievements
                            showAchievementCelebration = true
                        }
                    }
                )

                // 🎉 SHOW COMPLETION DIALOG INSTEAD OF OVERLAY
                showCompletion = true
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
                    Text(
                        text = "💡",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.size(20.dp)
                    )

                    Text(
                        text = when {
                            isRunning && isPaused -> "Tap to resume • Long press to stop"
                            isRunning -> "Tap to pause • Long press to stop"
                            else -> "Tap to start • Long press for settings"
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
                    com.jonathon.blossom.data.BreathingGuideState(
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
                    com.jonathon.blossom.data.BreathingGuideState()
                },
                timerProgress = if (totalTime > 0) (totalTime - timeRemaining).toFloat() / totalTime else 0f,
                timeRemaining = timeRemaining,
                isTimerRunning = isRunning,
                onTap = {
                    android.util.Log.i("BLOSSOM_DEBUG", "🖱️ TAP DETECTED! isRunning: $isRunning, phase: $meditationPhase")

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
                    android.util.Log.d("MeditationTracking", "🔥 LONG PRESS DETECTED! isRunning: $isRunning")
                    android.util.Log.i("BLOSSOM_DEBUG", "🔥 LONG PRESS DETECTED! isRunning: $isRunning")

                    if (isRunning) {
                        // Long press to stop meditation
                        android.util.Log.d("MeditationTracking", "=== MANUAL STOP TRIGGERED ===")
                        android.util.Log.i("BLOSSOM_DEBUG", "=== MANUAL STOP TRIGGERED ===")
                        android.util.Log.d("MeditationTracking", "isRunning: $isRunning")
                        android.util.Log.d("MeditationTracking", "sessionStartTime: $sessionStartTime")

                        // 📊 RECORD INCOMPLETE MEDITATION SESSION FIRST
                        if (sessionStartTime > 0) {
                            sessionEndTime = System.currentTimeMillis()
                            val actualDuration = (sessionEndTime - sessionStartTime) / 1000 // Convert to seconds

                            android.util.Log.d("MeditationTracking", "Session duration: ${actualDuration}s")
                            android.util.Log.d("MeditationTracking", "Start time: $sessionStartTime")
                            android.util.Log.d("MeditationTracking", "End time: $sessionEndTime")

                            // Only record if session was at least 10 seconds
                            if (actualDuration >= 10) {
                                android.util.Log.d("MeditationTracking", "✅ Recording manual stop session: ${actualDuration}s")
                                android.util.Log.i("BLOSSOM_DEBUG", "✅ Recording manual stop session: ${actualDuration}s")

                                val breathingPattern = if (meditationSettings.breathingGuideEnabled)
                                    meditationSettings.breathingPattern?.name ?: "None" else "None"
                                val binauralBeat = meditationSettings.selectedBinauralBeat?.name
                                val backgroundSound = meditationSettings.selectedSound?.name
                                val theme = settingsUiState.selectedTheme.displayName

                                android.util.Log.d("MeditationTracking", "Session details - Breathing: $breathingPattern, Binaural: $binauralBeat, Sound: $backgroundSound, Theme: $theme")

                                // 🎉 RECORD SESSION AND CHECK FOR MILESTONES!
                                insightsViewModel.recordMeditationSessionWithAchievements(
                                    startTime = sessionStartTime,
                                    endTime = sessionEndTime,
                                    duration = actualDuration.toInt(),
                                    breathingPattern = breathingPattern,
                                    binauralBeat = binauralBeat,
                                    backgroundSound = backgroundSound,
                                    theme = theme,
                                    completed = false, // Manually stopped
                                    onAchievementsUnlocked = { achievements ->
                                        if (achievements.isNotEmpty()) {
                                            newAchievements = achievements
                                            showAchievementCelebration = true
                                        }
                                    }
                                )

                                // 📊 REFRESH ANALYTICS IMMEDIATELY
                                android.util.Log.d("MeditationTracking", "🔄 Refreshing analytics after manual stop")
                                insightsViewModel.refreshData()
                            } else {
                                android.util.Log.w("MeditationTracking", "❌ Session too short to record: ${actualDuration}s (minimum 10s)")
                            }
                            sessionStartTime = 0L // Reset
                            android.util.Log.d("MeditationTracking", "Session start time reset to 0")
                        } else {
                            android.util.Log.w("MeditationTracking", "❌ No session start time recorded - cannot track session")
                        }

                        // Now stop the meditation
                        isRunning = false
                        isPaused = false
                        timeRemaining = selectedDuration * 60
                        totalTime = selectedDuration * 60
                        lastBellTime = 0
                        viewModel.stopSound()
                        viewModel.fadeOutBinauralBeats() // Smooth fade out
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

                    // 📊 RECORD SESSION START TIME
                    sessionStartTime = System.currentTimeMillis()
                    android.util.Log.d("MeditationTracking", "=== MEDITATION SESSION STARTED ===")
                    android.util.Log.d("MeditationTracking", "Session start time: $sessionStartTime")
                    android.util.Log.d("MeditationTracking", "Duration setting: ${meditationSettings.duration} minutes")
                    android.util.Log.d("MeditationTracking", "Interval bells: ${meditationSettings.intervalBellsEnabled}, Interval: ${meditationSettings.intervalMinutes}m")
                    android.util.Log.d("MeditationTracking", "Audio state - Bells: ${audioState.intervalBellsEnabled}, Interval: ${audioState.intervalMinutes}m")

                    // 🎵 START SOUNDS WHEN MEDITATION BEGINS
                    if (meditationSettings.selectedSound != null) {
                        viewModel.playSound(meditationSettings.selectedSound!!)
                    }

                    // 🧠 START BINAURAL BEATS WHEN MEDITATION BEGINS
                    if (meditationSettings.binauralBeatsEnabled && meditationSettings.selectedBinauralBeat != null) {
                        val natureSoundFile = if (meditationSettings.selectedSound != null) {
                            meditationSettings.selectedSound!!.fileName
                        } else null

                        viewModel.startBinauralBeats(
                            beat = meditationSettings.selectedBinauralBeat!!,
                            binauralVolume = meditationSettings.binauralVolume,
                            natureSoundFile = natureSoundFile,
                            natureVolume = 0.7f
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // 🎉 BEAUTIFUL COMPLETION DIALOG
        if (showCompletion) {
            MeditationCompletionDialog(
                sessionDuration = (sessionEndTime - sessionStartTime) / 1000, // Actual duration in seconds
                breathingPattern = if (meditationSettings.breathingGuideEnabled) meditationSettings.breathingPattern?.name ?: "" else "",
                binauralBeat = meditationSettings.selectedBinauralBeat?.name,
                backgroundSound = meditationSettings.selectedSound?.name,
                onDismiss = {
                    showCompletion = false
                    meditationPhase = MeditationPhase.PREPARATION
                    // Reset for next session
                    timeRemaining = selectedDuration * 60
                    totalTime = selectedDuration * 60
                    sessionStartTime = 0L
                    sessionEndTime = 0L
                    // 📊 REFRESH ANALYTICS IMMEDIATELY
                    insightsViewModel.refreshData()
                }
            )
        }

        // 🎉 MILESTONE CELEBRATION POPUP WITH CONFETTI!
        if (showAchievementCelebration && newAchievements.isNotEmpty()) {
            AchievementCelebrationDialog(
                achievements = newAchievements,
                onDismiss = {
                    showAchievementCelebration = false
                    newAchievements = emptyList()
                }
            )
        }

        // 🌟 MEDITATION BOTTOM SHEET
        MeditationBottomSheet(
            isVisible = showBottomSheet,
            currentSettings = meditationSettings,
            availableSounds = com.jonathon.blossom.data.MeditationSounds.allSounds,
            onSettingsChanged = { newSettings ->
                android.util.Log.d("MeditationTracking", "🔧 Settings changed - Interval: ${newSettings.intervalMinutes}m, Bells: ${newSettings.intervalBellsEnabled}")

                meditationSettings = newSettings
                // DON'T auto-play sounds when settings change
                // Sounds will only play when meditation starts
                viewModel.setVolume(newSettings.volume)

                // 🔔 ALWAYS SET INTERVAL MINUTES (whether bells are enabled or not)
                viewModel.setIntervalMinutes(newSettings.intervalMinutes)

                // 🔔 SYNC INTERVAL BELLS STATE
                if (newSettings.intervalBellsEnabled != audioState.intervalBellsEnabled) {
                    viewModel.toggleIntervalBells()
                }

                // DON'T auto-start binaural beats when settings change
                // They will only start when meditation starts
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

            // 🧠 Binaural beats chip (if enabled)
            if (settings.binauralBeatsEnabled && settings.selectedBinauralBeat != null) {
                item {
                    AssistChip(
                        onClick = { },
                        label = { Text("${settings.selectedBinauralBeat!!.frequency}Hz") },
                        leadingIcon = {
                            Text("🧠", style = MaterialTheme.typography.bodyMedium)
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

/**
 * 🎉 BEAUTIFUL MEDITATION COMPLETION DIALOG
 * Shows session stats and celebration
 */
@Composable
fun MeditationCompletionDialog(
    sessionDuration: Long, // in seconds
    breathingPattern: String,
    binauralBeat: String?,
    backgroundSound: String?,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 🎉 Celebration Header
                Text(
                    text = "🎉",
                    fontSize = 48.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Meditation Complete!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ⏰ Duration Display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = formatTimeWithMinutes(sessionDuration.toInt()),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "of mindful meditation",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 📊 Session Details
                if (breathingPattern.isNotEmpty() || binauralBeat != null || backgroundSound != null) {
                    Text(
                        text = "Session Details:",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (breathingPattern.isNotEmpty() && breathingPattern != "None") {
                        SessionDetailRow(
                            icon = "🌬️",
                            label = "Breathing Pattern",
                            value = breathingPattern
                        )
                    }

                    if (binauralBeat != null) {
                        SessionDetailRow(
                            icon = "🧠",
                            label = "Binaural Beat",
                            value = binauralBeat
                        )
                    }

                    if (backgroundSound != null) {
                        SessionDetailRow(
                            icon = "🎵",
                            label = "Background Sound",
                            value = backgroundSound
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ✨ Motivational Message
                Text(
                    text = "Great job! Your mindfulness practice is making a difference. 🌟",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 👍 Dismiss Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Continue")
                }
            }
        }
    }
}

/**
 * 📊 SESSION DETAIL ROW
 * Shows individual session setting with responsive layout
 */
@Composable
fun SessionDetailRow(
    icon: String,
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        // Label row with icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = icon,
                fontSize = 14.sp,
                modifier = Modifier.width(20.dp)
            )

            Text(
                text = "$label:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.weight(1f)
            )
        }

        // Value row with proper spacing
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 2.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * ⏰ FORMAT TIME WITH MINUTES AND SECONDS
 * Shows time in "5m 30s" format
 */
fun formatTimeWithMinutes(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60

    return when {
        minutes > 0 && remainingSeconds > 0 -> "${minutes}m ${remainingSeconds}s"
        minutes > 0 -> "${minutes}m"
        else -> "${remainingSeconds}s"
    }
}
