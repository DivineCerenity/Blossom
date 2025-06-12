package com.example.blossom.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.example.blossom.ui.settings.AppTheme

object ThemeProvider {
    
    fun getLightColorScheme(theme: AppTheme): ColorScheme {
        return when (theme) {
            AppTheme.COASTAL_SERENITY -> coastalSerenityLightColorScheme()
            AppTheme.AUTUMN_HARVEST -> autumnHarvestLightColorScheme()
            AppTheme.MEADOW_DREAMS -> meadowDreamsLightColorScheme()
            AppTheme.DESERT_BLOOM -> desertBloomLightColorScheme()
            AppTheme.TWILIGHT_MYSTIQUE -> twilightMystiqueLightColorScheme()
        }
    }

    fun getDarkColorScheme(theme: AppTheme): ColorScheme {
        return when (theme) {
            AppTheme.COASTAL_SERENITY -> coastalSerenityDarkColorScheme()
            AppTheme.AUTUMN_HARVEST -> autumnHarvestDarkColorScheme()
            AppTheme.MEADOW_DREAMS -> meadowDreamsDarkColorScheme()
            AppTheme.DESERT_BLOOM -> desertBloomDarkColorScheme()
            AppTheme.TWILIGHT_MYSTIQUE -> twilightMystiqueDarkColorScheme()
        }
    }
    
    // 🌊 COASTAL SERENITY THEMES 🌊
    private fun coastalSerenityLightColorScheme() = lightColorScheme(
        primary = CoastalBlueGray,
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = CoastalSageGray,
        onPrimaryContainer = Color(0xFF1A1A1A),
        secondary = CoastalGolden,
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = CoastalCream,
        onSecondaryContainer = Color(0xFF1A1A1A),
        tertiary = CoastalTerracotta,
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = CoastalSageGray,
        onTertiaryContainer = Color(0xFF1A1A1A),
        background = CoastalCream,
        onBackground = Color(0xFF1A1A1A),
        surface = CoastalCream,
        onSurface = Color(0xFF1A1A1A),
        surfaceVariant = CoastalSageGray,
        onSurfaceVariant = CoastalBlueGray,
        error = CoastalTerracotta
    )

    private fun coastalSerenityDarkColorScheme() = darkColorScheme(
        primary = CoastalSageGray,
        onPrimary = Color(0xFF1A1A1A),
        primaryContainer = CoastalBlueGray,
        onPrimaryContainer = Color(0xFFE5E5E5),
        secondary = CoastalGolden,
        onSecondary = Color(0xFF1A1A1A),
        secondaryContainer = CoastalTerracotta,
        onSecondaryContainer = Color(0xFFE5E5E5),
        tertiary = CoastalCream,
        onTertiary = Color(0xFF1A1A1A),
        tertiaryContainer = CoastalBlueGray,
        onTertiaryContainer = Color(0xFFE5E5E5),
        background = Color(0xFF0F1419),
        onBackground = Color(0xFFE5E5E5),
        surface = Color(0xFF0F1419),  // 🌙 MATCH BACKGROUND FOR CONSISTENCY
        onSurface = Color(0xFFE5E5E5),
        surfaceVariant = CoastalBlueGray,
        onSurfaceVariant = CoastalSageGray,
        error = CoastalTerracotta
    )
    
    // 🍂 AUTUMN HARVEST THEMES 🍂
    private fun autumnHarvestLightColorScheme() = lightColorScheme(
        primary = AutumnTerracotta,
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = AutumnSageGreen,
        onPrimaryContainer = Color(0xFF1A1A1A),
        secondary = AutumnTeal,
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = AutumnLimeGreen,
        onSecondaryContainer = Color(0xFF1A1A1A),
        tertiary = AutumnDarkGreen,
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = AutumnSageGreen,
        onTertiaryContainer = Color(0xFF1A1A1A),
        background = AutumnLimeGreen,
        onBackground = Color(0xFF1A1A1A),
        surface = AutumnLimeGreen,
        onSurface = Color(0xFF1A1A1A),
        surfaceVariant = AutumnSageGreen,
        onSurfaceVariant = AutumnDarkGreen,
        error = AutumnTerracotta
    )

    private fun autumnHarvestDarkColorScheme() = darkColorScheme(
        primary = AutumnSageGreen,
        onPrimary = Color(0xFF1A1A1A),
        primaryContainer = AutumnTerracotta,
        onPrimaryContainer = Color(0xFFE5E5E5),
        secondary = AutumnTeal,
        onSecondary = Color(0xFF1A1A1A),
        secondaryContainer = AutumnDarkGreen,
        onSecondaryContainer = Color(0xFFE5E5E5),
        tertiary = AutumnLimeGreen,
        onTertiary = Color(0xFF1A1A1A),
        tertiaryContainer = AutumnTerracotta,
        onTertiaryContainer = Color(0xFFE5E5E5),
        background = AutumnDarkGreen,
        onBackground = Color(0xFFE5E5E5),
        surface = AutumnDarkGreen,  // 🌙 MATCH BACKGROUND FOR CONSISTENCY
        onSurface = Color(0xFFE5E5E5),
        surfaceVariant = AutumnTerracotta,
        onSurfaceVariant = AutumnSageGreen,
        error = AutumnTerracotta
    )
    
