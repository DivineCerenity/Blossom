package com.example.blossom.ui.meditate

import androidx.lifecycle.ViewModel
import com.example.blossom.audio.MeditationAudioManager
import com.example.blossom.data.MeditationSound
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * 🧘 MEDITATION VIEWMODEL 🧘
 * Manages meditation timer and audio state
 */

@HiltViewModel
class MeditateViewModel @Inject constructor(
    private val audioManager: MeditationAudioManager
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

    override fun onCleared() {
        super.onCleared()
        audioManager.release()
    }
}
