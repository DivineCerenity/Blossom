package com.example.blossom.ui.prayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blossom.data.PrayerCategory
import com.example.blossom.data.PrayerPriority
import com.example.blossom.data.PrayerRequest
import com.example.blossom.data.PrayerRequestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PrayerRequestsUiState(
    val allPrayerRequests: List<PrayerRequest> = emptyList(),
    val activePrayerRequests: List<PrayerRequest> = emptyList(),
    val answeredPrayerRequests: List<PrayerRequest> = emptyList(),
    val activePrayerCount: Int = 0,
    val answeredPrayerCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PrayerRequestsViewModel @Inject constructor(
    private val repository: PrayerRequestRepository,
    private val analyticsRepository: com.example.blossom.data.AnalyticsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PrayerRequestsUiState())
    val uiState = _uiState.asStateFlow()

    // 🏆 ACHIEVEMENT STATE
    private val _newAchievements = MutableStateFlow<List<com.example.blossom.data.Achievement>>(emptyList())
    val newAchievements: StateFlow<List<com.example.blossom.data.Achievement>> = _newAchievements.asStateFlow()
    
    init {
        loadPrayerRequests()
    }
    
    private fun loadPrayerRequests() {
        viewModelScope.launch {
            combine(
                repository.getAllPrayerRequests(),
                repository.getActivePrayerRequests(),
                repository.getAnsweredPrayerRequests(),
                repository.getActivePrayerCount(),
                repository.getAnsweredPrayerCount()
            ) { all, active, answered, activeCount, answeredCount ->
                PrayerRequestsUiState(
                    allPrayerRequests = all,
                    activePrayerRequests = active,
                    answeredPrayerRequests = answered,
                    activePrayerCount = activeCount,
                    answeredPrayerCount = answeredCount
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
    
    fun addPrayerRequest(
        title: String,
        description: String,
        category: PrayerCategory,
        priority: PrayerPriority
    ) {
        viewModelScope.launch {
            val prayerRequest = PrayerRequest(
                title = title.trim(),
                description = description.trim(),
                category = category,
                priority = priority
            )
            repository.insertPrayerRequest(prayerRequest)

            // 🏆 CHECK FOR PRAYER ACHIEVEMENTS!
            val achievements = analyticsRepository.checkPrayerAchievements()
            if (achievements.isNotEmpty()) {
                _newAchievements.value = achievements
            }
        }
    }
    
    fun toggleAnswered(prayerRequest: PrayerRequest) {
        viewModelScope.launch {
            if (prayerRequest.isAnswered) {
                repository.markAsUnanswered(prayerRequest.id)
            } else {
                repository.markAsAnswered(prayerRequest.id)

                // 🏆 CHECK FOR PRAYER ACHIEVEMENTS WHEN MARKING AS ANSWERED!
                val achievements = analyticsRepository.checkPrayerAchievements()
                if (achievements.isNotEmpty()) {
                    _newAchievements.value = achievements
                }
            }
        }
    }
    
    fun deletePrayerRequest(prayerRequest: PrayerRequest) {
        viewModelScope.launch {
            repository.deletePrayerRequest(prayerRequest)
        }
    }
    
    fun updatePrayerRequest(prayerRequest: PrayerRequest) {
        viewModelScope.launch {
            repository.updatePrayerRequest(prayerRequest)
        }
    }

    /**
     * 🏆 CLEAR ACHIEVEMENTS
     * Call this after showing achievement celebrations
     */
    fun clearAchievements() {
        _newAchievements.value = emptyList()
    }
}
