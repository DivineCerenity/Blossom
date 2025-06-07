package com.example.blossom.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blossom.data.JournalEntry
import com.example.blossom.data.JournalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalListViewModel @Inject constructor(
    private val journalRepository: JournalRepository
) : ViewModel() {

    // Holds all entries from the database
    val entries = journalRepository.getAllEntries()

    // Holds the entry that the user has long-pressed to delete
    private val _entryToDelete = MutableStateFlow<JournalEntry?>(null)
    val entryToDelete = _entryToDelete.asStateFlow()

    fun onDeletionInitiated(entry: JournalEntry) {
        _entryToDelete.value = entry
    }

    fun onDeletionCancelled() {
        _entryToDelete.value = null
    }

    fun onDeletionConfirmed() {
        viewModelScope.launch {
            entryToDelete.value?.let {
                journalRepository.delete(it)
                _entryToDelete.value = null
            }
        }
    }
}