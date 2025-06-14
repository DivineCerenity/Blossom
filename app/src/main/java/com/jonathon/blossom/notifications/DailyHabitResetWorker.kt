package com.jonathon.blossom.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jonathon.blossom.data.DailyHabitRepository
import javax.inject.Inject

/**
 * 🔄 DAILY HABIT RESET WORKER
 * Resets all habits' completion status at the specified reset time
 */
class DailyHabitResetWorker(
    private val context: Context,
    params: WorkerParameters,
    private val habitRepository: DailyHabitRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Reset all daily habits (sets isCompleted = false)
            habitRepository.resetDailyHabits()
            
            // Schedule the next reset for tomorrow
            DailyHabitResetScheduler.scheduleNextReset(context)
            
            Result.success()
        } catch (e: Exception) {
            // If reset fails, retry with exponential backoff
            Result.retry()
        }
    }
}
