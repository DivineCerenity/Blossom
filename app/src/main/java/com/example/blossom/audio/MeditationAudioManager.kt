package com.example.blossom.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.blossom.data.MeditationSound
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val isMuted: Boolean = false
)

@Singleton
class MeditationAudioManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mediaPlayer: MediaPlayer? = null
    
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
     * 🛑 Stop current sound
     */
    fun stopSound() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            mediaPlayer = null
            
            _audioState.value = _audioState.value.copy(
                isPlaying = false,
                currentSound = null
            )
            
            Log.d("MeditationAudio", "Stopped sound")
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
     * 🧹 Clean up resources
     */
    fun release() {
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

            else -> {
                Log.w("MeditationAudio", "Sound file not found: $fileName")
                0
            }
        }
    }
}
