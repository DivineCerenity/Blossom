package com.example.blossom.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blossom.data.JournalDao
import com.example.blossom.data.JournalEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val journalDao: JournalDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditJournalUiState())
    val uiState: StateFlow<AddEditJournalUiState> = _uiState.asStateFlow()

    fun insertJournalEntry(title: String, content: String, mood: String, imageUrl: String?) {
        viewModelScope.launch {
            val journalEntry = JournalEntry(
                title = title,
                content = content,
                mood = mood,
                creationTimestamp = System.currentTimeMillis(),
                imageUrl = imageUrl
            )
            journalDao.insertJournalEntry(journalEntry)
        }
    }

    fun updateJournalEntry(id: Int, title: String, content: String, mood: String, imageUrl: String?) {
        viewModelScope.launch {
            val updatedJournalEntry = JournalEntry(
                id = id,
                title = title,
                content = content,
                mood = mood,
                creationTimestamp = System.currentTimeMillis(),
                imageUrl = imageUrl
            )
            journalDao.insertJournalEntry(updatedJournalEntry) // Assuming insert is also used for updates
        }
    }

    fun getAllJournalEntries() = journalDao.getAllEntries()

    fun onTitleChanged(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun onContentChanged(content: String) {
        _uiState.value = _uiState.value.copy(content = content)
    }

    fun onMoodSelected(mood: String) {
        _uiState.value = _uiState.value.copy(mood = mood)
    }

    fun onImageUriChanged(imageUri: String?) {
        _uiState.value = _uiState.value.copy(imageUrl = imageUri)
    }

    fun onAddImage(imageUri: String) {
        val currentImages = _uiState.value.imageUrls.toMutableList()
        currentImages.add(imageUri)

        // If this is the first image, set it as featured
        val featuredImage = _uiState.value.featuredImageUrl ?: imageUri

        _uiState.value = _uiState.value.copy(
            imageUrl = if (_uiState.value.imageUrl == null) imageUri else _uiState.value.imageUrl, // Keep for backward compatibility
            imageUrls = currentImages,
            featuredImageUrl = featuredImage
        )
    }

    fun onDeleteImage(imageUri: String) {
        val currentImages = _uiState.value.imageUrls.toMutableList()
        currentImages.remove(imageUri)

        // If the deleted image was the featured image, clear it or set a new one
        val newFeaturedImage = if (_uiState.value.featuredImageUrl == imageUri) {
            currentImages.firstOrNull() // Set first remaining image as featured, or null if none
        } else {
            _uiState.value.featuredImageUrl
        }

        _uiState.value = _uiState.value.copy(
            imageUrls = currentImages,
            featuredImageUrl = newFeaturedImage
        )
    }

    fun onSetFeaturedImage(imageUri: String) {
        _uiState.value = _uiState.value.copy(featuredImageUrl = imageUri)
    }

    fun saveJournalEntry() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val imageUrlsString = currentState.imageUrls.joinToString("|")

            val journalEntry = JournalEntry(
                id = currentState.id,
                title = currentState.title,
                content = currentState.content,
                mood = currentState.mood,
                creationTimestamp = System.currentTimeMillis(),
                imageUrl = currentState.imageUrl, // Keep for backward compatibility
                imageUrls = imageUrlsString,
                featuredImageUrl = currentState.featuredImageUrl
            )

            journalDao.insertJournalEntry(journalEntry)
            _uiState.value = _uiState.value.copy(shouldNavigateBack = true)
        }
    }

    fun deleteImage() {
        _uiState.value = _uiState.value.copy(imageUrl = null)
    }

    fun eventHandled() {
        _uiState.value = _uiState.value.copy(shouldNavigateBack = false)
    }

    fun loadEntry(id: Int) {
        viewModelScope.launch {
            val entry = journalDao.getEntryById(id)
            entry?.let {
                val imageUrls = if (it.imageUrls.isNotEmpty()) {
                    it.imageUrls.split("|").filter { url -> url.isNotBlank() }
                } else {
                    // Fallback to single image for backward compatibility
                    if (it.imageUrl != null) listOf(it.imageUrl) else emptyList()
                }

                _uiState.value = AddEditJournalUiState(
                    id = it.id,
                    title = it.title,
                    content = it.content,
                    mood = it.mood,
                    imageUrl = it.imageUrl,
                    imageUrls = imageUrls,
                    featuredImageUrl = it.featuredImageUrl
                )
            }
        }
    }
}