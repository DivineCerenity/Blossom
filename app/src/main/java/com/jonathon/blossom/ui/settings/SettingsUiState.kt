package com.jonathon.blossom.ui.settings

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val selectedTheme: AppTheme = AppTheme.TWILIGHT_MYSTIQUE,
    val isGoogleSignedIn: Boolean = false,
    val googleUserEmail: String? = null,
    val backupStatus: String = "",
    val restoreStatus: String = "",
    val habitResetTime: Int = 0 // Default to midnight (0:00)
)
