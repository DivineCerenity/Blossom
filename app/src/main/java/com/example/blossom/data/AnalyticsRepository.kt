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
        val totalSessions = analyticsDao.getTotalSessions() // 🎯 NOW INCLUDES ALL SESSIONS!
        val totalTime = analyticsDao.getTotalMeditationTime() ?: 0
        val averageTime = analyticsDao.getAverageMeditationTime() ?: 0
        val favoritePattern = analyticsDao.getFavoriteBreathingPattern()
        val favoriteBeat = analyticsDao.getFavoriteBinauralBeat()
        val favoriteTheme = analyticsDao.getFavoriteTheme()

        // Calculate this week's stats (including all sessions)
        val weekStart = getWeekStartTime()
        val weekEnd = getWeekEndTime()
        val weekSessions = analyticsDao.getMeditationSessionsInRange(weekStart, weekEnd)
        val sessionsThisWeek = weekSessions.size // 🎯 ALL SESSIONS THIS WEEK!
        val timeThisWeek = weekSessions.sumOf { it.duration } // 🎯 ALL TIME THIS WEEK!
        
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
        val totalTime = sessions.sumOf { it.duration } // 🎯 INCLUDE ALL SESSION TIME!
        
        val journalEntries = journalDao.getAllEntries().first()
            .count { it.creationTimestamp >= startOfDay && it.creationTimestamp <= endOfDay }

        val prayers = prayerDao.getAllPrayerRequests().first()
        val prayersAdded = prayers.count { it.createdDate >= startOfDay && it.createdDate <= endOfDay }
        val prayersAnswered = prayers.count { it.isAnswered && it.createdDate >= startOfDay && it.createdDate <= endOfDay }
        
        val analytics = DailyAnalytics(
            date = dateStr,
            meditationTime = totalTime,
            meditationSessions = sessions.size, // 🎯 ALL SESSIONS (including stopped)
            journalEntries = journalEntries,
            prayersAdded = prayersAdded,
            prayersAnswered = prayersAnswered
        )
        
        analyticsDao.insertOrUpdateDailyAnalytics(analytics)
    }
    
    private suspend fun checkAchievements() {
        val stats = getMeditationStats()
        val journalInsights = getJournalInsights()
        val prayerInsights = getPrayerInsights()

        // 🧘‍♂️ MEDITATION COUNT ACHIEVEMENTS
        checkAndUnlock("first_meditation", stats.totalSessions >= 1)
        checkAndUnlock("meditation_explorer", stats.totalSessions >= 5)
        checkAndUnlock("meditation_enthusiast", stats.totalSessions >= 10)
        checkAndUnlock("meditation_devotee", stats.totalSessions >= 25)
        checkAndUnlock("meditation_master", stats.totalSessions >= 50)
        checkAndUnlock("zen_master", stats.totalSessions >= 100)
        checkAndUnlock("enlightened_soul", stats.totalSessions >= 250)
        checkAndUnlock("meditation_legend", stats.totalSessions >= 500)

        // 🔥 MEDITATION STREAK ACHIEVEMENTS
        checkAndUnlock("streak_starter", stats.currentStreak >= 3)
        checkAndUnlock("week_warrior", stats.currentStreak >= 7)
        checkAndUnlock("fortnight_fighter", stats.currentStreak >= 14)
        checkAndUnlock("month_master", stats.currentStreak >= 30)
        checkAndUnlock("season_sage", stats.currentStreak >= 90)
        checkAndUnlock("year_yogi", stats.currentStreak >= 365)

        // ⏰ MEDITATION TIME ACHIEVEMENTS (in minutes)
        val totalMinutes = stats.totalTime / 60
        checkAndUnlock("first_hour", totalMinutes >= 60)
        checkAndUnlock("time_traveler", totalMinutes >= 300) // 5 hours
        checkAndUnlock("mindful_marathon", totalMinutes >= 600) // 10 hours
        checkAndUnlock("zen_zone", totalMinutes >= 1200) // 20 hours
        checkAndUnlock("meditation_mountain", totalMinutes >= 3000) // 50 hours
        checkAndUnlock("enlightenment_peak", totalMinutes >= 6000) // 100 hours

        // 📝 JOURNAL ACHIEVEMENTS
        checkAndUnlock("first_thoughts", journalInsights.totalEntries >= 1)
        checkAndUnlock("storyteller", journalInsights.totalEntries >= 10)
        checkAndUnlock("memory_keeper", journalInsights.totalEntries >= 25)
        checkAndUnlock("life_chronicler", journalInsights.totalEntries >= 50)
        checkAndUnlock("wisdom_writer", journalInsights.totalEntries >= 100)

        // 🙏 PRAYER ACHIEVEMENTS
        checkAndUnlock("first_prayer", prayerInsights.totalPrayers >= 1)
        checkAndUnlock("faithful_heart", prayerInsights.totalPrayers >= 10)
        checkAndUnlock("prayer_warrior", prayerInsights.totalPrayers >= 25)
        checkAndUnlock("spiritual_guardian", prayerInsights.totalPrayers >= 50)
        checkAndUnlock("divine_messenger", prayerInsights.answeredPrayers >= 5)
        checkAndUnlock("miracle_witness", prayerInsights.answeredPrayers >= 10)

        // 🎨 EXPLORATION ACHIEVEMENTS
        checkAndUnlock("pattern_explorer", stats.favoritePattern != null)
        checkAndUnlock("frequency_finder", stats.favoriteBinauralBeat != null)
        checkAndUnlock("theme_wanderer", stats.favoriteTheme != null)
    }

    private suspend fun checkAndUnlock(achievementId: String, condition: Boolean) {
        if (condition) {
            analyticsDao.unlockAchievement(achievementId, System.currentTimeMillis())
        }
    }

    /**
     * 🏆 INITIALIZE ALL ACHIEVEMENTS
     * Call this when the app starts to ensure all achievements exist
     */
    suspend fun initializeAchievements() {
        val achievements = listOf(
            // 🧘‍♂️ MEDITATION COUNT ACHIEVEMENTS
            Achievement("first_meditation", "First Steps", "Complete your first meditation session", "🌱", null, AchievementCategory.MEDITATION_COUNT, 1),
            Achievement("meditation_explorer", "Explorer", "Complete 5 meditation sessions", "🗺️", null, AchievementCategory.MEDITATION_COUNT, 5),
            Achievement("meditation_enthusiast", "Enthusiast", "Complete 10 meditation sessions", "⭐", null, AchievementCategory.MEDITATION_COUNT, 10),
            Achievement("meditation_devotee", "Devotee", "Complete 25 meditation sessions", "🙏", null, AchievementCategory.MEDITATION_COUNT, 25),
            Achievement("meditation_master", "Master", "Complete 50 meditation sessions", "🎯", null, AchievementCategory.MEDITATION_COUNT, 50),
            Achievement("zen_master", "Zen Master", "Complete 100 meditation sessions", "🧘‍♂️", null, AchievementCategory.MEDITATION_COUNT, 100),
            Achievement("enlightened_soul", "Enlightened Soul", "Complete 250 meditation sessions", "✨", null, AchievementCategory.MEDITATION_COUNT, 250),
            Achievement("meditation_legend", "Legend", "Complete 500 meditation sessions", "👑", null, AchievementCategory.MEDITATION_COUNT, 500),

            // 🔥 MEDITATION STREAK ACHIEVEMENTS
            Achievement("streak_starter", "Streak Starter", "Meditate for 3 consecutive days", "🔥", null, AchievementCategory.MEDITATION_STREAK, 3),
            Achievement("week_warrior", "Week Warrior", "Meditate for 7 consecutive days", "⚔️", null, AchievementCategory.MEDITATION_STREAK, 7),
            Achievement("fortnight_fighter", "Fortnight Fighter", "Meditate for 14 consecutive days", "🛡️", null, AchievementCategory.MEDITATION_STREAK, 14),
            Achievement("month_master", "Month Master", "Meditate for 30 consecutive days", "📅", null, AchievementCategory.MEDITATION_STREAK, 30),
            Achievement("season_sage", "Season Sage", "Meditate for 90 consecutive days", "🌸", null, AchievementCategory.MEDITATION_STREAK, 90),
            Achievement("year_yogi", "Year Yogi", "Meditate for 365 consecutive days", "🎊", null, AchievementCategory.MEDITATION_STREAK, 365),

            // ⏰ MEDITATION TIME ACHIEVEMENTS
            Achievement("first_hour", "First Hour", "Meditate for a total of 1 hour", "⏰", null, AchievementCategory.MEDITATION_TIME, 60),
            Achievement("time_traveler", "Time Traveler", "Meditate for a total of 5 hours", "🕐", null, AchievementCategory.MEDITATION_TIME, 300),
            Achievement("mindful_marathon", "Mindful Marathon", "Meditate for a total of 10 hours", "🏃‍♂️", null, AchievementCategory.MEDITATION_TIME, 600),
            Achievement("zen_zone", "Zen Zone", "Meditate for a total of 20 hours", "🌀", null, AchievementCategory.MEDITATION_TIME, 1200),
            Achievement("meditation_mountain", "Meditation Mountain", "Meditate for a total of 50 hours", "⛰️", null, AchievementCategory.MEDITATION_TIME, 3000),
            Achievement("enlightenment_peak", "Enlightenment Peak", "Meditate for a total of 100 hours", "🏔️", null, AchievementCategory.MEDITATION_TIME, 6000),

            // 📝 JOURNAL ACHIEVEMENTS
            Achievement("first_thoughts", "First Thoughts", "Write your first journal entry", "💭", null, AchievementCategory.JOURNAL_ENTRIES, 1),
            Achievement("storyteller", "Storyteller", "Write 10 journal entries", "📖", null, AchievementCategory.JOURNAL_ENTRIES, 10),
            Achievement("memory_keeper", "Memory Keeper", "Write 25 journal entries", "🗂️", null, AchievementCategory.JOURNAL_ENTRIES, 25),
            Achievement("life_chronicler", "Life Chronicler", "Write 50 journal entries", "📚", null, AchievementCategory.JOURNAL_ENTRIES, 50),
            Achievement("wisdom_writer", "Wisdom Writer", "Write 100 journal entries", "✍️", null, AchievementCategory.JOURNAL_ENTRIES, 100),

            // 🙏 PRAYER ACHIEVEMENTS
            Achievement("first_prayer", "First Prayer", "Add your first prayer request", "🙏", null, AchievementCategory.PRAYERS_ANSWERED, 1),
            Achievement("faithful_heart", "Faithful Heart", "Add 10 prayer requests", "💖", null, AchievementCategory.PRAYERS_ANSWERED, 10),
            Achievement("prayer_warrior", "Prayer Warrior", "Add 25 prayer requests", "⚔️", null, AchievementCategory.PRAYERS_ANSWERED, 25),
            Achievement("spiritual_guardian", "Spiritual Guardian", "Add 50 prayer requests", "👼", null, AchievementCategory.PRAYERS_ANSWERED, 50),
            Achievement("divine_messenger", "Divine Messenger", "Have 5 prayers answered", "📬", null, AchievementCategory.PRAYERS_ANSWERED, 5),
            Achievement("miracle_witness", "Miracle Witness", "Have 10 prayers answered", "✨", null, AchievementCategory.PRAYERS_ANSWERED, 10),

            // 🎨 EXPLORATION ACHIEVEMENTS
            Achievement("pattern_explorer", "Pattern Explorer", "Try different breathing patterns", "🌬️", null, AchievementCategory.PATTERN_EXPLORER, 1),
            Achievement("frequency_finder", "Frequency Finder", "Discover binaural beats", "🎵", null, AchievementCategory.FREQUENCY_FINDER, 1),
            Achievement("theme_wanderer", "Theme Wanderer", "Explore different themes", "🎨", null, AchievementCategory.THEME_EXPLORER, 1)
        )

        achievements.forEach { achievement ->
            analyticsDao.insertOrUpdateAchievement(achievement)
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
