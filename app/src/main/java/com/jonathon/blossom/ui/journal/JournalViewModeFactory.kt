package com.jonathon.blossom.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jonathon.blossom.data.JournalDao
import com.jonathon.blossom.data.AnalyticsRepository

/**
 * A custom factory that knows how to create our Journal-related ViewModels
 * by providing them with the required dependencies.
 */
class JournalViewModelFactory(
    private val journalDao: JournalDao,
    private val analyticsRepository: AnalyticsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JournalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JournalViewModel(journalDao, analyticsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}