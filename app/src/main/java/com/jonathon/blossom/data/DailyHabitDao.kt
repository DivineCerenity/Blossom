package com.jonathon.blossom.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyHabitDao {
    @Query("SELECT * FROM daily_habits ORDER BY reminderTime ASC")
    fun getAllHabits(): Flow<List<DailyHabit>>

    @Query("SELECT * FROM daily_habits WHERE isEnabled = 1")
    fun getEnabledHabits(): Flow<List<DailyHabit>>

    @Query("SELECT * FROM daily_habits WHERE id = :id")
    suspend fun getHabitById(id: Int): DailyHabit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit: DailyHabit)

    @Update
    suspend fun update(habit: DailyHabit)    @Delete
    suspend fun delete(habit: DailyHabit)

    @Query("UPDATE daily_habits SET isCompleted = 0")
    suspend fun resetDailyHabits()

    @Query("UPDATE daily_habits SET streakCount = streakCount + 1 WHERE id = :habitId")
    suspend fun incrementStreak(habitId: Int)

    @Query("UPDATE daily_habits SET streakCount = 0 WHERE id = :habitId")
    suspend fun resetStreak(habitId: Int)
} 