package com.example.blossom.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blossom.data.JournalEntry
import com.example.blossom.data.JournalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalListViewModel @Inject constructor(
    private val journalRepository: JournalRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Holds all entries from the database, filtered by search query
    val entries = combine(
        journalRepository.getAllEntries(),
        searchQuery
    ) { entries, query ->
        entries.filter { entry ->
            query.isEmpty() || entry.title.contains(query, ignoreCase = true) ||
                    entry.content.contains(query, ignoreCase = true)
        }
    }

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

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}