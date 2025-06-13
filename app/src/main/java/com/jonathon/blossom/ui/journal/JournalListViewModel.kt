package com.jonathon.blossom.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jonathon.blossom.data.JournalEntry
import com.jonathon.blossom.data.JournalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalListViewModel @Inject constructor(
    private val journalRepository: JournalRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.NEWEST)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val entries: StateFlow<List<JournalEntry>> = combine(
        journalRepository.getJournalEntries(),
        _searchQuery,
        _sortOption
    ) { entries, query, sortOption ->
        _isLoading.value = false
        entries
            .filter { entry ->
                query.isEmpty() || entry.title.contains(query, ignoreCase = true) ||
                        entry.content.contains(query, ignoreCase = true)
            }
            .let { filteredEntries ->
                when (sortOption) {
                    SortOption.NEWEST -> filteredEntries.sortedByDescending { it.creationTimestamp }
                    SortOption.OLDEST -> filteredEntries.sortedBy { it.creationTimestamp }
                    SortOption.HAPPY_FIRST -> filteredEntries.sortedWith(
                        compareByDescending<JournalEntry> { it.mood == "Happy" }
                            .thenByDescending { it.creationTimestamp }
                    )
                    SortOption.GRATEFUL_FIRST -> filteredEntries.sortedWith(
                        compareByDescending<JournalEntry> { it.mood == "Grateful" }
                            .thenByDescending { it.creationTimestamp }
                    )
                    SortOption.EXCITED_FIRST -> filteredEntries.sortedWith(
                        compareByDescending<JournalEntry> { it.mood == "Excited" }
                            .thenByDescending { it.creationTimestamp }
                    )
                    SortOption.NEUTRAL_FIRST -> filteredEntries.sortedWith(
                        compareByDescending<JournalEntry> { it.mood == "Neutral" }
                            .thenByDescending { it.creationTimestamp }
                    )
                    SortOption.SAD_FIRST -> filteredEntries.sortedWith(
                        compareByDescending<JournalEntry> { it.mood == "Sad" }
                            .thenByDescending { it.creationTimestamp }
                    )
                }
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        _isLoading.value = true
        viewModelScope.launch {
            journalRepository.getJournalEntries().collect()
            _isLoading.value = false
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
            _isLoading.value = true
            entryToDelete.value?.let {
                journalRepository.delete(it)
                _entryToDelete.value = null
            }
            _isLoading.value = false
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSortOptionSelected(option: SortOption) {
        _sortOption.value = option
    }
}