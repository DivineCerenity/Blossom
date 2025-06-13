package com.jonathon.blossom.ui.meditation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MeditationUiState(
    val selectedDuration: Int = 10, // minutes
    val timeRemaining: Int = 600, // seconds (10 minutes default)
    val totalTime: Int = 600, // seconds
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isCompleted: Boolean = false,
    val progress: Float = 0f
)

@HiltViewModel
class MeditationViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(MeditationUiState())
    val uiState = _uiState.asStateFlow()
    
    private var timerJob: Job? = null
    
    fun setDuration(minutes: Int) {
        if (!_uiState.value.isRunning) {
            val seconds = minutes * 60
            _uiState.value = _uiState.value.copy(
                selectedDuration = minutes,
                timeRemaining = seconds,
                totalTime = seconds,
                progress = 0f
            )
        }
    }
    
    fun startMeditation() {
        if (_uiState.value.isPaused) {
            // Resume from pause
            _uiState.value = _uiState.value.copy(
                isRunning = true,
                isPaused = false
            )
        } else {
            // Start fresh
            val totalSeconds = _uiState.value.selectedDuration * 60
            _uiState.value = _uiState.value.copy(
                isRunning = true,
                isPaused = false,
                isCompleted = false,
                timeRemaining = totalSeconds,
                totalTime = totalSeconds,
                progress = 0f
            )
        }
        
        startTimer()
    }
    
    fun pauseMeditation() {
        _uiState.value = _uiState.value.copy(
            isRunning = false,
            isPaused = true
        )
        timerJob?.cancel()
    }
    
    fun stopMeditation() {
        _uiState.value = _uiState.value.copy(
            isRunning = false,
            isPaused = false,
            timeRemaining = _uiState.value.totalTime,
            progress = 0f
        )
        timerJob?.cancel()
    }
    
    fun resetMeditation() {
        _uiState.value = _uiState.value.copy(
            isRunning = false,
            isPaused = false,
            isCompleted = false,
            timeRemaining = _uiState.value.totalTime,
            progress = 0f
        )
        timerJob?.cancel()
    }
    
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeRemaining > 0 && _uiState.value.isRunning) {
                delay(1000) // Wait 1 second
                
                val currentState = _uiState.value
                val newTimeRemaining = currentState.timeRemaining - 1
                val progress = 1f - (newTimeRemaining.toFloat() / currentState.totalTime.toFloat())
                
                _uiState.value = currentState.copy(
                    timeRemaining = newTimeRemaining,
                    progress = progress
                )
                
                // Check if meditation is complete
                if (newTimeRemaining <= 0) {
                    _uiState.value = _uiState.value.copy(
                        isRunning = false,
                        isPaused = false,
                        isCompleted = true,
                        progress = 1f
                    )
                    break
                }
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
