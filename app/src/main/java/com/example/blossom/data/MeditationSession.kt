package com.example.blossom.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * 🧘‍♂️ MEDITATION SESSION ENTITY
 * Tracks every meditation session for beautiful analytics
 */
@Entity(tableName = "meditation_sessions")
data class MeditationSession(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val startTime: Long,
    val endTime: Long,
    val duration: Int, // in seconds
    val breathingPattern: String,
    val binauralBeat: String?,
    val backgroundSound: String?,
    val theme: String,
    val completed: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 📊 DAILY ANALYTICS SUMMARY
 * Aggregated daily statistics for quick insights
 */
@Entity(tableName = "daily_analytics")
data class DailyAnalytics(
    @PrimaryKey val date: String, // "2024-01-15"
    val meditationTime: Int, // total seconds
    val meditationSessions: Int,
    val journalEntries: Int,
    val prayersAdded: Int,
    val prayersAnswered: Int,
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * 🏆 ACHIEVEMENT ENTITY
 * Track unlocked achievements and milestones
 */
@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val unlockedAt: Long?,
    val category: AchievementCategory,
    val threshold: Int, // The value needed to unlock
    val currentProgress: Int = 0
)

/**
 * 🎯 ACHIEVEMENT CATEGORIES
 */
enum class AchievementCategory {
    MEDITATION_STREAK,
    MEDITATION_TIME,
    MEDITATION_COUNT,
    JOURNAL_ENTRIES,
    PRAYERS_ANSWERED,
    PATTERN_EXPLORER,
    FREQUENCY_FINDER
}

/**
 * 📈 ANALYTICS DATA CLASSES FOR UI
 */
data class MeditationStats(
    val currentStreak: Int,
    val longestStreak: Int,
    val totalSessions: Int,
    val totalTime: Int, // in seconds
    val averageSessionLength: Int, // in seconds
    val sessionsThisWeek: Int,
    val timeThisWeek: Int,
    val favoritePattern: String?,
    val favoriteBinauralBeat: String?,
    val favoriteTheme: String?
)

data class WeeklyData(
    val dates: List<String>,
    val meditationTimes: List<Int>, // seconds per day
    val sessionCounts: List<Int>
)

data class JournalInsights(
    val totalEntries: Int,
    val entriesThisWeek: Int,
    val mostCommonMood: String?,
    val moodTrends: List<MoodTrendData>
)

data class MoodTrendData(
    val date: String,
    val mood: String,
    val count: Int
)

data class PrayerInsights(
    val totalPrayers: Int,
    val answeredPrayers: Int,
    val answeredPercentage: Float,
    val prayersThisWeek: Int,
    val answeredThisWeek: Int,
    val mostCommonCategory: String?
)
