package com.example.blossom.ui.meditate

import androidx.lifecycle.ViewModel
import com.example.blossom.audio.MeditationAudioManager
import com.example.blossom.audio.BinauralBeatsManager
import com.example.blossom.data.MeditationSound
import com.example.blossom.data.BinauralBeat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * 🧘 MEDITATION VIEWMODEL 🧘
 * Manages meditation timer and audio state
 */

@HiltViewModel
class MeditateViewModel @Inject constructor(
    private val audioManager: MeditationAudioManager,
    private val binauralBeatsManager: BinauralBeatsManager
) : ViewModel() {
    
    val audioState: StateFlow<com.example.blossom.audio.AudioState> = audioManager.audioState
    
    fun playSound(sound: MeditationSound) {
        audioManager.playSound(sound)
    }
    
    fun stopSound() {
        audioManager.stopSound()
    }
    
    fun pauseSound() {
        audioManager.pauseSound()
    }
    
    fun resumeSound() {
        audioManager.resumeSound()
    }
    
    fun setVolume(volume: Float) {
        audioManager.setVolume(volume)
    }
    
    fun toggleMute() {
        audioManager.toggleMute()
    }

    fun toggleIntervalBells() {
        audioManager.toggleIntervalBells()
    }

    fun setIntervalMinutes(minutes: Int) {
        audioManager.setIntervalMinutes(minutes)
    }

    fun playIntervalBell() {
        audioManager.playIntervalBell()
    }

    // 🧠 BINAURAL BEATS FUNCTIONS
    fun startBinauralBeats(
        beat: BinauralBeat,
        binauralVolume: Float = 0.5f,
        mixWithNature: Boolean = false,
        natureSoundFile: String? = null,
        natureVolume: Float = 0.7f
    ) {
        binauralBeatsManager.startBinauralBeats(
            beat = beat,
            binauralVol = binauralVolume,
            mixWithNature = mixWithNature,
            natureSoundFile = natureSoundFile,
            natureVol = natureVolume
        )
    }

    fun stopBinauralBeats() {
        binauralBeatsManager.stopBinauralBeats()
    }

    fun setBinauralVolume(volume: Float) {
        binauralBeatsManager.setBinauralVolume(volume)
    }

    fun setNatureSoundVolume(volume: Float) {
        binauralBeatsManager.setNatureSoundVolume(volume)
    }

    fun toggleNatureSoundMixing(enabled: Boolean, natureSoundFile: String? = null) {
        binauralBeatsManager.toggleNatureSoundMixing(enabled, natureSoundFile)
    }

    fun getBinauralBeatsState() = binauralBeatsManager.getCurrentState()

    override fun onCleared() {
        super.onCleared()
        audioManager.release()
        binauralBeatsManager.release()
    }
}
