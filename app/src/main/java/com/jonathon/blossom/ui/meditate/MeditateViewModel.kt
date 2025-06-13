package com.jonathon.blossom.ui.meditate

import androidx.lifecycle.ViewModel
import com.jonathon.blossom.audio.MeditationAudioManager
import com.jonathon.blossom.audio.BinauralBeatsManager
import com.jonathon.blossom.data.MeditationSound
import com.jonathon.blossom.data.BinauralBeat
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
    
    val audioState: StateFlow<com.jonathon.blossom.audio.AudioState> = audioManager.audioState
    
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
        natureSoundFile: String? = null,
        natureVolume: Float = 0.7f
    ) {
        binauralBeatsManager.startBinauralBeats(
            beat = beat,
            binauralVol = binauralVolume,
            natureSoundFile = natureSoundFile,
            natureVol = natureVolume
        )
    }

    fun stopBinauralBeats() {
        binauralBeatsManager.stopBinauralBeats()
    }

    fun fadeOutBinauralBeats() {
        binauralBeatsManager.fadeOutAndStop(5000) // 5 second extra smooth fade
    }

    fun setBinauralVolume(volume: Float) {
        binauralBeatsManager.setBinauralVolume(volume)
    }

    // Removed setNatureSoundVolume - no longer needed

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
