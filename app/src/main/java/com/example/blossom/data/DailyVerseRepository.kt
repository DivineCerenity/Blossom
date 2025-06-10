package com.example.blossom.data

import com.example.blossom.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyVerseRepository @Inject constructor(
    private val dailyVerseDao: DailyVerseDao,
    private val apiService: ApiService
) {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    /**
     * Gets the verse for today. If no verse exists for today, fetches a new one from the API.
     * Returns cached verse if it exists for today's date.
     */
    suspend fun getTodaysVerse(): Result<DailyVerse> = withContext(Dispatchers.IO) {
        try {
            val today = dateFormat.format(Date())
            
            // First, try to get cached verse for today
            val cachedVerse = dailyVerseDao.getVerseForDate(today)
            if (cachedVerse != null) {
                return@withContext Result.success(cachedVerse)
            }
            
            // If no cached verse, fetch from API
            val apiResponse = apiService.getRandomVerse()
            val newVerse = DailyVerse(
                date = today,
                verse = apiResponse.verse.details.verseText,
                reference = apiResponse.verse.details.reference
            )
            
            // Save to database
            dailyVerseDao.insertVerse(newVerse)
            
            // Clean up old verses (keep only last 7 days)
            cleanupOldVerses()
            
            Result.success(newVerse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Forces a refresh of today's verse by fetching from API
     */
    suspend fun refreshTodaysVerse(): Result<DailyVerse> = withContext(Dispatchers.IO) {
        try {
            val today = dateFormat.format(Date())
            
            // Fetch fresh verse from API
            val apiResponse = apiService.getRandomVerse()
            val newVerse = DailyVerse(
                date = today,
                verse = apiResponse.verse.details.verseText,
                reference = apiResponse.verse.details.reference
            )
            
            // Save to database (will replace existing verse for today)
            dailyVerseDao.insertVerse(newVerse)
            
            Result.success(newVerse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Cleans up verses older than 7 days to keep database size manageable
     */
    private suspend fun cleanupOldVerses() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val cutoffDate = dateFormat.format(calendar.time)
        dailyVerseDao.deleteOldVerses(cutoffDate)
    }
    
    /**
     * Gets the cached verse for a specific date (useful for testing or viewing past verses)
     */
    suspend fun getVerseForDate(date: String): DailyVerse? = withContext(Dispatchers.IO) {
        dailyVerseDao.getVerseForDate(date)
    }
}
