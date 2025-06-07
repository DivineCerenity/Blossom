package com.example.blossom.data

import android.content.Context
import androidx.work.*
import com.example.blossom.notifications.HabitReminderWorker
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyHabitRepository @Inject constructor(
    private val dailyHabitDao: DailyHabitDao,
    private val context: Context
) {
    fun getAllHabits(): Flow<List<DailyHabit>> = dailyHabitDao.getAllHabits()
    
    fun getEnabledHabits(): Flow<List<DailyHabit>> = dailyHabitDao.getEnabledHabits()

    suspend fun getHabitById(id: Int): DailyHabit? = dailyHabitDao.getHabitById(id)

    suspend fun insertHabit(habit: DailyHabit) {
        dailyHabitDao.insert(habit)
        scheduleReminder(habit)
    }

    suspend fun updateHabit(habit: DailyHabit) {
        dailyHabitDao.update(habit)
        if (habit.isEnabled) {
            scheduleReminder(habit)
        } else {
            cancelReminder(habit.id)
        }
    }

    suspend fun deleteHabit(habit: DailyHabit) {
        dailyHabitDao.delete(habit)
        cancelReminder(habit.id)
    }

    suspend fun completeHabit(habit: DailyHabit) {
        val currentTime = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTime
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        // Check if this is the first completion of the day
        if (habit.lastCompletedDate < startOfDay) {
            // If the last completion was yesterday, increment streak
            if (habit.lastCompletedDate > 0) {
                val lastCompletionCalendar = Calendar.getInstance()
                lastCompletionCalendar.timeInMillis = habit.lastCompletedDate
                lastCompletionCalendar.add(Calendar.DAY_OF_YEAR, 1)
                lastCompletionCalendar.set(Calendar.HOUR_OF_DAY, 0)
                lastCompletionCalendar.set(Calendar.MINUTE, 0)
                lastCompletionCalendar.set(Calendar.SECOND, 0)
                lastCompletionCalendar.set(Calendar.MILLISECOND, 0)

                if (lastCompletionCalendar.timeInMillis == startOfDay) {
                    dailyHabitDao.incrementStreak(habit.id)
                } else {
                    dailyHabitDao.resetStreak(habit.id)
                }
            } else {
                dailyHabitDao.incrementStreak(habit.id)
            }
        }

        dailyHabitDao.update(habit.copy(
            isCompleted = true,
            lastCompletedDate = currentTime
        ))
    }

    suspend fun resetDailyHabits() {
        dailyHabitDao.resetDailyHabits()
    }

    fun scheduleReminder(habit: DailyHabit) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = habit.reminderTime

        // If the time has already passed today, schedule for tomorrow
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val delay = calendar.timeInMillis - System.currentTimeMillis()

        val workRequest = OneTimeWorkRequestBuilder<HabitReminderWorker>()
            .setInputData(workDataOf("habit_id" to habit.id))
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag("habit_${habit.id}")
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "habit_${habit.id}",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
    }

    fun scheduleTestNotification(habit: DailyHabit) {
        val workRequest = OneTimeWorkRequestBuilder<HabitReminderWorker>()
            .setInputData(workDataOf("habit_id" to habit.id))
            .setInitialDelay(1, TimeUnit.MINUTES)
            .addTag("habit_${habit.id}")
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "habit_${habit.id}",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
    }

    private fun cancelReminder(habitId: Int) {
        WorkManager.getInstance(context)
            .cancelUniqueWork("habit_$habitId")
    }
} 