package com.example.blossom.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {


    @Query("SELECT * FROM journal_entries ORDER BY is_favorited DESC, creationTimestamp DESC")
    fun getAllEntries(): Flow<List<JournalEntry>>

    // Gets a single entry by its ID for the edit screen
    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun getEntryById(id: Int): JournalEntry?

    // Inserts a new entry. If an entry with the same ID exists, it replaces it.
    // This handles both "add new" and "update existing" perfectly.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: JournalEntry)

    // Deletes an entry.
    @Delete
    suspend fun delete(entry: JournalEntry)
}