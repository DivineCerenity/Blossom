package com.jonathon.blossom.ui.settings

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("blossom_settings", Context.MODE_PRIVATE)

    private val _selectedTheme = MutableStateFlow(loadSelectedTheme())
    private val _isDarkMode = MutableStateFlow(loadDarkMode())
    private val _habitResetTime = MutableStateFlow(loadHabitResetTime())

    private fun loadSelectedTheme(): AppTheme {
        val themeName = sharedPreferences.getString("selected_theme", AppTheme.TWILIGHT_MYSTIQUE.name)
        return try {
            AppTheme.valueOf(themeName ?: AppTheme.TWILIGHT_MYSTIQUE.name)
        } catch (e: IllegalArgumentException) {
            AppTheme.TWILIGHT_MYSTIQUE
        }
    }

    private fun loadDarkMode(): Boolean {
        return sharedPreferences.getBoolean("dark_mode", false)
    }

    private fun loadHabitResetTime(): Int {
        return sharedPreferences.getInt("habit_reset_time", 0)
    }

    fun getSelectedTheme(): Flow<AppTheme> {
        return _selectedTheme.asStateFlow()
    }

    fun getDarkMode(): Flow<Boolean> {
        return _isDarkMode.asStateFlow()
    }

    fun getHabitResetTime(): Flow<Int> {
        return _habitResetTime.asStateFlow()
    }

    suspend fun saveSelectedTheme(theme: AppTheme) {
        sharedPreferences.edit()
            .putString("selected_theme", theme.name)
            .apply()
        _selectedTheme.value = theme
    }

    suspend fun saveDarkMode(isDarkMode: Boolean) {
        sharedPreferences.edit()
            .putBoolean("dark_mode", isDarkMode)
            .apply()
        _isDarkMode.value = isDarkMode
    }

    suspend fun saveHabitResetTime(hour: Int) {
        sharedPreferences.edit()
            .putInt("habit_reset_time", hour)
            .apply()
        _habitResetTime.value = hour
    }

    /**
     * Reload all settings from SharedPreferences
     * Call this after restoring settings from cloud backup
     */
    suspend fun reloadFromSharedPreferences() {
        _selectedTheme.value = loadSelectedTheme()
        _isDarkMode.value = loadDarkMode()
        _habitResetTime.value = loadHabitResetTime()
    }
}
