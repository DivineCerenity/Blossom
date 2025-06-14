package com.jonathon.blossom.ui.settings

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val selectedTheme: AppTheme = AppTheme.TWILIGHT_MYSTIQUE,
    val isGoogleSignedIn: Boolean = false,
    val googleUserEmail: String? = null,
    val backupStatus: String = "",
    val restoreStatus: String = "",
    val habitResetTime: Int = 0, // Default to midnight (0:00)
    val themeRefreshKey: Long = System.currentTimeMillis(), // Used to force full recomposition after restore
    val showBackupConfirmation: Boolean = false, // Show backup confirmation dialog
    val showRestoreConfirmation: Boolean = false // Show restore confirmation dialog
)
