package com.jonathon.blossom.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jonathon.blossom.data.BreathingPattern
import com.jonathon.blossom.data.BreathingPatterns
import com.jonathon.blossom.data.MeditationSound
import com.jonathon.blossom.data.BinauralBeat
import com.jonathon.blossom.data.BinauralBeats

/**
 * 🌟 MEDITATION BOTTOM SHEET SETTINGS
 * Professional bottom sheet with all meditation settings
 */

data class MeditationSettings(
    val duration: Int = 5, // minutes
    val selectedSound: MeditationSound? = null,
    val volume: Float = 0.7f,
    val intervalBellsEnabled: Boolean = false,
    val intervalMinutes: Int = 5,
    val breathingGuideEnabled: Boolean = false,
    val breathingPattern: BreathingPattern? = null,
    // 🧠 BINAURAL BEATS SETTINGS
    val binauralBeatsEnabled: Boolean = false,
    val selectedBinauralBeat: BinauralBeat? = null,
    val binauralVolume: Float = 0.5f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationBottomSheet(
    isVisible: Boolean,
    currentSettings: MeditationSettings,
    availableSounds: List<MeditationSound>,
    onSettingsChanged: (MeditationSettings) -> Unit,
    onSave: (MeditationSettings) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var settings by remember(currentSettings) { mutableStateOf(currentSettings) }
    
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            dragHandle = {
                Surface(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Box(
                        modifier = Modifier.size(width = 32.dp, height = 4.dp)
                    )
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Header
                Text(
                    text = "Meditation Settings",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                
                // Duration Section
                DurationSection(
                    duration = settings.duration,
                    onDurationChanged = { settings = settings.copy(duration = it) }
                )
                
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                
                // Sound Section
                SoundSection(
                    selectedSound = settings.selectedSound,
                    availableSounds = availableSounds,
                    volume = settings.volume,
                    onSoundChanged = { settings = settings.copy(selectedSound = it) },
                    onVolumeChanged = { settings = settings.copy(volume = it) }
                )
                
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                
                // Interval Bells Section
                IntervalBellsSection(
                    enabled = settings.intervalBellsEnabled,
                    intervalMinutes = settings.intervalMinutes,
                    onEnabledChanged = { settings = settings.copy(intervalBellsEnabled = it) },
                    onIntervalChanged = { settings = settings.copy(intervalMinutes = it) }
                )
                
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                
                // Breathing Guide Section
                BreathingGuideSection(
                    enabled = settings.breathingGuideEnabled,
                    selectedPattern = settings.breathingPattern,
                    onEnabledChanged = { settings = settings.copy(breathingGuideEnabled = it) },
                    onPatternChanged = { settings = settings.copy(breathingPattern = it) }
                )

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // 🧠 BINAURAL BEATS SECTION
                BinauralBeatsSection(
                    enabled = settings.binauralBeatsEnabled,
                    selectedBeat = settings.selectedBinauralBeat,
                    binauralVolume = settings.binauralVolume,
                    onEnabledChanged = { settings = settings.copy(binauralBeatsEnabled = it) },
                    onBeatChanged = { settings = settings.copy(selectedBinauralBeat = it) },
                    onBinauralVolumeChanged = { settings = settings.copy(binauralVolume = it) }
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = {
                            onSettingsChanged(settings)
                            onSave(settings)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

/**
 * ⏰ DURATION SECTION
 */
@Composable
private fun DurationSection(
    duration: Int,
    onDurationChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                text = "Duration",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        // Duration Display
        Text(
            text = "$duration",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Light,
                fontSize = 48.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = if (duration == 1) "minute" else "minutes",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // Duration Slider
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Slider(
                value = duration.toFloat(),
                onValueChange = { onDurationChanged(it.toInt()) },
                valueRange = 1f..60f,
                steps = 58,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            )
            
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
    }
}

/**
 * 🎵 SOUND SECTION
 */
@Composable
private fun SoundSection(
    selectedSound: MeditationSound?,
    availableSounds: List<MeditationSound>,
    volume: Float,
    onSoundChanged: (MeditationSound?) -> Unit,
    onVolumeChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSoundPicker by remember { mutableStateOf(false) }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                text = "Background Sound",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        // Sound Selection Button
        OutlinedButton(
            onClick = { showSoundPicker = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = if (selectedSound != null) Icons.Default.PlayArrow else Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = selectedSound?.name ?: "Choose Sound",
                fontWeight = FontWeight.Medium
            )
        }
        
        // Volume Control (only if sound is selected)
        if (selectedSound != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Volume",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "🔉", style = MaterialTheme.typography.titleMedium)
                    
                    Slider(
                        value = volume,
                        onValueChange = onVolumeChanged,
                        valueRange = 0f..1f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                    )
                    
                    Text(text = "🔊", style = MaterialTheme.typography.titleMedium)
                }
                
                Text(
                    text = "${(volume * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Sound Picker Dialog
        if (showSoundPicker) {
            AlertDialog(
                onDismissRequest = { showSoundPicker = false },
                title = {
                    Text(
                        text = "Choose Background Sound",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                text = {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // No Sound option
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSoundChanged(null)
                                        showSoundPicker = false
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                RadioButton(
                                    selected = selectedSound == null,
                                    onClick = {
                                        onSoundChanged(null)
                                        showSoundPicker = false
                                    }
                                )
                                Text(
                                    text = "No Sound",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                        // Available sounds
                        items(availableSounds) { sound ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSoundChanged(sound)
                                        showSoundPicker = false
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                RadioButton(
                                    selected = selectedSound?.id == sound.id,
                                    onClick = {
                                        onSoundChanged(sound)
                                        showSoundPicker = false
                                    }
                                )
                                Text(
                                    text = sound.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { showSoundPicker = false }
                    ) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

/**
 * 🔔 INTERVAL BELLS SECTION
 */
@Composable
private fun IntervalBellsSection(
    enabled: Boolean,
    intervalMinutes: Int,
    onEnabledChanged: (Boolean) -> Unit,
    onIntervalChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
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
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Switch(
                checked = enabled,
                onCheckedChange = onEnabledChanged,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            )
        }

        if (enabled) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Bell every $intervalMinutes minutes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listOf(1, 3, 5, 10)) { minutes ->
                        FilterChip(
                            onClick = { onIntervalChanged(minutes) },
                            label = { Text("${minutes}m") },
                            selected = intervalMinutes == minutes,
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
}

/**
 * 🌬️ BREATHING GUIDE SECTION
 */
@Composable
private fun BreathingGuideSection(
    enabled: Boolean,
    selectedPattern: BreathingPattern?,
    onEnabledChanged: (Boolean) -> Unit,
    onPatternChanged: (BreathingPattern?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Air,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Breathing Guide",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Switch(
                checked = enabled,
                onCheckedChange = onEnabledChanged,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            )
        }

        if (enabled) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Choose Breathing Technique",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(BreathingPatterns.getAllPresets()) { pattern ->
                        FilterChip(
                            onClick = { onPatternChanged(pattern) },
                            label = {
                                Text(
                                    text = pattern.name,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },
                            selected = selectedPattern?.id == pattern.id,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                selectedPattern?.let { pattern ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = pattern.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Pattern: ${pattern.inhaleSeconds}s in" +
                                  (if (pattern.holdInSeconds > 0) " • ${pattern.holdInSeconds}s hold" else "") +
                                  " • ${pattern.exhaleSeconds}s out" +
                                  (if (pattern.holdOutSeconds > 0) " • ${pattern.holdOutSeconds}s hold" else ""),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * 🧠 BINAURAL BEATS SECTION
 */
@Composable
private fun BinauralBeatsSection(
    enabled: Boolean,
    selectedBeat: BinauralBeat?,
    binauralVolume: Float,
    onEnabledChanged: (Boolean) -> Unit,
    onBeatChanged: (BinauralBeat?) -> Unit,
    onBinauralVolumeChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "Binaural Beats",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Brainwave entrainment",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Switch(
                checked = enabled,
                onCheckedChange = onEnabledChanged,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            )
        }

        if (enabled) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Binaural Beat Selection
                Text(
                    text = "Choose Frequency",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // No binaural beat option
                    item {
                        FilterChip(
                            onClick = { onBeatChanged(null) },
                            label = { Text("None") },
                            selected = selectedBeat == null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }

                    // All binaural beats
                    items(BinauralBeats.getAllPresets()) { beat ->
                        FilterChip(
                            onClick = { onBeatChanged(beat) },
                            label = {
                                Text(
                                    text = "${beat.frequency}Hz",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },
                            selected = selectedBeat?.id == beat.id,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                selectedBeat?.let { beat ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = beat.name,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = beat.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Volume Controls
                if (selectedBeat != null) {
                    // Only show binaural volume control now
                    BinauralBeatsControls(
                        binauralVolume = binauralVolume,
                        onBinauralVolumeChanged = onBinauralVolumeChanged
                    )
                }
            }
        }
    }
}
