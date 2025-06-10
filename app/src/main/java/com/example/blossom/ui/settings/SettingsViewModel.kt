package com.example.blossom.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.getSelectedTheme().collect { theme ->
                _uiState.value = _uiState.value.copy(selectedTheme = theme)
            }
        }
    }

    fun selectTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsRepository.saveSelectedTheme(theme)
            _uiState.value = _uiState.value.copy(selectedTheme = theme)
        }
    }
}
