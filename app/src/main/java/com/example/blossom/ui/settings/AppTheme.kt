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
    OCEAN_DEPTHS(
        displayName = "Ocean Depths",
        description = "Deep teals to seafoam greens - mysterious and refreshing"
    ),
    GOLDEN_HOUR(
        displayName = "Golden Hour",
        description = "Warm golds to soft peaches - radiant and uplifting"
    ),
    MOONLIT_GARDEN(
        displayName = "Moonlit Garden",
        description = "Silver blues to lavender mists - ethereal and dreamy"
    ),
    FOREST_WHISPER(
        displayName = "Forest Whisper",
        description = "Deep emeralds to soft moss - grounding and natural"
    ),
    TROPICAL_SUNSET(
        displayName = "Tropical Sunset",
        description = "Coral pinks to warm oranges - vibrant and energizing"
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
