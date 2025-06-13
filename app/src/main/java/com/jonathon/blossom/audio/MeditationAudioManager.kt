package com.jonathon.blossom.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.jonathon.blossom.data.MeditationSound
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🎵 MEDITATION AUDIO MANAGER 🎵
 * Handles all meditation background sounds with beautiful controls
 */

data class AudioState(
    val isPlaying: Boolean = false,
    val currentSound: MeditationSound? = null, // Keep for backward compatibility
    val activeSounds: List<MeditationSound> = emptyList(), // Multiple active sounds
    val volume: Float = 0.7f,
    val isMuted: Boolean = false,
    val intervalBellsEnabled: Boolean = false,
    val intervalMinutes: Int = 5
)

@Singleton
class MeditationAudioManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mediaPlayer: MediaPlayer? = null // Keep for backward compatibility
    private var fadeJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    // Track multiple background sounds
    private val activeBackgroundPlayers = mutableMapOf<String, MediaPlayer>()

    // Track active bell sounds to stop them when needed
    private val activeBellPlayers = mutableListOf<MediaPlayer>()

    private val _audioState = MutableStateFlow(AudioState())
    val audioState: StateFlow<AudioState> = _audioState.asStateFlow()

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading
    
    /**
     * 🎵 Play a meditation sound (supports multiple sounds)
     */
    fun playSound(sound: MeditationSound) {
        try {
            _isLoading.value = true

            // Check if this sound is already playing
            if (activeBackgroundPlayers.containsKey(sound.id)) {
                Log.d("MeditationAudio", "Sound ${sound.name} is already playing")
                _isLoading.value = false
                return
            }

            // Get resource ID from assets or raw folder
            val resourceId = getResourceId(sound.fileName)

            Log.d("MeditationAudio", "Attempting to play: ${sound.name} (${sound.fileName}), resourceId: $resourceId")

            if (resourceId != 0) {
                val newPlayer = MediaPlayer.create(context, resourceId)?.apply {
                    isLooping = sound.isLooping
                    setVolume(_audioState.value.volume, _audioState.value.volume)

                    setOnPreparedListener {
                        start()

                        // Add to active players
                        activeBackgroundPlayers[sound.id] = this

                        // Update state
                        val currentActiveSounds = _audioState.value.activeSounds.toMutableList()
                        currentActiveSounds.add(sound)

                        _audioState.value = _audioState.value.copy(
                            isPlaying = true,
                            currentSound = sound, // Keep for backward compatibility
                            activeSounds = currentActiveSounds
                        )
                        _isLoading.value = false

                        // 🎵 NO FADE - INSTANT FULL VOLUME FOR SEAMLESS LOOPS!
                        setVolume(_audioState.value.volume, _audioState.value.volume)

                        Log.d("MeditationAudio", "Started playing: ${sound.name}. Total active sounds: ${currentActiveSounds.size}")
                    }

                    setOnErrorListener { _, what, extra ->
                        Log.e("MeditationAudio", "MediaPlayer error: what=$what, extra=$extra")
                        _isLoading.value = false
                        false
                    }

                    setOnCompletionListener {
                        if (!sound.isLooping) {
                            // Remove from active players
                            activeBackgroundPlayers.remove(sound.id)

                            val currentActiveSounds = _audioState.value.activeSounds.toMutableList()
                            currentActiveSounds.removeAll { it.id == sound.id }

                            _audioState.value = _audioState.value.copy(
                                isPlaying = currentActiveSounds.isNotEmpty(),
                                activeSounds = currentActiveSounds
                            )
                        }
                    }
                }

                // Keep backward compatibility
                if (mediaPlayer == null) {
                    mediaPlayer = newPlayer
                }

            } else {
                Log.w("MeditationAudio", "Sound file not found: ${sound.fileName}")
                _isLoading.value = false
            }

        } catch (e: Exception) {
            Log.e("MeditationAudio", "Error playing sound: ${sound.name}", e)
            _isLoading.value = false
        }
    }
    
    /**
     * 🛑 Stop ALL sounds with fade out (background + any bells)
     */
    fun stopSound() {
        try {
            // Stop all background sounds - NO FADE FOR SEAMLESS LOOPS!
            activeBackgroundPlayers.values.forEach { player ->
                try {
                    try {
                        if (player.isPlaying) {
                            // 🎵 INSTANT STOP - NO FADE OUT!
                            player.stop()
                            player.release()
                        } else {
                            player.release()
                        }
                    } catch (e: IllegalStateException) {
                        Log.w("MeditationAudio", "MediaPlayer already in invalid state")
                        try {
                            player.release()
                        } catch (e2: Exception) {
                            Log.w("MeditationAudio", "Error releasing invalid player")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MeditationAudio", "Error stopping background player", e)
                }
            }
            activeBackgroundPlayers.clear()

            // Stop main background sound (backward compatibility) - NO FADE!
            try {
                if (mediaPlayer?.isPlaying == true) {
                    // 🎵 INSTANT STOP - NO FADE FOR SEAMLESS LOOPS!
                    try {
                        mediaPlayer?.apply {
                            if (isPlaying) {
                                stop()
                            }
                            release()
                        }
                        mediaPlayer = null
                    } catch (e: Exception) {
                        Log.e("MeditationAudio", "Error stopping main player", e)
                    }
                } else {
                    try {
                        mediaPlayer?.release()
                    } catch (e: IllegalStateException) {
                        Log.w("MeditationAudio", "MediaPlayer already released")
                    }
                    mediaPlayer = null
                }
            } catch (e: IllegalStateException) {
                Log.w("MeditationAudio", "MediaPlayer in invalid state")
                mediaPlayer = null
            }

            // Stop all active bell sounds immediately
            activeBellPlayers.forEach { bellPlayer ->
                try {
                    try {
                        if (bellPlayer.isPlaying) {
                            bellPlayer.stop()
                        }
                    } catch (e: IllegalStateException) {
                        Log.w("MeditationAudio", "Bell player already in invalid state")
                    }
                    try {
                        bellPlayer.release()
                    } catch (e: IllegalStateException) {
                        Log.w("MeditationAudio", "Bell player already released")
                    }
                } catch (e: Exception) {
                    Log.e("MeditationAudio", "Error stopping bell player", e)
                }
            }
            activeBellPlayers.clear()

            _audioState.value = _audioState.value.copy(
                isPlaying = false,
                currentSound = null,
                activeSounds = emptyList()
            )

            Log.d("MeditationAudio", "Stopped ALL sounds (background + bells) with fade out")
        } catch (e: Exception) {
            Log.e("MeditationAudio", "Error stopping sound", e)
        }
    }
    
    /**
     * ⏸️ Pause current sound
     */
    fun pauseSound() {
        try {
            mediaPlayer?.apply {
                try {
                    if (isPlaying) {
                        pause()
                        _audioState.value = _audioState.value.copy(isPlaying = false)
                        Log.d("MeditationAudio", "Paused sound")
                    }
                } catch (e: IllegalStateException) {
                    Log.w("MeditationAudio", "Cannot pause - MediaPlayer in invalid state")
                }
            }
        } catch (e: Exception) {
            Log.e("MeditationAudio", "Error pausing sound", e)
        }
    }
    
    /**
     * ▶️ Resume current sound
     */
    fun resumeSound() {
        try {
            mediaPlayer?.apply {
                try {
                    if (!isPlaying) {
                        start()
                        _audioState.value = _audioState.value.copy(isPlaying = true)
                        Log.d("MeditationAudio", "Resumed sound")
                    }
                } catch (e: IllegalStateException) {
                    Log.w("MeditationAudio", "Cannot resume - MediaPlayer in invalid state")
                }
            }
        } catch (e: Exception) {
            Log.e("MeditationAudio", "Error resuming sound", e)
        }
    }
    
    /**
     * 🔊 Set volume (0.0 to 1.0)
     */
    fun setVolume(volume: Float) {
        val clampedVolume = volume.coerceIn(0f, 1f)

        try {
            mediaPlayer?.setVolume(clampedVolume, clampedVolume)
        } catch (e: IllegalStateException) {
            Log.w("MeditationAudio", "Cannot set volume - MediaPlayer in invalid state")
        } catch (e: Exception) {
            Log.e("MeditationAudio", "Error setting volume", e)
        }

        _audioState.value = _audioState.value.copy(volume = clampedVolume)

        Log.d("MeditationAudio", "Set volume to: $clampedVolume")
    }
    
    /**
     * 🔇 Toggle mute
     */
    fun toggleMute() {
        val newMutedState = !_audioState.value.isMuted
        val volume = if (newMutedState) 0f else _audioState.value.volume

        try {
            mediaPlayer?.setVolume(volume, volume)
        } catch (e: IllegalStateException) {
            Log.w("MeditationAudio", "Cannot toggle mute - MediaPlayer in invalid state")
        } catch (e: Exception) {
            Log.e("MeditationAudio", "Error toggling mute", e)
        }

        _audioState.value = _audioState.value.copy(isMuted = newMutedState)

        Log.d("MeditationAudio", "Toggled mute: $newMutedState")
    }

    /**
     * 🔔 Toggle interval bells
     */
    fun toggleIntervalBells() {
        _audioState.value = _audioState.value.copy(
            intervalBellsEnabled = !_audioState.value.intervalBellsEnabled
        )
        Log.d("MeditationAudio", "Interval bells: ${_audioState.value.intervalBellsEnabled}")
    }

    /**
     * 🔔 Set interval minutes
     */
    fun setIntervalMinutes(minutes: Int) {
        _audioState.value = _audioState.value.copy(intervalMinutes = minutes)
        Log.d("MeditationAudio", "Interval set to: $minutes minutes")
    }

    /**
     * 🔔 Play interval bell (called from timer)
     */
    fun playIntervalBell() {
        Log.d("MeditationAudio", "playIntervalBell() called - enabled: ${_audioState.value.intervalBellsEnabled}")

        if (!_audioState.value.intervalBellsEnabled) {
            Log.d("MeditationAudio", "Interval bells disabled, skipping")
            return
        }

        try {
            // Try to play meditation bell sound
            val bellResourceId = getResourceId("meditation_bell.wav")
            Log.d("MeditationAudio", "Bell resource ID: $bellResourceId")

            if (bellResourceId != 0) {
                // Create a separate MediaPlayer for the bell sound
                // This plays over the background sound
                val bellPlayer = MediaPlayer.create(context, bellResourceId)
                bellPlayer?.apply {
                    setVolume(0.8f, 0.8f)
                    setOnCompletionListener {
                        activeBellPlayers.remove(this)
                        release()
                    }

                    // Add to active bell players list
                    activeBellPlayers.add(this)
                    start()

                    // 🔔 CLEAN BELL SOUND - NO FADE EFFECTS!
                    coroutineScope.launch {
                        delay(5000) // Let bell ring for 5 seconds

                        // Remove from active list after natural completion
                        try {
                            activeBellPlayers.remove(this@apply)
                            if (isPlaying) {
                                stop()
                            }
                            release()
                        } catch (e: IllegalStateException) {
                            Log.w("MeditationAudio", "Bell player already released")
                        } catch (e: Exception) {
                            Log.e("MeditationAudio", "Error cleaning up bell player", e)
                        }
                    }
                }
                Log.i("MeditationAudio", "🔔 PLAYED MEDITATION BELL SOUND WITH FADE! 🔔")
            } else {
                // Fallback - just log the bell (this should show up!)
                Log.i("MeditationAudio", "🔔🔔🔔 INTERVAL BELL! (No bell sound file found) 🔔🔔🔔")
            }
        } catch (e: Exception) {
            Log.e("MeditationAudio", "Error playing interval bell", e)
            Log.i("MeditationAudio", "🔔🔔🔔 INTERVAL BELL! (Error playing sound) 🔔🔔🔔")
        }
    }
    
    // 🎵 FADE EFFECTS REMOVED FOR SEAMLESS AUDACITY LOOPS! 🎵

    /**
     * 🧹 Clean up resources
     */
    fun release() {
        fadeJob?.cancel()
        stopSound()
    }
    
    /**
     * 📁 Get resource ID from file name
     */
    private fun getResourceId(fileName: String): Int {
        // Remove extension and get base name for resource lookup
        val baseName = fileName.substringBeforeLast(".")

        return when (baseName) {
            // 🌿 Nature Sounds
            "rain_gentle" -> context.resources.getIdentifier("rain_gentle", "raw", context.packageName)
            "rain_heavy" -> context.resources.getIdentifier("rain_heavy", "raw", context.packageName)
            "forest_birds" -> context.resources.getIdentifier("forest_birds", "raw", context.packageName)
            "wind_trees" -> context.resources.getIdentifier("wind_trees", "raw", context.packageName)

            // 🌊 Water Sounds
            "ocean_waves" -> context.resources.getIdentifier("ocean_waves", "raw", context.packageName)
            "stream_flowing" -> context.resources.getIdentifier("stream_flowing", "raw", context.packageName)

            // 🔥 Fire Sounds
            "fireplace" -> context.resources.getIdentifier("fireplace", "raw", context.packageName)

            // 🎵 Ambient Sounds
            "white_noise" -> context.resources.getIdentifier("white_noise", "raw", context.packageName)
            "pink_noise" -> context.resources.getIdentifier("pink_noise", "raw", context.packageName)
            "singing_bowls" -> context.resources.getIdentifier("singing_bowls", "raw", context.packageName)
            "cafe_ambience" -> context.resources.getIdentifier("cafe_ambience", "raw", context.packageName)

            // 🔔 Bell sounds
            "meditation_bell" -> context.resources.getIdentifier("meditation_bell", "raw", context.packageName)

            else -> {
                Log.w("MeditationAudio", "Sound file not found: $fileName")
                0
            }
        }
    }
}
