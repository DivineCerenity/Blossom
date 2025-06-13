package com.jonathon.blossom.ui.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jonathon.blossom.data.DailyHabit
import com.jonathon.blossom.data.DailyHabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyHabitsViewModel @Inject constructor(
    private val repository: DailyHabitRepository
) : ViewModel() {

    private val _habits = MutableStateFlow<List<DailyHabit>>(emptyList())
    val habits: StateFlow<List<DailyHabit>> = _habits.asStateFlow()

    fun loadHabits() {
        viewModelScope.launch {
            repository.getAllHabits().collect { habitList ->
                _habits.value = habitList
            }
        }
    }

    fun addHabit(habit: DailyHabit) {
        viewModelScope.launch {
            repository.insertHabit(habit)
        }
    }

    fun updateHabit(habit: DailyHabit) {
        viewModelScope.launch {
            repository.updateHabit(habit)
        }
    }

    fun deleteHabit(habitId: Int) {
        viewModelScope.launch {
            repository.getHabitById(habitId)?.let { habit ->
                repository.deleteHabit(habit)
            }
        }
    }

    fun completeHabit(habit: DailyHabit) {
        viewModelScope.launch {
            repository.completeHabit(habit)
        }
    }

    fun testNotification(habit: DailyHabit) {
        viewModelScope.launch {
            repository.scheduleTestNotification(habit)
        }
    }

    fun updateHabitReminder(habitId: Int, reminderTime: Long) {
        viewModelScope.launch {
            repository.getHabitById(habitId)?.let { habit ->
                repository.updateHabit(habit.copy(reminderTime = reminderTime))
            }
        }
    }

    fun resetHabitCompletion(habit: DailyHabit) {
        viewModelScope.launch {
            repository.updateHabit(habit.copy(isCompleted = false))
        }
    }

    suspend fun getHabitById(habitId: Int): DailyHabit? {
        return repository.getHabitById(habitId)
    }

    fun updateHabitById(habitId: Int, newTitle: String, newDescription: String) {
        viewModelScope.launch {
            repository.getHabitById(habitId)?.let { existingHabit ->
                val updatedHabit = existingHabit.copy(
                    title = newTitle,
                    description = newDescription
                )
                repository.updateHabit(updatedHabit)
            }
        }
    }
} 