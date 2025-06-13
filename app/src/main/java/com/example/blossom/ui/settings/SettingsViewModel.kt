package com.example.blossom.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blossom.data.AnalyticsRepository
import com.example.blossom.data.Achievement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    // 🏆 ACHIEVEMENT STATE
    private val _newAchievements = MutableStateFlow<List<Achievement>>(emptyList())
    val newAchievements: StateFlow<List<Achievement>> = _newAchievements.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.getSelectedTheme().collect { theme ->
                _uiState.value = _uiState.value.copy(selectedTheme = theme)
            }
        }
        viewModelScope.launch {
            settingsRepository.getDarkMode().collect { isDarkMode ->
                _uiState.value = _uiState.value.copy(isDarkMode = isDarkMode)
            }
        }
    }

    fun selectTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsRepository.saveSelectedTheme(theme)
            _uiState.value = _uiState.value.copy(selectedTheme = theme)

            // 🏆 RECORD THEME CHANGE AND CHECK FOR ACHIEVEMENTS!
            val achievements = analyticsRepository.recordThemeChange(theme.displayName)
            if (achievements.isNotEmpty()) {
                _newAchievements.value = achievements
            }
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            val newDarkMode = !_uiState.value.isDarkMode
            settingsRepository.saveDarkMode(newDarkMode)
            _uiState.value = _uiState.value.copy(isDarkMode = newDarkMode)
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
