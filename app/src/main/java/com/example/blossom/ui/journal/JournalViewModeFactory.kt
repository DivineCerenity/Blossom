package com.example.blossom.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.blossom.data.JournalDao

/**
 * A custom factory that knows how to create our Journal-related ViewModels
 * by providing them with the single JournalDao instance.
 */
class JournalViewModelFactory(private val journalDao: JournalDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JournalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JournalViewModel(journalDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}