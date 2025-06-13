package com.jonathon.blossom.ui.settings

data class SettingsUiState(
    val selectedTheme: AppTheme = AppTheme.TWILIGHT_MYSTIQUE,
    val isDarkMode: Boolean = false,
    val isGoogleSignedIn: Boolean = false,
    val googleUserEmail: String? = null,
    val backupStatus: String = "",
    val restoreStatus: String = ""
)
