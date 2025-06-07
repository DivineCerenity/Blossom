package com.example.blossom.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_habits")
data class DailyHabit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val reminderTime: Long = 0, // Time in milliseconds for daily reminder
    val isCompleted: Boolean = false,
    val isEnabled: Boolean = true,
    val streakCount: Int = 0,
    val lastCompletedDate: Long = 0 // Timestamp of last completion
) 