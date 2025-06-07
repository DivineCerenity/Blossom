package com.example.blossom.data

import kotlinx.coroutines.flow.Flow

/**
 * The repository is the single source of truth for our app's data.
 * The ViewModels will talk to this file, not directly to the DAO.
 */
class JournalRepository(private val journalDao: JournalDao) {

    fun getAllEntries(): Flow<List<JournalEntry>> = journalDao.getAllEntries()

    suspend fun getEntryById(id: Int): JournalEntry? = journalDao.getEntryById(id)

    suspend fun insert(entry: JournalEntry) {
        journalDao.insert(entry)
    }

    suspend fun delete(entry: JournalEntry) {
        journalDao.delete(entry)
    }
}