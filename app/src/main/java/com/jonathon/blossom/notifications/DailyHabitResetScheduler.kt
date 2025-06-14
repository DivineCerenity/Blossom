package com.jonathon.blossom.notifications

import android.content.Context
import androidx.work.*
import com.jonathon.blossom.data.DailyHabitRepository
import com.jonathon.blossom.ui.settings.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🔄 DAILY HABIT RESET SCHEDULER
 * Schedules daily habit resets at the user-specified time
 */
@Singleton
class DailyHabitResetScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val habitRepository: DailyHabitRepository
) {
    
    companion object {
        private const val RESET_WORK_TAG = "daily_habit_reset"
        private const val RESET_WORK_NAME = "daily_habit_reset_work"
        
        /**
         * Schedule the next habit reset based on current settings
         */
        suspend fun scheduleNextReset(context: Context) {
            val workManager = WorkManager.getInstance(context)
            
            // Cancel any existing reset work
            workManager.cancelUniqueWork(RESET_WORK_NAME)
            
            // Get reset time from settings (this is a static method for the worker)
            val sharedPreferences = context.getSharedPreferences("blossom_settings", Context.MODE_PRIVATE)
            val resetHour = sharedPreferences.getInt("habit_reset_time", 0) // Default to midnight
            
            // Calculate delay until next reset time
            val delay = calculateDelayUntilResetTime(resetHour)
            
            // Schedule the reset work
            val resetWorkRequest = OneTimeWorkRequestBuilder<DailyHabitResetWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(RESET_WORK_TAG)
                .build()
            
            workManager.enqueueUniqueWork(
                RESET_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                resetWorkRequest
            )
        }
        
        /**
         * Calculate milliseconds until the next reset time
         */
        private fun calculateDelayUntilResetTime(resetHour: Int): Long {
            val currentTime = System.currentTimeMillis()
            val calendar = Calendar.getInstance()
            
            // Set to the reset time today
            calendar.set(Calendar.HOUR_OF_DAY, resetHour)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            
            // If reset time has already passed today, schedule for tomorrow
            if (calendar.timeInMillis <= currentTime) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            
            return calendar.timeInMillis - currentTime
        }
    }
    
    /**
     * Schedule the next habit reset using current settings
     */
    suspend fun scheduleReset() {
        val resetHour = settingsRepository.getHabitResetTime().first()
        
        val workManager = WorkManager.getInstance(context)
        
        // Cancel any existing reset work
        workManager.cancelUniqueWork(RESET_WORK_NAME)
        
        // Calculate delay until next reset time
        val delay = calculateDelayUntilResetTime(resetHour)
        
        // Schedule the reset work
        val resetWorkRequest = OneTimeWorkRequestBuilder<DailyHabitResetWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag(RESET_WORK_TAG)
            .build()
        
        workManager.enqueueUniqueWork(
            RESET_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            resetWorkRequest
        )
    }
    
    /**
     * Calculate milliseconds until the next reset time
     */
    private fun calculateDelayUntilResetTime(resetHour: Int): Long {
        val currentTime = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        
        // Set to the reset time today
        calendar.set(Calendar.HOUR_OF_DAY, resetHour)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        // If reset time has already passed today, schedule for tomorrow
        if (calendar.timeInMillis <= currentTime) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        
        return calendar.timeInMillis - currentTime
    }
    
    /**
     * 🧪 TEST: Manually trigger habit reset for testing
     */
    suspend fun testHabitReset() {
        val workManager = WorkManager.getInstance(context)
        
        // Create immediate reset work request
        val testResetWorkRequest = OneTimeWorkRequestBuilder<DailyHabitResetWorker>()
            .setInitialDelay(0, TimeUnit.SECONDS) // Immediate execution
            .addTag("test_habit_reset")
            .build()
        
        // Enqueue the test reset work
        workManager.enqueueUniqueWork(
            "test_habit_reset_work",
            ExistingWorkPolicy.REPLACE,
            testResetWorkRequest
        )
    }
    
    /**
     * 🧪 TEST: Get habit repository for direct testing
     */
    fun getHabitRepository(): DailyHabitRepository {
        return habitRepository
    }
}
