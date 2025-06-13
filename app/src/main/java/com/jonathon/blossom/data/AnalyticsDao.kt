package com.jonathon.blossom.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * 📊 ANALYTICS DAO
 * Database access for meditation sessions and analytics
 */
@Dao
interface AnalyticsDao {
    
    // 🧘‍♂️ MEDITATION SESSION OPERATIONS
    @Insert
    suspend fun insertMeditationSession(session: MeditationSession)
    
    @Query("SELECT * FROM meditation_sessions ORDER BY startTime DESC")
    fun getAllMeditationSessions(): Flow<List<MeditationSession>>
    
    @Query("SELECT * FROM meditation_sessions WHERE completed = 1 ORDER BY startTime DESC")
    fun getCompletedMeditationSessions(): Flow<List<MeditationSession>>
    
    @Query("SELECT * FROM meditation_sessions WHERE startTime >= :startTime AND startTime <= :endTime")
    suspend fun getMeditationSessionsInRange(startTime: Long, endTime: Long): List<MeditationSession>
    
    // 📈 STREAK CALCULATIONS (INCLUDING ALL SESSIONS!)
    @Query("""
        SELECT COUNT(DISTINCT DATE(startTime/1000, 'unixepoch', 'localtime')) as streak
        FROM meditation_sessions
        WHERE DATE(startTime/1000, 'unixepoch', 'localtime') >= (
            SELECT DATE('now', 'localtime', '-' ||
                (SELECT COUNT(DISTINCT DATE(startTime/1000, 'unixepoch', 'localtime'))
                 FROM meditation_sessions) || ' days')
        )
    """)
    suspend fun getCurrentStreak(): Int
    
    // 📊 TOTAL SESSION COUNTS (INCLUDING STOPPED SESSIONS)
    @Query("SELECT COUNT(*) FROM meditation_sessions")
    suspend fun getTotalSessions(): Int

    @Query("SELECT COUNT(*) FROM meditation_sessions WHERE completed = 1")
    suspend fun getTotalCompletedSessions(): Int

    @Query("SELECT SUM(duration) FROM meditation_sessions")
    suspend fun getTotalMeditationTime(): Int?

    @Query("SELECT AVG(duration) FROM meditation_sessions")
    suspend fun getAverageMeditationTime(): Int?
    
    // 🌬️ PATTERN INSIGHTS
    @Query("""
        SELECT breathingPattern
        FROM meditation_sessions
        WHERE completed = 1
        GROUP BY breathingPattern
        ORDER BY COUNT(*) DESC
        LIMIT 1
    """)
    suspend fun getFavoriteBreathingPattern(): String?

    @Query("""
        SELECT binauralBeat
        FROM meditation_sessions
        WHERE completed = 1 AND binauralBeat IS NOT NULL
        GROUP BY binauralBeat
        ORDER BY COUNT(*) DESC
        LIMIT 1
    """)
    suspend fun getFavoriteBinauralBeat(): String?

    @Query("""
        SELECT theme
        FROM meditation_sessions
        WHERE completed = 1
        GROUP BY theme
        ORDER BY COUNT(*) DESC
        LIMIT 1
    """)
    suspend fun getFavoriteTheme(): String?

    // 🎨 EXPLORATION TRACKING - COUNT UNIQUE OPTIONS USED
    @Query("""
        SELECT COUNT(DISTINCT breathingPattern)
        FROM meditation_sessions
        WHERE completed = 1 AND breathingPattern != 'None'
    """)
    suspend fun getUniqueBreathingPatterns(): Int

    @Query("""
        SELECT COUNT(DISTINCT binauralBeat)
        FROM meditation_sessions
        WHERE completed = 1 AND binauralBeat IS NOT NULL
    """)
    suspend fun getUniqueBinauralBeats(): Int

    @Query("""
        SELECT COUNT(DISTINCT theme)
        FROM meditation_sessions
        WHERE completed = 1
    """)
    suspend fun getUniqueThemes(): Int
    
    // 📅 WEEKLY DATA (INCLUDING ALL SESSIONS)
    @Query("""
        SELECT
            DATE(startTime/1000, 'unixepoch', 'localtime') as date,
            SUM(duration) as totalTime,
            COUNT(*) as sessionCount
        FROM meditation_sessions
        WHERE startTime >= :weekStartTime
        AND startTime <= :weekEndTime
        GROUP BY DATE(startTime/1000, 'unixepoch', 'localtime')
        ORDER BY date
    """)
    suspend fun getWeeklyMeditationData(weekStartTime: Long, weekEndTime: Long): List<DailyMeditationData>
    
    // 📊 DAILY ANALYTICS OPERATIONS
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDailyAnalytics(analytics: DailyAnalytics)
    
    @Query("SELECT * FROM daily_analytics WHERE date = :date")
    suspend fun getDailyAnalytics(date: String): DailyAnalytics?
    
    @Query("SELECT * FROM daily_analytics ORDER BY date DESC LIMIT 30")
    suspend fun getRecentDailyAnalytics(): List<DailyAnalytics>
    
    // 🏆 ACHIEVEMENT OPERATIONS
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAchievement(achievement: Achievement)
    
    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): Flow<List<Achievement>>
    
    @Query("SELECT * FROM achievements WHERE unlockedAt IS NOT NULL ORDER BY unlockedAt DESC")
    fun getUnlockedAchievements(): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE id = :achievementId")
    suspend fun getAchievementById(achievementId: String): Achievement?
    
    @Query("SELECT * FROM achievements WHERE unlockedAt IS NULL")
    fun getLockedAchievements(): Flow<List<Achievement>>
    
    @Query("UPDATE achievements SET unlockedAt = :unlockedAt WHERE id = :achievementId")
    suspend fun unlockAchievement(achievementId: String, unlockedAt: Long)
    
    @Query("UPDATE achievements SET currentProgress = :progress WHERE id = :achievementId")
    suspend fun updateAchievementProgress(achievementId: String, progress: Int)

    // 🔄 RESET/DELETE OPERATIONS
    @Query("DELETE FROM meditation_sessions")
    suspend fun deleteAllMeditationSessions()

    @Query("DELETE FROM daily_analytics")
    suspend fun deleteAllDailyAnalytics()

    @Query("DELETE FROM achievements")
    suspend fun deleteAllAchievements()
}

/**
 * 📅 DAILY MEDITATION DATA
 * Helper data class for weekly analytics
 */
data class DailyMeditationData(
    val date: String,
    val totalTime: Int,
    val sessionCount: Int
)
