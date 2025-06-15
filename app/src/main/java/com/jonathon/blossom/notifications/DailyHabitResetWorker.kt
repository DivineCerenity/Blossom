package com.jonathon.blossom.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.android.EntryPointAccessors
import com.jonathon.blossom.di.WorkerAppEntryPoint

/**
 * 🔄 DAILY HABIT RESET WORKER
 * Resets all habits' completion status at the specified reset time
 */
class DailyHabitResetWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            // Access the repository using Hilt's entry point
            val appContext = applicationContext
            val entryPoint = EntryPointAccessors.fromApplication(appContext, WorkerAppEntryPoint::class.java)
            val habitRepository = entryPoint.dailyHabitRepository()
            // Reset all daily habits (sets isCompleted = false)
            habitRepository.resetDailyHabits()
            
            // Schedule the next reset for tomorrow
            DailyHabitResetScheduler.scheduleNextReset(appContext)
            
            Result.success()
        } catch (e: Exception) {
            // If reset fails, retry with exponential backoff
            Result.retry()
        }
    }
}
