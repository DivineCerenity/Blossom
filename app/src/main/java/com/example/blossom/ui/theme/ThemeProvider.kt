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
            AppTheme.AURORA_DREAMS -> auroraDreamsLightColorScheme()
            AppTheme.SAKURA_WHISPER -> sakuraWhisperLightColorScheme()
            AppTheme.TWILIGHT_MYSTIQUE -> twilightMystiqueLightColorScheme()
        }
    }

    fun getDarkColorScheme(theme: AppTheme): ColorScheme {
        return when (theme) {
            AppTheme.COASTAL_SERENITY -> coastalSerenityDarkColorScheme()
            AppTheme.AUTUMN_HARVEST -> autumnHarvestDarkColorScheme()
            AppTheme.AURORA_DREAMS -> auroraDreamsDarkColorScheme()
            AppTheme.SAKURA_WHISPER -> sakuraWhisperDarkColorScheme()
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

    // 🌌 AURORA DREAMS THEMES 🌌
    private fun auroraDreamsLightColorScheme() = lightColorScheme(
        primary = AuroraMidPurple,
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = AuroraLavender,
        onPrimaryContainer = Color(0xFF1A1A1A),
        secondary = AuroraTeal,
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = AuroraMint,
        onSecondaryContainer = Color(0xFF1A1A1A),
        tertiary = AuroraDeepPurple,
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = AuroraLavender,
        onTertiaryContainer = Color(0xFF1A1A1A),
        background = Color(0xFFF0F8FF),  // 🌌 SOLID ETHEREAL BACKGROUND
        onBackground = Color(0xFF1A1A1A),
        surface = Color(0xFFF0F8FF),  // 🌌 MATCH BACKGROUND FOR CONSISTENCY
        onSurface = Color(0xFF1A1A1A),
        surfaceVariant = AuroraLavender,
        onSurfaceVariant = AuroraDeepPurple,
        error = AuroraMidPurple
    )

    private fun auroraDreamsDarkColorScheme() = darkColorScheme(
        primary = AuroraLavender,
        onPrimary = Color(0xFF1A1A1A),
        primaryContainer = AuroraDeepPurple,
        onPrimaryContainer = Color(0xFFE5E5E5),
        secondary = AuroraMint,
        onSecondary = Color(0xFF1A1A1A),
        secondaryContainer = AuroraTeal,
        onSecondaryContainer = Color(0xFFE5E5E5),
        tertiary = AuroraMidPurple,
        onTertiary = Color(0xFF1A1A1A),
        tertiaryContainer = AuroraDeepPurple,
        onTertiaryContainer = Color(0xFFE5E5E5),
        background = Color(0xFF0F0A1A),  // Deep cosmic background
        onBackground = Color(0xFFE5E5E5),
        surface = Color(0xFF0F0A1A),  // 🌙 MATCH BACKGROUND FOR CONSISTENCY
        onSurface = Color(0xFFE5E5E5),
        surfaceVariant = AuroraDeepPurple,
        onSurfaceVariant = AuroraLavender,
        error = AuroraMidPurple
    )

    // 🌸 SAKURA WHISPER THEMES 🌸
    private fun sakuraWhisperLightColorScheme() = lightColorScheme(
        primary = SakuraBlush,
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = SakuraSoftPink,
        onPrimaryContainer = Color(0xFF1A1A1A),
        secondary = SakuraSage,
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = SakuraCream,
        onSecondaryContainer = Color(0xFF1A1A1A),
        tertiary = SakuraRose,
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = SakuraSoftPink,
        onTertiaryContainer = Color(0xFF1A1A1A),
        background = SakuraCream,  // 🌸 WARM CREAM BACKGROUND
        onBackground = Color(0xFF1A1A1A),
        surface = SakuraCream,  // 🌸 MATCH BACKGROUND FOR CONSISTENCY
        onSurface = Color(0xFF1A1A1A),
        surfaceVariant = SakuraSoftPink,
        onSurfaceVariant = SakuraRose,
        error = SakuraRose
    )

    private fun sakuraWhisperDarkColorScheme() = darkColorScheme(
        primary = SakuraSoftPink,
        onPrimary = Color(0xFF1A1A1A),
        primaryContainer = SakuraRose,
        onPrimaryContainer = Color(0xFFE5E5E5),
        secondary = SakuraSage,
        onSecondary = Color(0xFF1A1A1A),
        secondaryContainer = SakuraBlush,
        onSecondaryContainer = Color(0xFFE5E5E5),
        tertiary = SakuraCream,
        onTertiary = Color(0xFF1A1A1A),
        tertiaryContainer = SakuraRose,
        onTertiaryContainer = Color(0xFFE5E5E5),
        background = Color(0xFF2A1F2A),  // 🌸 DEEP WARM BACKGROUND
        onBackground = Color(0xFFE5E5E5),
        surface = Color(0xFF2A1F2A),  // 🌸 MATCH BACKGROUND FOR CONSISTENCY
        onSurface = Color(0xFFE5E5E5),
        surfaceVariant = SakuraRose,
        onSurfaceVariant = SakuraSoftPink,
        error = SakuraBlush
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
