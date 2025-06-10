package com.example.blossom.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_verses")
data class DailyVerse(
    @PrimaryKey
    val date: String, // Format: "YYYY-MM-DD"
    val verse: String,
    val reference: String,
    val fetchedAt: Long = System.currentTimeMillis()
)
