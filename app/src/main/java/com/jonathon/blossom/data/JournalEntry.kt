package com.jonathon.blossom.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val mood: String,
    val creationTimestamp: Long,
    val imageUrl: String? = null, // Keep for backward compatibility
    val imageUrls: String = "", // Pipe-separated image URLs
    val featuredImageUrl: String? = null // URL of the featured image for the card
)