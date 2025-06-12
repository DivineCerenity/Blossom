package com.example.blossom.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.example.blossom.data.BinauralBeat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🧠 BINAURAL BEATS AUDIO MANAGER
 * Professional binaural beats playback with seamless looping
 */
@Singleton
class BinauralBeatsManager @Inject constructor(
    private val context: Context
) {
    private var binauralPlayer: MediaPlayer? = null
    private var natureSoundPlayer: MediaPlayer? = null
    private var currentBeat: BinauralBeat? = null
    private var isPlaying = false
    private var binauralVolume = 0.5f
    private var natureSoundVolume = 0.7f
    private var isMixingEnabled = false
    private var currentNatureSoundFileName: String? = null
    
    private val scope = CoroutineScope(Dispatchers.Main)
    private var fadeJob: Job? = null
    
    companion object {
        private const val TAG = "BinauralBeatsManager"
        private const val BINAURAL_FOLDER = "sounds/binaural"
        private const val NATURE_SOUNDS_FOLDER = "sounds"
    }
    
    /**
     * 🎵 Start playing binaural beats
     */
    fun startBinauralBeats(
        beat: BinauralBeat,
        binauralVol: Float = 0.5f,
        mixWithNature: Boolean = false,
        natureSoundFile: String? = null,
        natureVol: Float = 0.7f
    ) {
        try {
            Log.d(TAG, "Starting binaural beats: ${beat.name} (${beat.frequency}Hz)")
            
            // Stop any current playback
            stopBinauralBeats()
            
            // Update settings
            currentBeat = beat
            binauralVolume = binauralVol
            isMixingEnabled = mixWithNature
            natureSoundVolume = natureVol
            currentNatureSoundFileName = natureSoundFile
            
            // Start binaural beats
            startBinauralPlayer(beat)
            
            // Start nature sounds if mixing is enabled
            if (mixWithNature && natureSoundFile != null) {
                startNatureSoundPlayer(natureSoundFile)
            }
            
            isPlaying = true
            
        } catch (e: Exception) {
            Log.e(TAG, "Error starting binaural beats", e)
        }
    }
    
    /**
     * 🎵 Start binaural beats player
     */
    private fun startBinauralPlayer(beat: BinauralBeat) {
        try {
            binauralPlayer?.release()
            binauralPlayer = MediaPlayer().apply {
                val assetPath = "$BINAURAL_FOLDER/${beat.fileName}"
                val afd = context.assets.openFd(assetPath)
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                
                isLooping = true // SEAMLESS LOOPING!
                setVolume(binauralVolume, binauralVolume)
                
                setOnPreparedListener { player ->
                    Log.d(TAG, "Binaural beats prepared: ${beat.name}")
                    player.start()
                }
                
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "Binaural beats error: what=$what, extra=$extra")
                    true
                }
                
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting binaural player", e)
        }
    }
    
    /**
     * 🌊 Start nature sounds player (for mixing)
     */
    private fun startNatureSoundPlayer(fileName: String) {
        try {
            natureSoundPlayer?.release()
            natureSoundPlayer = MediaPlayer().apply {
                val assetPath = "$NATURE_SOUNDS_FOLDER/$fileName"
                val afd = context.assets.openFd(assetPath)
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                
                isLooping = true
                setVolume(natureSoundVolume, natureSoundVolume)
                
                setOnPreparedListener { player ->
                    Log.d(TAG, "Nature sound prepared for mixing: $fileName")
                    player.start()
                }
                
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "Nature sound error: what=$what, extra=$extra")
                    true
                }
                
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting nature sound player", e)
        }
    }
    
    /**
     * ⏹️ Stop binaural beats
     */
    fun stopBinauralBeats() {
        try {
            Log.d(TAG, "Stopping binaural beats")
            
            fadeJob?.cancel()
            
            binauralPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            binauralPlayer = null
            
            natureSoundPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            natureSoundPlayer = null
            
            isPlaying = false
            currentBeat = null
            
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping binaural beats", e)
        }
    }
    
    /**
     * 🔊 Update binaural beats volume
     */
    fun setBinauralVolume(volume: Float) {
        binauralVolume = volume.coerceIn(0f, 1f)
        binauralPlayer?.setVolume(binauralVolume, binauralVolume)
        Log.d(TAG, "Binaural volume set to: $binauralVolume")
    }
    
    /**
     * 🌊 Update nature sounds volume
     */
    fun setNatureSoundVolume(volume: Float) {
        natureSoundVolume = volume.coerceIn(0f, 1f)
        natureSoundPlayer?.setVolume(natureSoundVolume, natureSoundVolume)
        Log.d(TAG, "Nature sound volume set to: $natureSoundVolume")
    }
    
    /**
     * 🎛️ Toggle nature sound mixing
     */
    fun toggleNatureSoundMixing(enabled: Boolean, natureSoundFile: String? = null) {
        isMixingEnabled = enabled
        
        if (enabled && natureSoundFile != null && isPlaying) {
            // Start nature sound mixing
            currentNatureSoundFileName = natureSoundFile
            startNatureSoundPlayer(natureSoundFile)
        } else {
            // Stop nature sound mixing
            natureSoundPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            natureSoundPlayer = null
        }
        
        Log.d(TAG, "Nature sound mixing: $enabled")
    }
    
    /**
     * 🎵 INSTANT STOP - NO FADE FOR SEAMLESS LOOPS!
     */
    fun fadeOutAndStop(durationMs: Long = 5000) {
        fadeJob?.cancel()
        // 🎵 INSTANT STOP - NO FADE EFFECTS!
        stopBinauralBeats()
    }
    
    /**
     * 📊 Get current state
     */
    fun getCurrentState() = BinauralBeatsState(
        isActive = isPlaying,
        currentBeat = currentBeat,
        binauralVolume = binauralVolume,
        isMixedWithNatureSounds = isMixingEnabled,
        natureSoundVolume = natureSoundVolume
    )
    
    /**
     * 🧹 Cleanup resources
     */
    fun release() {
        stopBinauralBeats()
        fadeJob?.cancel()
    }
}

/**
 * 📊 BINAURAL BEATS STATE
 */
data class BinauralBeatsState(
    val isActive: Boolean = false,
    val currentBeat: BinauralBeat? = null,
    val binauralVolume: Float = 0.5f,
    val isMixedWithNatureSounds: Boolean = false,
    val natureSoundVolume: Float = 0.7f
)
