package com.example.blossom.ui.dashboard // Or whatever package it's in

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blossom.data.DailyVerseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// This data class holds the state for your Dashboard/Home screen
data class DashboardUiState(
    val isLoading: Boolean = true,
    val verseText: String = "",
    val verseReference: String = "",
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dailyVerseRepository: DailyVerseRepository // Inject the repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchVerse()
    }

    fun fetchVerse() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            dailyVerseRepository.getTodaysVerse()
                .onSuccess { dailyVerse ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            verseText = dailyVerse.verse,
                            verseReference = dailyVerse.reference
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to load verse. Please check your connection."
                        )
                    }
                }
        }
    }
}