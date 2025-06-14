package com.jonathon.blossom.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {

    @Query("SELECT * FROM journal_entries ORDER BY creationTimestamp DESC")
    fun getAllEntries(): Flow<List<JournalEntry>>

    @Query("SELECT * FROM journal_entries ORDER BY creationTimestamp DESC")
    suspend fun getAllEntriesSync(): List<JournalEntry>

    // Inserts a new entry. If an entry with the same ID exists, it replaces it.
    // This handles both "add new" and "update existing" perfectly.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<JournalEntry>)

    // Deletes an entry.
    @Delete
    suspend fun delete(entry: JournalEntry)

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun getEntryById(id: Int): JournalEntry?

    @Query("SELECT COUNT(*) FROM journal_entries")
    suspend fun getEntryCount(): Int
}
