package com.example.blossom.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * The repository is the single source of truth for our app's data.
 * The ViewModels will talk to this file, not directly to the DAO.
 */
class JournalRepository @Inject constructor(private val journalDao: JournalDao) {

    fun getJournalEntries(): Flow<List<JournalEntry>> = journalDao.getAllEntries()

    suspend fun getJournalEntryById(id: Int): JournalEntry? {
        return journalDao.getEntryById(id)
    }

    suspend fun insertJournalEntry(journalEntry: JournalEntry) {
        journalDao.insertJournalEntry(journalEntry)
    }

    suspend fun delete(journalEntry: JournalEntry) {
        journalDao.delete(journalEntry)
    }
}