package com.jonathon.blossom.ui.meditate

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jonathon.blossom.data.BreathingGuideState
import com.jonathon.blossom.data.BreathingPattern
import com.jonathon.blossom.data.BreathingPatterns
import com.jonathon.blossom.data.BreathingPhase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 🌬️ BREATHING GUIDE MANAGER 🌬️
 * Manages breathing guide state and timing
 */
class BreathingGuideManager : ViewModel() {
    
    private val _breathingState = MutableStateFlow(BreathingGuideState())
    val breathingState: StateFlow<BreathingGuideState> = _breathingState.asStateFlow()
    
    private var breathingJob: Job? = null
    private var selectedPattern: BreathingPattern = BreathingPatterns.BOX_BREATHING
    
    /**
     * 🎯 Start breathing guide with selected pattern
     */
    fun startBreathingGuide(pattern: BreathingPattern? = null) {
        pattern?.let { selectedPattern = it }
        
        breathingJob?.cancel()
        
        _breathingState.value = BreathingGuideState(
            isActive = true,
            currentPattern = selectedPattern,
            isGuideVisible = true
        )
        
        breathingJob = viewModelScope.launch {
            runBreathingCycle()
        }
    }
    
    /**
     * ⏸️ Pause breathing guide
     */
    fun pauseBreathingGuide() {
        breathingJob?.cancel()
        _breathingState.value = _breathingState.value.copy(
            isActive = false
        )
    }
    
    /**
     * ⏹️ Stop breathing guide
     */
    fun stopBreathingGuide() {
        breathingJob?.cancel()
        _breathingState.value = BreathingGuideState()
    }
    
    /**
     * 👁️ Toggle breathing guide visibility
     */
    fun toggleBreathingGuide() {
        val currentState = _breathingState.value
        if (currentState.isGuideVisible) {
            stopBreathingGuide()
        } else {
            startBreathingGuide()
        }
    }
    
    /**
     * 🔄 Set breathing pattern
     */
    fun setBreathingPattern(pattern: BreathingPattern) {
        selectedPattern = pattern
        if (_breathingState.value.isActive) {
            startBreathingGuide(pattern)
        }
    }
    
    /**
     * 🌬️ Run the breathing cycle
     */
    private suspend fun runBreathingCycle() {
        var elapsedSeconds = 0
        var cycleCount = 0
        
        while (true) {
            val pattern = selectedPattern
            val elapsedInCycle = elapsedSeconds % pattern.totalCycleSeconds
            
            // Check if we completed a cycle
            if (elapsedInCycle == 0 && elapsedSeconds > 0) {
                cycleCount++
            }
            
            val currentPhase = pattern.getCurrentPhase(elapsedInCycle)
            val phaseProgress = pattern.getPhaseProgress(elapsedInCycle)
            
            _breathingState.value = _breathingState.value.copy(
                elapsedSeconds = elapsedSeconds,
                currentPhase = currentPhase,
                phaseProgress = phaseProgress,
                cycleCount = cycleCount
            )
            
            delay(1000) // Update every second
            elapsedSeconds++
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        breathingJob?.cancel()
    }
}

/**
 * 🎯 COMPOSABLE BREATHING GUIDE MANAGER
 * Use this in your composables to manage breathing guide state
 */
@Composable
fun rememberBreathingGuideManager(): BreathingGuideManager {
    return remember { BreathingGuideManager() }
}

/**
 * 🌟 BREATHING GUIDE INTEGRATION
 * Easy integration with meditation timer
 */
@Composable
fun BreathingGuideIntegration(
    isTimerRunning: Boolean,
    selectedPattern: BreathingPattern?,
    onPatternChange: (BreathingPattern) -> Unit,
    modifier: Modifier = Modifier
) {
    val breathingManager = rememberBreathingGuideManager()
    val breathingState by breathingManager.breathingState.collectAsState()
    
    // Auto-start breathing guide when timer starts
    LaunchedEffect(isTimerRunning, selectedPattern) {
        if (isTimerRunning && selectedPattern != null) {
            breathingManager.startBreathingGuide(selectedPattern)
        } else if (!isTimerRunning) {
            breathingManager.stopBreathingGuide()
        }
    }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Breathing pattern selector (when timer is not running)
        if (!isTimerRunning) {
            com.jonathon.blossom.ui.components.BreathingPatternSelector(
                selectedPattern = selectedPattern,
                onPatternSelected = onPatternChange,
                onCreateCustom = {
                    // TODO: Implement custom pattern creator
                }
            )
        }
        
        // Breathing guide (when active)
        if (breathingState.isGuideVisible) {
            com.jonathon.blossom.ui.components.BreathingGuide(
                state = breathingState,
                onToggleGuide = { breathingManager.toggleBreathingGuide() }
            )
        }
        
        // Quick toggle button
        if (!isTimerRunning) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { breathingManager.toggleBreathingGuide() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        if (breathingState.isGuideVisible) "Hide Guide" else "Show Guide"
                    )
                }
                
                if (selectedPattern != null) {
                    Button(
                        onClick = { 
                            if (breathingState.isActive) {
                                breathingManager.pauseBreathingGuide()
                            } else {
                                breathingManager.startBreathingGuide()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            if (breathingState.isActive) "Pause" else "Start"
                        )
                    }
                }
            }
        }
    }
}

/**
 * 🎨 BREATHING GUIDE PREVIEW STATE
 * For testing and previews
 */
object BreathingGuidePreview {
    fun createSampleState(
        phase: BreathingPhase = BreathingPhase.INHALE,
        progress: Float = 0.5f
    ): BreathingGuideState {
        return BreathingGuideState(
            isActive = true,
            currentPattern = BreathingPatterns.BOX_BREATHING,
            elapsedSeconds = 15,
            currentPhase = phase,
            phaseProgress = progress,
            cycleCount = 2,
            isGuideVisible = true
        )
    }
}
