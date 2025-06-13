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
            AppTheme.OCEAN_DEPTHS -> oceanDepthsLightColorScheme()
            AppTheme.GOLDEN_HOUR -> goldenHourLightColorScheme()
            AppTheme.MOONLIT_GARDEN -> moonlitGardenLightColorScheme()
            AppTheme.FOREST_WHISPER -> forestWhisperLightColorScheme()
            AppTheme.TROPICAL_SUNSET -> tropicalSunsetLightColorScheme()
            AppTheme.TWILIGHT_MYSTIQUE -> twilightMystiqueLightColorScheme()
        }
    }

    fun getDarkColorScheme(theme: AppTheme): ColorScheme {
        return when (theme) {
            AppTheme.COASTAL_SERENITY -> coastalSerenityDarkColorScheme()
            AppTheme.AUTUMN_HARVEST -> autumnHarvestDarkColorScheme()
            AppTheme.AURORA_DREAMS -> auroraDreamsDarkColorScheme()
            AppTheme.SAKURA_WHISPER -> sakuraWhisperDarkColorScheme()
            AppTheme.OCEAN_DEPTHS -> oceanDepthsDarkColorScheme()
            AppTheme.GOLDEN_HOUR -> goldenHourDarkColorScheme()
            AppTheme.MOONLIT_GARDEN -> moonlitGardenDarkColorScheme()
            AppTheme.FOREST_WHISPER -> forestWhisperDarkColorScheme()
            AppTheme.TROPICAL_SUNSET -> tropicalSunsetDarkColorScheme()
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

    // 🌊 OCEAN DEPTHS THEMES 🌊
    private fun oceanDepthsLightColorScheme() = lightColorScheme(
        primary = OceanMidTeal,
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = OceanSeafoam,
        onPrimaryContainer = Color(0xFF1A1A1A),
        secondary = OceanDeepTeal,
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = OceanMist,
        onSecondaryContainer = Color(0xFF1A1A1A),
        tertiary = OceanSeafoam,
        onTertiary = Color(0xFF1A1A1A),
        tertiaryContainer = OceanPearl,
        onTertiaryContainer = Color(0xFF1A1A1A),
        background = OceanPearl,  // 🌊 PEARL WHITE BACKGROUND
        onBackground = Color(0xFF1A1A1A),
        surface = OceanPearl,  // 🌊 MATCH BACKGROUND FOR CONSISTENCY
        onSurface = Color(0xFF1A1A1A),
        surfaceVariant = OceanMist,
        onSurfaceVariant = OceanDeepTeal,
        error = OceanMidTeal
    )

    private fun oceanDepthsDarkColorScheme() = darkColorScheme(
        primary = OceanSeafoam,
        onPrimary = Color(0xFF1A1A1A),
        primaryContainer = OceanDeepTeal,
        onPrimaryContainer = Color(0xFFE5E5E5),
        secondary = OceanMist,
        onSecondary = Color(0xFF1A1A1A),
        secondaryContainer = OceanMidTeal,
        onSecondaryContainer = Color(0xFFE5E5E5),
        tertiary = OceanPearl,
        onTertiary = Color(0xFF1A1A1A),
        tertiaryContainer = OceanDeepTeal,
        onTertiaryContainer = Color(0xFFE5E5E5),
        background = Color(0xFF0A1A1A),  // 🌊 DEEP OCEAN BACKGROUND
        onBackground = Color(0xFFE5E5E5),
        surface = Color(0xFF0A1A1A),  // 🌊 MATCH BACKGROUND FOR CONSISTENCY
        onSurface = Color(0xFFE5E5E5),
        surfaceVariant = OceanDeepTeal,
        onSurfaceVariant = OceanSeafoam,
        error = OceanMidTeal
    )

    // ✨ GOLDEN HOUR THEMES ✨
    private fun goldenHourLightColorScheme() = lightColorScheme(
        primary = GoldenWarm,
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = GoldenPeach,
        onPrimaryContainer = Color(0xFF1A1A1A),
        secondary = GoldenDeep,
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = GoldenBlush,
        onSecondaryContainer = Color(0xFF1A1A1A),
        tertiary = GoldenPeach,
        onTertiary = Color(0xFF1A1A1A),
        tertiaryContainer = GoldenCream,
        onTertiaryContainer = Color(0xFF1A1A1A),
        background = GoldenCream,  // ✨ GOLDEN CREAM BACKGROUND
        onBackground = Color(0xFF1A1A1A),
        surface = GoldenCream,  // ✨ MATCH BACKGROUND FOR CONSISTENCY
        onSurface = Color(0xFF1A1A1A),
        surfaceVariant = GoldenBlush,
        onSurfaceVariant = GoldenDeep,
        error = GoldenWarm
    )

    private fun goldenHourDarkColorScheme() = darkColorScheme(
        primary = GoldenPeach,
        onPrimary = Color(0xFF1A1A1A),
        primaryContainer = GoldenDeep,
        onPrimaryContainer = Color(0xFFE5E5E5),
        secondary = GoldenBlush,
        onSecondary = Color(0xFF1A1A1A),
        secondaryContainer = GoldenWarm,
        onSecondaryContainer = Color(0xFFE5E5E5),
        tertiary = GoldenCream,
        onTertiary = Color(0xFF1A1A1A),
        tertiaryContainer = GoldenDeep,
        onTertiaryContainer = Color(0xFFE5E5E5),
        background = Color(0xFF2A1F0A),  // ✨ DEEP GOLDEN BACKGROUND
        onBackground = Color(0xFFE5E5E5),
        surface = Color(0xFF2A1F0A),  // ✨ MATCH BACKGROUND FOR CONSISTENCY
        onSurface = Color(0xFFE5E5E5),
        surfaceVariant = GoldenDeep,
        onSurfaceVariant = GoldenPeach,
        error = GoldenWarm
    )

    // 🌙 MOONLIT GARDEN THEMES 🌙
    private fun moonlitGardenLightColorScheme() = lightColorScheme(
        primary = MoonlitBlue,
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = MoonlitLavender,
        onPrimaryContainer = Color(0xFF1A1A1A),
        secondary = MoonlitSilver,
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = MoonlitMist,
        onSecondaryContainer = Color(0xFF1A1A1A),
        tertiary = MoonlitLavender,
        onTertiary = Color(0xFF1A1A1A),
        tertiaryContainer = MoonlitPearl,
        onTertiaryContainer = Color(0xFF1A1A1A),
        background = MoonlitPearl,  // 🌙 GHOST WHITE BACKGROUND
        onBackground = Color(0xFF1A1A1A),
        surface = MoonlitPearl,  // 🌙 MATCH BACKGROUND FOR CONSISTENCY
        onSurface = Color(0xFF1A1A1A),
        surfaceVariant = MoonlitMist,
        onSurfaceVariant = MoonlitSilver,
        error = MoonlitBlue
    )

    private fun moonlitGardenDarkColorScheme() = darkColorScheme(
        primary = MoonlitLavender,
        onPrimary = Color(0xFF1A1A1A),
        primaryContainer = MoonlitSilver,
        onPrimaryContainer = Color(0xFFE5E5E5),
        secondary = MoonlitMist,
        onSecondary = Color(0xFF1A1A1A),
        secondaryContainer = MoonlitBlue,
        onSecondaryContainer = Color(0xFFE5E5E5),
        tertiary = MoonlitPearl,
        onTertiary = Color(0xFF1A1A1A),
        tertiaryContainer = MoonlitSilver,
        onTertiaryContainer = Color(0xFFE5E5E5),
        background = Color(0xFF1A1A2A),  // 🌙 DEEP MOONLIT BACKGROUND
        onBackground = Color(0xFFE5E5E5),
        surface = Color(0xFF1A1A2A),  // 🌙 MATCH BACKGROUND FOR CONSISTENCY
        onSurface = Color(0xFFE5E5E5),
        surfaceVariant = MoonlitSilver,
        onSurfaceVariant = MoonlitLavender,
        error = MoonlitBlue
    )

    // 🌿 FOREST WHISPER THEMES 🌿
    private fun forestWhisperLightColorScheme() = lightColorScheme(
        primary = ForestEmerald,
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = ForestSage,
        onPrimaryContainer = Color(0xFF1A1A1A),
        secondary = ForestDeepGreen,
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = ForestMoss,
        onSecondaryContainer = Color(0xFF1A1A1A),
        tertiary = ForestSage,
        onTertiary = Color(0xFF1A1A1A),
        tertiaryContainer = ForestMint,
        onTertiaryContainer = Color(0xFF1A1A1A),
        background = ForestMint,  // 🌿 LIGHT MINT BACKGROUND
        onBackground = Color(0xFF1A1A1A),
        surface = ForestMint,  // 🌿 MATCH BACKGROUND FOR CONSISTENCY
        onSurface = Color(0xFF1A1A1A),
        surfaceVariant = ForestMoss,
        onSurfaceVariant = ForestDeepGreen,
        error = ForestEmerald
    )

    private fun forestWhisperDarkColorScheme() = darkColorScheme(
        primary = ForestSage,
        onPrimary = Color(0xFF1A1A1A),
        primaryContainer = ForestDeepGreen,
        onPrimaryContainer = Color(0xFFE5E5E5),
        secondary = ForestMoss,
        onSecondary = Color(0xFF1A1A1A),
        secondaryContainer = ForestEmerald,
        onSecondaryContainer = Color(0xFFE5E5E5),
        tertiary = ForestMint,
        onTertiary = Color(0xFF1A1A1A),
        tertiaryContainer = ForestDeepGreen,
        onTertiaryContainer = Color(0xFFE5E5E5),
        background = Color(0xFF0F1F0F),  // 🌿 DEEP FOREST BACKGROUND
        onBackground = Color(0xFFE5E5E5),
        surface = Color(0xFF0F1F0F),  // 🌿 MATCH BACKGROUND FOR CONSISTENCY
        onSurface = Color(0xFFE5E5E5),
        surfaceVariant = ForestDeepGreen,
        onSurfaceVariant = ForestSage,
        error = ForestEmerald
    )

    // 🌺 TROPICAL SUNSET THEMES 🌺
    private fun tropicalSunsetLightColorScheme() = lightColorScheme(
        primary = TropicalCoral,
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = TropicalPeach,
        onPrimaryContainer = Color(0xFF1A1A1A),
        secondary = TropicalOrange,
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = TropicalBlush,
        onSecondaryContainer = Color(0xFF1A1A1A),
        tertiary = TropicalPeach,
        onTertiary = Color(0xFF1A1A1A),
        tertiaryContainer = TropicalCream,
        onTertiaryContainer = Color(0xFF1A1A1A),
        background = TropicalCream,  // 🌺 SEASHELL CREAM BACKGROUND
        onBackground = Color(0xFF1A1A1A),
        surface = TropicalCream,  // 🌺 MATCH BACKGROUND FOR CONSISTENCY
        onSurface = Color(0xFF1A1A1A),
        surfaceVariant = TropicalBlush,
        onSurfaceVariant = TropicalOrange,
        error = TropicalCoral
    )

    private fun tropicalSunsetDarkColorScheme() = darkColorScheme(
        primary = TropicalPeach,
        onPrimary = Color(0xFF1A1A1A),
        primaryContainer = TropicalCoral,
        onPrimaryContainer = Color(0xFFE5E5E5),
        secondary = TropicalBlush,
        onSecondary = Color(0xFF1A1A1A),
        secondaryContainer = TropicalOrange,
        onSecondaryContainer = Color(0xFFE5E5E5),
        tertiary = TropicalCream,
        onTertiary = Color(0xFF1A1A1A),
        tertiaryContainer = TropicalCoral,
        onTertiaryContainer = Color(0xFFE5E5E5),
        background = Color(0xFF2A1A0F),  // 🌺 DEEP SUNSET BACKGROUND
        onBackground = Color(0xFFE5E5E5),
        surface = Color(0xFF2A1A0F),  // 🌺 MATCH BACKGROUND FOR CONSISTENCY
        onSurface = Color(0xFFE5E5E5),
        surfaceVariant = TropicalCoral,
        onSurfaceVariant = TropicalPeach,
        error = TropicalOrange
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
