package com.jonathon.blossom.data

import android.content.Context
import androidx.work.*
import com.jonathon.blossom.notifications.HabitReminderWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyHabitRepository @Inject constructor(
    private val dailyHabitDao: DailyHabitDao,
    private val context: Context,
    private val analyticsRepository: AnalyticsRepository
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
    }    suspend fun completeHabit(habit: DailyHabit): List<com.jonathon.blossom.data.Achievement> {
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

        // Fetch updated streakCount and completionCount
        val updatedHabit = getHabitById(habit.id) ?: habit
        val newStreak = updatedHabit.streakCount
        val newLongest = maxOf(updatedHabit.longestStreak, newStreak)
        val newCompletionCount = updatedHabit.completionCount + 1

        // Update habit first
        dailyHabitDao.update(habit.copy(
            isCompleted = true,
            lastCompletedDate = currentTime,
            streakCount = newStreak,
            longestStreak = newLongest,
            completionCount = newCompletionCount
        ))

        // Check for habit achievements and return newly unlocked ones
        return checkHabitAchievements(updatedHabit.copy(
            streakCount = newStreak,
            longestStreak = newLongest,
            completionCount = newCompletionCount
        ))
    }    // --- Milestone logic ---
    private suspend fun checkHabitAchievements(habit: DailyHabit): List<com.jonathon.blossom.data.Achievement> {
        val newAchievements = mutableListOf<com.jonathon.blossom.data.Achievement>()
        
        // Check and unlock achievements, collecting newly unlocked ones
        if (habit.completionCount == 1) {
            val achievement = analyticsRepository.unlockAchievementAndReturn("habit_first_completion")
            achievement?.let { newAchievements.add(it) }
        }
        
        if (habit.longestStreak == 7) {
            val achievement = analyticsRepository.unlockAchievementAndReturn("habit_7_day_streak")
            achievement?.let { newAchievements.add(it) }
        }
        
        if (habit.longestStreak == 30) {
            val achievement = analyticsRepository.unlockAchievementAndReturn("habit_30_day_streak")
            achievement?.let { newAchievements.add(it) }
        }
        
        if (habit.completionCount == 100) {
            val achievement = analyticsRepository.unlockAchievementAndReturn("habit_100_completions")
            achievement?.let { newAchievements.add(it) }
        }
        
        // Comeback: streakCount == 1 and longestStreak >= 7 (broke a streak and started again)
        if (habit.streakCount == 1 && habit.longestStreak >= 7) {
            val achievement = analyticsRepository.unlockAchievementAndReturn("habit_comeback")
            achievement?.let { newAchievements.add(it) }
        }
        
        // Multi-habit streak: check if user has 3+ habits with streakCount >= 7
        val habits = dailyHabitDao.getAllHabits().first()
        val habitsWithLongStreak = habits.count { it.streakCount >= 7 }
        if (habitsWithLongStreak >= 3) {
            val achievement = analyticsRepository.unlockAchievementAndReturn("multi_habit_streak")
            achievement?.let { newAchievements.add(it) }
        }
        
        return newAchievements
    }

    suspend fun resetDailyHabits() {
        dailyHabitDao.resetDailyHabits()
    }

    fun scheduleReminder(habit: DailyHabit) {
        if (habit.reminderTime <= 0) return // Don't schedule if no reminder time set
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
            .setInputData(workDataOf("habit_id" to habit.id, "is_test" to true))
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