package com.example.blossom.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 📊 ANALYTICS REPOSITORY
 * Business logic for meditation analytics and insights
 */
@Singleton
class AnalyticsRepository @Inject constructor(
    private val analyticsDao: AnalyticsDao,
    private val journalDao: JournalDao,
    private val prayerDao: PrayerRequestDao
) {
    
    // 🧘‍♂️ MEDITATION SESSION TRACKING
    suspend fun recordMeditationSession(
        startTime: Long,
        endTime: Long,
        duration: Int,
        breathingPattern: String,
        binauralBeat: String?,
        backgroundSound: String?,
        theme: String,
        completed: Boolean
    ) {
        val session = MeditationSession(
            startTime = startTime,
            endTime = endTime,
            duration = duration,
            breathingPattern = breathingPattern,
            binauralBeat = binauralBeat,
            backgroundSound = backgroundSound,
            theme = theme,
            completed = completed
        )
        
        analyticsDao.insertMeditationSession(session)
        
        // Update daily analytics
        updateDailyAnalytics(Date(startTime))
        
        // Check for achievement unlocks
        checkAchievements()
    }
    
    // 📈 GET MEDITATION STATISTICS
    suspend fun getMeditationStats(): MeditationStats {
        val currentStreak = analyticsDao.getCurrentStreak()
        val totalSessions = analyticsDao.getTotalCompletedSessions()
        val totalTime = analyticsDao.getTotalMeditationTime() ?: 0
        val averageTime = analyticsDao.getAverageMeditationTime() ?: 0
        val favoritePattern = analyticsDao.getFavoriteBreathingPattern()
        val favoriteBeat = analyticsDao.getFavoriteBinauralBeat()
        val favoriteTheme = analyticsDao.getFavoriteTheme()
        
        // Calculate this week's stats
        val weekStart = getWeekStartTime()
        val weekEnd = getWeekEndTime()
        val weekSessions = analyticsDao.getMeditationSessionsInRange(weekStart, weekEnd)
        val sessionsThisWeek = weekSessions.filter { it.completed }.size
        val timeThisWeek = weekSessions.filter { it.completed }.sumOf { it.duration }
        
        // Calculate longest streak (simplified - could be enhanced)
        val longestStreak = currentStreak // For now, using current as longest
        
        return MeditationStats(
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            totalSessions = totalSessions,
            totalTime = totalTime,
            averageSessionLength = averageTime,
            sessionsThisWeek = sessionsThisWeek,
            timeThisWeek = timeThisWeek,
            favoritePattern = favoritePattern,
            favoriteBinauralBeat = favoriteBeat,
            favoriteTheme = favoriteTheme
        )
    }
    
    // 📅 GET WEEKLY MEDITATION DATA
    suspend fun getWeeklyData(): WeeklyData {
        val weekStart = getWeekStartTime()
        val weekEnd = getWeekEndTime()
        val dailyData = analyticsDao.getWeeklyMeditationData(weekStart, weekEnd)
        
        // Create complete week data (fill missing days with 0)
        val dates = mutableListOf<String>()
        val times = mutableListOf<Int>()
        val counts = mutableListOf<Int>()
        
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = weekStart
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        for (i in 0..6) {
            val dateStr = dateFormat.format(calendar.time)
            dates.add(dateStr)
            
            val dayData = dailyData.find { it.date == dateStr }
            times.add(dayData?.totalTime ?: 0)
            counts.add(dayData?.sessionCount ?: 0)
            
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        return WeeklyData(dates, times, counts)
    }
    
    // 📝 GET JOURNAL INSIGHTS
    suspend fun getJournalInsights(): JournalInsights {
        val allEntries = journalDao.getAllEntries().first()
        val totalEntries = allEntries.size

        val weekStart = getWeekStartTime()
        val entriesThisWeek = allEntries.count { it.creationTimestamp >= weekStart }

        // Calculate mood trends (simplified)
        val moodCounts = allEntries.groupBy { it.mood }.mapValues { it.value.size }
        val mostCommonMood = moodCounts.maxByOrNull { it.value }?.key

        // Create mood trend data (last 7 days)
        val moodTrends = createMoodTrends(allEntries)

        return JournalInsights(
            totalEntries = totalEntries,
            entriesThisWeek = entriesThisWeek,
            mostCommonMood = mostCommonMood,
            moodTrends = moodTrends
        )
    }
    
    // 🙏 GET PRAYER INSIGHTS
    suspend fun getPrayerInsights(): PrayerInsights {
        val allPrayers = prayerDao.getAllPrayerRequests().first()
        val totalPrayers = allPrayers.size
        val answeredPrayers = allPrayers.count { it.isAnswered }
        val answeredPercentage = if (totalPrayers > 0) (answeredPrayers.toFloat() / totalPrayers) * 100 else 0f

        val weekStart = getWeekStartTime()
        val prayersThisWeek = allPrayers.count { it.createdDate >= weekStart }
        val answeredThisWeek = allPrayers.count { it.isAnswered && it.createdDate >= weekStart }

        val categoryCounts = allPrayers.groupBy { it.category }.mapValues { it.value.size }
        val mostCommonCategory = categoryCounts.maxByOrNull { it.value }?.key?.displayName

        return PrayerInsights(
            totalPrayers = totalPrayers,
            answeredPrayers = answeredPrayers,
            answeredPercentage = answeredPercentage,
            prayersThisWeek = prayersThisWeek,
            answeredThisWeek = answeredThisWeek,
            mostCommonCategory = mostCommonCategory
        )
    }
    
    // 🏆 ACHIEVEMENT OPERATIONS
    fun getAllAchievements(): Flow<List<Achievement>> = analyticsDao.getAllAchievements()
    fun getUnlockedAchievements(): Flow<List<Achievement>> = analyticsDao.getUnlockedAchievements()
    
    suspend fun initializeAchievements() {
        // Initialize default achievements if they don't exist
        val achievements = listOf(
            Achievement("first_meditation", "First Steps", "Complete your first meditation", "🧘‍♂️", null, AchievementCategory.MEDITATION_COUNT, 1),
            Achievement("week_warrior", "Week Warrior", "Meditate for 7 days in a row", "🔥", null, AchievementCategory.MEDITATION_STREAK, 7),
            Achievement("zen_master", "Zen Master", "Complete 100 meditation sessions", "🏆", null, AchievementCategory.MEDITATION_COUNT, 100),
            Achievement("deep_diver", "Deep Diver", "Meditate for 60+ minutes in one session", "🌊", null, AchievementCategory.MEDITATION_TIME, 3600),
            Achievement("pattern_explorer", "Pattern Explorer", "Try all breathing patterns", "🌬️", null, AchievementCategory.PATTERN_EXPLORER, 9),
            Achievement("frequency_finder", "Frequency Finder", "Try all binaural beats", "🧠", null, AchievementCategory.FREQUENCY_FINDER, 13)
        )

        achievements.forEach { achievement ->
            analyticsDao.insertOrUpdateAchievement(achievement)
        }
    }

    /**
     * 🔄 RESET ALL ANALYTICS DATA
     * Clear all meditation sessions, achievements, and daily analytics
     */
    suspend fun resetAllStats() {
        analyticsDao.deleteAllMeditationSessions()
        analyticsDao.deleteAllDailyAnalytics()
        analyticsDao.deleteAllAchievements()

        // Re-initialize achievements
        initializeAchievements()
    }
    
    // 🔥 PRIVATE HELPER METHODS
    private suspend fun updateDailyAnalytics(date: Date) {
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        val existing = analyticsDao.getDailyAnalytics(dateStr)
        
        val startOfDay = getStartOfDay(date).time
        val endOfDay = getEndOfDay(date).time
        
        val sessions = analyticsDao.getMeditationSessionsInRange(startOfDay, endOfDay)
        val completedSessions = sessions.filter { it.completed }
        val totalTime = completedSessions.sumOf { it.duration }
        
        val journalEntries = journalDao.getAllEntries().first()
            .count { it.creationTimestamp >= startOfDay && it.creationTimestamp <= endOfDay }

        val prayers = prayerDao.getAllPrayerRequests().first()
        val prayersAdded = prayers.count { it.createdDate >= startOfDay && it.createdDate <= endOfDay }
        val prayersAnswered = prayers.count { it.isAnswered && it.createdDate >= startOfDay && it.createdDate <= endOfDay }
        
        val analytics = DailyAnalytics(
            date = dateStr,
            meditationTime = totalTime,
            meditationSessions = completedSessions.size,
            journalEntries = journalEntries,
            prayersAdded = prayersAdded,
            prayersAnswered = prayersAnswered
        )
        
        analyticsDao.insertOrUpdateDailyAnalytics(analytics)
    }
    
    private suspend fun checkAchievements() {
        val stats = getMeditationStats()
        
        // Check meditation count achievements
        if (stats.totalSessions >= 1) {
            analyticsDao.unlockAchievement("first_meditation", System.currentTimeMillis())
        }
        if (stats.totalSessions >= 100) {
            analyticsDao.unlockAchievement("zen_master", System.currentTimeMillis())
        }
        
        // Check streak achievements
        if (stats.currentStreak >= 7) {
            analyticsDao.unlockAchievement("week_warrior", System.currentTimeMillis())
        }
    }
    
    private fun createMoodTrends(entries: List<JournalEntry>): List<MoodTrendData> {
        // Simplified mood trends for last 7 days
        return emptyList() // TODO: Implement detailed mood trends
    }
    
    private fun getWeekStartTime(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    private fun getWeekEndTime(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
    
    private fun getStartOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
    
    private fun getEndOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.time
    }
}
