package com.example.blossom.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blossom.data.JournalEntry
import com.example.blossom.data.JournalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditJournalUiState(
    val currentEntryId: Int? = null,
    val title: String = "",
    val content: String = "",
    val selectedMood: String = "Happy",
    val isFavorited: Boolean = false,
    val creationTimestamp: Long = 0L,
    val isEditing: Boolean = false,
    val shouldNavigateBack: Boolean = false,
    val imageUri: String? = null
)

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val journalRepository: JournalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditJournalUiState())
    val uiState = _uiState.asStateFlow()

    fun onTitleChanged(newTitle: String) { _uiState.update { it.copy(title = newTitle) } }
    fun onContentChanged(newContent: String) { _uiState.update { it.copy(content = newContent) } }
    fun onMoodSelected(newMood: String) { _uiState.update { it.copy(selectedMood = newMood) } }
    fun onFavoriteToggled() { _uiState.update { it.copy(isFavorited = !it.isFavorited) } }
    fun onImageUriChanged(newUri: String?) { _uiState.update { it.copy(imageUri = newUri) } }

    fun loadEntry(entryId: Int) {
        if (entryId == -1) {
            _uiState.value = AddEditJournalUiState()
            return
        }
        viewModelScope.launch {
            val entry = journalRepository.getEntryById(entryId)
            if (entry != null) {
                _uiState.update {
                    it.copy(
                        currentEntryId = entry.id,
                        title = entry.title,
                        content = entry.content,
                        selectedMood = entry.mood,
                        isFavorited = entry.is_favorited,
                        creationTimestamp = entry.creationTimestamp,
                        isEditing = true,
                        imageUri = entry.imageUri
                    )
                }
            }
        }
    }

    fun saveJournalEntry() {
        val currentState = _uiState.value
        if (currentState.title.isBlank()) return

        val entryToSave = JournalEntry(
            id = currentState.currentEntryId ?: 0,
            title = currentState.title,
            content = currentState.content,
            creationTimestamp = if (currentState.isEditing) currentState.creationTimestamp else System.currentTimeMillis(),
            lastModifiedTimestamp = System.currentTimeMillis(),
            mood = currentState.selectedMood,
            is_favorited = currentState.isFavorited,
            imageUri = currentState.imageUri
        )

        viewModelScope.launch {
            journalRepository.insert(entryToSave)
            _uiState.update { it.copy(shouldNavigateBack = true) }
        }
    }

    fun eventHandled() { _uiState.update { it.copy(shouldNavigateBack = false) } }

    fun deleteImage() {
        _uiState.update { it.copy(imageUri = null) }
        val currentState = _uiState.value
        if (currentState.currentEntryId != null && currentState.isEditing) {
            val updatedEntry = JournalEntry(
                id = currentState.currentEntryId,
                title = currentState.title,
                content = currentState.content,
                creationTimestamp = currentState.creationTimestamp,
                lastModifiedTimestamp = System.currentTimeMillis(),
                mood = currentState.selectedMood,
                is_favorited = currentState.isFavorited,
                imageUri = null
            )
            viewModelScope.launch {
                journalRepository.insert(updatedEntry)
            }
        }
    }
}