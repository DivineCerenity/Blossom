package com.example.blossom.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.blossom.data.MeditationSound
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
    val currentSound: MeditationSound? = null,
    val volume: Float = 0.7f,
    val isMuted: Boolean = false,
    val intervalBellsEnabled: Boolean = false,
    val intervalMinutes: Int = 5
)

@Singleton
class MeditationAudioManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mediaPlayer: MediaPlayer? = null
    private var fadeJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val _audioState = MutableStateFlow(AudioState())
    val audioState: StateFlow<AudioState> = _audioState.asStateFlow()

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading
    
    /**
     * 🎵 Play a meditation sound
     */
    fun playSound(sound: MeditationSound) {
        try {
            _isLoading.value = true
            
            // Stop current sound if playing
            stopSound()
            
            // Get resource ID from assets or raw folder
            val resourceId = getResourceId(sound.fileName)

            Log.d("MeditationAudio", "Attempting to play: ${sound.name} (${sound.fileName}), resourceId: $resourceId")

            if (resourceId != 0) {
                mediaPlayer = MediaPlayer.create(context, resourceId)?.apply {
                    isLooping = sound.isLooping
                    setVolume(_audioState.value.volume, _audioState.value.volume)
                    
                    setOnPreparedListener {
                        start()
                        _audioState.value = _audioState.value.copy(
                            isPlaying = true,
                            currentSound = sound
                        )
                        _isLoading.value = false

                        // Start with fade in effect
                        fadeIn()

                        Log.d("MeditationAudio", "Started playing: ${sound.name}")
                    }
                    
                    setOnErrorListener { _, what, extra ->
                        Log.e("MeditationAudio", "MediaPlayer error: what=$what, extra=$extra")
                        _isLoading.value = false
                        false
                    }
                    
                    setOnCompletionListener {
                        if (!sound.isLooping) {
                            _audioState.value = _audioState.value.copy(
                                isPlaying = false,
                                currentSound = null
                            )
                        }
                    }
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
     * 🛑 Stop current sound with fade out
     */
    fun stopSound() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                fadeOut(1000) {
                    // After fade out completes, actually stop the player
                    try {
                        mediaPlayer?.apply {
                            if (isPlaying) {
                                stop()
                            }
                            release()
                        }
                        mediaPlayer = null
                    } catch (e: Exception) {
                        Log.e("MeditationAudio", "Error in fade out completion", e)
                    }
                }
            } else {
                mediaPlayer?.release()
                mediaPlayer = null
            }

            _audioState.value = _audioState.value.copy(
                isPlaying = false,
                currentSound = null
            )

            Log.d("MeditationAudio", "Stopped sound with fade out")
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
                if (isPlaying) {
                    pause()
                    _audioState.value = _audioState.value.copy(isPlaying = false)
                    Log.d("MeditationAudio", "Paused sound")
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
                if (!isPlaying) {
                    start()
                    _audioState.value = _audioState.value.copy(isPlaying = true)
                    Log.d("MeditationAudio", "Resumed sound")
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
        
        mediaPlayer?.setVolume(clampedVolume, clampedVolume)
        _audioState.value = _audioState.value.copy(volume = clampedVolume)
        
        Log.d("MeditationAudio", "Set volume to: $clampedVolume")
    }
    
    /**
     * 🔇 Toggle mute
     */
    fun toggleMute() {
        val newMutedState = !_audioState.value.isMuted
        val volume = if (newMutedState) 0f else _audioState.value.volume

        mediaPlayer?.setVolume(volume, volume)
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
                    setOnCompletionListener { release() }
                    start()
                }
                Log.i("MeditationAudio", "🔔 PLAYED MEDITATION BELL SOUND! 🔔")
            } else {
                // Fallback - just log the bell (this should show up!)
                Log.i("MeditationAudio", "🔔🔔🔔 INTERVAL BELL! (No bell sound file found) 🔔🔔🔔")
            }
        } catch (e: Exception) {
            Log.e("MeditationAudio", "Error playing interval bell", e)
            Log.i("MeditationAudio", "🔔🔔🔔 INTERVAL BELL! (Error playing sound) 🔔🔔🔔")
        }
    }
    
    /**
     * 🎵 Fade in effect - gradually increase volume
     */
    private fun fadeIn(durationMs: Long = 2000) {
        fadeJob?.cancel()
        fadeJob = coroutineScope.launch {
            val targetVolume = _audioState.value.volume
            val steps = 20
            val stepDuration = durationMs / steps
            val volumeStep = targetVolume / steps

            mediaPlayer?.setVolume(0f, 0f)

            for (i in 1..steps) {
                delay(stepDuration)
                val currentVolume = volumeStep * i
                mediaPlayer?.setVolume(currentVolume, currentVolume)
            }
        }
    }

    /**
     * 🎵 Fade out effect - gradually decrease volume
     */
    private fun fadeOut(durationMs: Long = 1500, onComplete: () -> Unit = {}) {
        fadeJob?.cancel()
        fadeJob = coroutineScope.launch {
            val currentVolume = _audioState.value.volume
            val steps = 15
            val stepDuration = durationMs / steps
            val volumeStep = currentVolume / steps

            for (i in 1..steps) {
                delay(stepDuration)
                val newVolume = currentVolume - (volumeStep * i)
                mediaPlayer?.setVolume(newVolume.coerceAtLeast(0f), newVolume.coerceAtLeast(0f))
            }

            onComplete()
        }
    }

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
