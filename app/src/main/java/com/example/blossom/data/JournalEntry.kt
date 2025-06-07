package com.example.blossom.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val content: String,
    val creationTimestamp: Long, // <--- ADDED
    val lastModifiedTimestamp: Long, // <--- RENAMED from timestamp
    val mood: String,
    val is_favorited: Boolean = false
)