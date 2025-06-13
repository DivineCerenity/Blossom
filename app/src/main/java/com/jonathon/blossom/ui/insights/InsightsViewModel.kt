package com.jonathon.blossom.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jonathon.blossom.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 📊 INSIGHTS VIEW MODEL
 * Manages analytics data and UI state
 */
@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()
    
    init {
        loadAnalyticsData()
        initializeAchievements()
    }
    
    private fun loadAnalyticsData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // Load all analytics data
                val meditationStats = analyticsRepository.getMeditationStats()
                val weeklyData = analyticsRepository.getWeeklyData()
                val journalInsights = analyticsRepository.getJournalInsights()
                val prayerInsights = analyticsRepository.getPrayerInsights()
                
                // Collect achievements
                analyticsRepository.getAllAchievements().collect { achievements ->
                    _uiState.value = _uiState.value.copy(
                        meditationStats = meditationStats,
                        weeklyData = weeklyData,
                        journalInsights = journalInsights,
                        prayerInsights = prayerInsights,
                        achievements = achievements,
                        isLoading = false,
                        error = null
                    )
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load analytics data"
                )
            }
        }
    }
    
    private fun initializeAchievements() {
        viewModelScope.launch {
            analyticsRepository.initializeAchievements()
        }
    }
    
    fun refreshData() {
        loadAnalyticsData()
    }

    /**
     * 🔄 Reset all analytics data
     */
    fun resetAllStats() {
        viewModelScope.launch {
            try {
                analyticsRepository.resetAllStats()
                loadAnalyticsData() // Refresh after reset
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to reset statistics"
                )
            }
        }
    }
    
    /**
     * Record a meditation session for analytics and return newly unlocked achievements
     */
    suspend fun recordMeditationSession(
        startTime: Long,
        endTime: Long,
        duration: Int,
        breathingPattern: String,
        binauralBeat: String?,
        backgroundSound: String?,
        theme: String,
        completed: Boolean
    ): List<Achievement> {
        val newAchievements = analyticsRepository.recordMeditationSession(
            startTime = startTime,
            endTime = endTime,
            duration = duration,
            breathingPattern = breathingPattern,
            binauralBeat = binauralBeat,
            backgroundSound = backgroundSound,
            theme = theme,
            completed = completed
        )

        // Refresh data after recording
        loadAnalyticsData()

        return newAchievements
    }

    /**
     * Record a meditation session and handle achievements with callback
     */
    fun recordMeditationSessionWithAchievements(
        startTime: Long,
        endTime: Long,
        duration: Int,
        breathingPattern: String,
        binauralBeat: String?,
        backgroundSound: String?,
        theme: String,
        completed: Boolean,
        onAchievementsUnlocked: (List<Achievement>) -> Unit
    ) {
        viewModelScope.launch {
            val newAchievements = recordMeditationSession(
                startTime = startTime,
                endTime = endTime,
                duration = duration,
                breathingPattern = breathingPattern,
                binauralBeat = binauralBeat,
                backgroundSound = backgroundSound,
                theme = theme,
                completed = completed
            )

            // Call the callback with new achievements
            onAchievementsUnlocked(newAchievements)
        }
    }
}

/**
 * 📊 INSIGHTS UI STATE
 * Holds all analytics data for the UI
 */
data class InsightsUiState(
    val meditationStats: MeditationStats = MeditationStats(
        currentStreak = 0,
        longestStreak = 0,
        totalSessions = 0,
        totalTime = 0,
        averageSessionLength = 0,
        sessionsThisWeek = 0,
        timeThisWeek = 0,
        favoritePattern = null,
        favoriteBinauralBeat = null,
        favoriteTheme = null
    ),
    val weeklyData: WeeklyData = WeeklyData(
        dates = emptyList(),
        meditationTimes = emptyList(),
        sessionCounts = emptyList()
    ),
    val journalInsights: JournalInsights = JournalInsights(
        totalEntries = 0,
        entriesThisWeek = 0,
        mostCommonMood = null,
        moodTrends = emptyList()
    ),
    val prayerInsights: PrayerInsights = PrayerInsights(
        totalPrayers = 0,
        answeredPrayers = 0,
        answeredPercentage = 0f,
        prayersThisWeek = 0,
        answeredThisWeek = 0,
        mostCommonCategory = null
    ),
    val achievements: List<Achievement> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
