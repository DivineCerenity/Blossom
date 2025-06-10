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
    MEADOW_DREAMS(
        displayName = "Meadow Dreams",
        description = "Vibrant lime to deep forest - energizing yet calming"
    ),
    DESERT_BLOOM(
        displayName = "Desert Bloom",
        description = "Sage to warm coral - sophisticated and warm"
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