    // 🌿 MEADOW DREAMS THEMES 🌿
    private fun meadowDreamsLightColorScheme() = lightColorScheme(
        primary = MeadowForest,
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = MeadowSage,
        onPrimaryContainer = Color(0xFF1A1A1A),
        secondary = MeadowTeal,
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = MeadowLime,
        onSecondaryContainer = Color(0xFF1A1A1A),
        tertiary = MeadowNavy,
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = MeadowSage,
        onTertiaryContainer = Color(0xFF1A1A1A),
        background = MeadowLime,
        onBackground = Color(0xFF1A1A1A),
        surface = MeadowLime,
        onSurface = Color(0xFF1A1A1A),
        surfaceVariant = MeadowSage,
        onSurfaceVariant = MeadowNavy,
        error = MeadowTeal
    )

    private fun meadowDreamsDarkColorScheme() = darkColorScheme(
        primary = MeadowSage,
        onPrimary = Color(0xFF1A1A1A),
        primaryContainer = MeadowForest,
        onPrimaryContainer = Color(0xFFE5E5E5),
        secondary = MeadowLime,
        onSecondary = Color(0xFF1A1A1A),
        secondaryContainer = MeadowNavy,
        onSecondaryContainer = Color(0xFFE5E5E5),
        tertiary = MeadowTeal,
        onTertiary = Color(0xFF1A1A1A),
        tertiaryContainer = MeadowForest,
        onTertiaryContainer = Color(0xFFE5E5E5),
        background = MeadowNavy,
        onBackground = Color(0xFFE5E5E5),
        surface = MeadowNavy,  // 🌙 MATCH BACKGROUND FOR CONSISTENCY
        onSurface = Color(0xFFE5E5E5),
        surfaceVariant = MeadowForest,
        onSurfaceVariant = MeadowSage,
        error = MeadowTeal
    )
    
    // 🌵 DESERT BLOOM THEMES 🌵
    private fun desertBloomLightColorScheme() = lightColorScheme(
        primary = DesertSage,
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = DesertMint,
        onPrimaryContainer = Color(0xFF1A1A1A),
        secondary = DesertGold,
        onSecondary = Color(0xFF1A1A1A),
        secondaryContainer = DesertCream,
        onSecondaryContainer = Color(0xFF1A1A1A),
        tertiary = DesertCoral,
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = DesertMint,
        onTertiaryContainer = Color(0xFF1A1A1A),
        background = DesertCream,
        onBackground = Color(0xFF1A1A1A),
        surface = DesertCream,
        onSurface = Color(0xFF1A1A1A),
        surfaceVariant = DesertMint,
        onSurfaceVariant = DesertSage,
        error = DesertCoral
    )

    private fun desertBloomDarkColorScheme() = darkColorScheme(
        primary = DesertMint,
        onPrimary = Color(0xFF1A1A1A),
        primaryContainer = DesertSage,
        onPrimaryContainer = Color(0xFFE5E5E5),
        secondary = DesertGold,
        onSecondary = Color(0xFF1A1A1A),
        secondaryContainer = DesertCoral,
        onSecondaryContainer = Color(0xFFE5E5E5),
        tertiary = DesertCream,
        onTertiary = Color(0xFF1A1A1A),
        tertiaryContainer = DesertSage,
        onTertiaryContainer = Color(0xFFE5E5E5),
        background = Color(0xFF1A1F1A),
        onBackground = Color(0xFFE5E5E5),
        surface = Color(0xFF1A1F1A),  // 🌙 MATCH BACKGROUND FOR CONSISTENCY
        onSurface = Color(0xFFE5E5E5),
        surfaceVariant = DesertSage,
        onSurfaceVariant = DesertMint,
        error = DesertCoral
    )
    
    // 🌙 TWILIGHT MYSTIQUE THEMES 🌙
    private fun twilightMystiqueLightColorScheme() = lightColorScheme(
        primary = TwilightPurple,
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = TwilightMauve,
        onPrimaryContainer = Color(0xFFFFFFFF),
        secondary = TwilightRose,
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = TwilightBeige,
        onSecondaryContainer = Color(0xFF1A1A1A),
        tertiary = TwilightBeige,
        onTertiary = Color(0xFF1A1A1A),
        tertiaryContainer = TwilightCream,
        onTertiaryContainer = Color(0xFF1A1A1A),
        background = TwilightCream,
        onBackground = Color(0xFF1A1A1A),
        surface = TwilightCream,
        onSurface = Color(0xFF1A1A1A),
        surfaceVariant = TwilightBeige,
        onSurfaceVariant = TwilightPurple,
        error = TwilightRose
    )

    private fun twilightMystiqueDarkColorScheme() = darkColorScheme(
        primary = TwilightMauve,
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF5A4A63), // 🌙 SLIGHTLY LIGHTER THAN BACKGROUND FOR VISIBILITY
        onPrimaryContainer = Color(0xFFE5E5E5),
        secondary = TwilightRose,
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFF5A4A63), // 🌙 CONSISTENT WITH PRIMARY CONTAINER
        onSecondaryContainer = Color(0xFFE5E5E5),
        tertiary = TwilightBeige,
        onTertiary = Color(0xFF1A1A1A),
        tertiaryContainer = TwilightMauve,
        onTertiaryContainer = Color(0xFFE5E5E5),
        background = TwilightPurple, // 🌙 ORIGINAL BACKGROUND
        onBackground = Color(0xFFE5E5E5),
        surface = TwilightPurple,  // 🌙 MATCH BACKGROUND FOR CONSISTENCY
        onSurface = Color(0xFFE5E5E5),
        surfaceVariant = TwilightMauve,
        onSurfaceVariant = TwilightBeige,
        error = TwilightRose
    )
}
