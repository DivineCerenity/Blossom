package com.example.blossom.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.blossom.data.JournalRepository

/**
 * A custom factory that knows how to create our Journal-related ViewModels
 * by providing them with the single JournalRepository instance.
 */
class JournalViewModelFactory(private val repository: JournalRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JournalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JournalViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(JournalListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JournalListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}