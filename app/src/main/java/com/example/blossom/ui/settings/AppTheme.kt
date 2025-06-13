package com.example.blossom.ui.settings

enum class AppTheme(
    val displayName: String,
    val description: String
) {
    COASTAL_SERENITY(
        displayName = "Coastal Serenity",
        description = "Sophisticated blues flowing to warm earth tones - perfect for peaceful reflection"
    ),
    AUTUMN_HARVEST(
        displayName = "Autumn Harvest",
        description = "Rich terracotta to sage greens - grounding and natural"
    ),
    AURORA_DREAMS(
        displayName = "Aurora Dreams",
        description = "Cosmic purples to ethereal mint - magical and transcendent"
    ),
    SAKURA_WHISPER(
        displayName = "Sakura Whisper",
        description = "Gentle cherry blossom pinks to warm creams - serene and nurturing"
    ),
    TWILIGHT_MYSTIQUE(
        displayName = "Twilight Mystique",
        description = "Deep purples to soft creams - mysterious and elegant"
    )
}

data class SettingsUiState(
    val selectedTheme: AppTheme = AppTheme.TWILIGHT_MYSTIQUE,
    val isDarkMode: Boolean = false
)
