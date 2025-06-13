package com.jonathon.blossom.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.jonathon.blossom.ui.settings.AppTheme

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
            AppTheme.SERENE_LOTUS -> sereneLotusLightColorScheme()
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
            AppTheme.SERENE_LOTUS -> sereneLotusDarkColorScheme()
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
        primary = AutumnTerracotta, // Rich terracotta
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = AutumnSageGreen, // Sage green container
        onPrimaryContainer = Color(0xFF1A1A1A),
        secondary = AutumnDarkGreen, // Deep forest green for contrast
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = AutumnLimeGreen, // Soft lime green as highlight
        onSecondaryContainer = Color(0xFF1A1A1A),
        tertiary = AutumnTeal, // Teal as accent
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = AutumnSageGreen, // Sage green for harmony
        onTertiaryContainer = Color(0xFF1A1A1A),
        background = AutumnSageGreen, // Sage green background for grounded feel
        onBackground = Color(0xFF1A1A1A),
        surface = AutumnSageGreen, // Match surface to background for seamless look
        onSurface = Color(0xFF1A1A1A),
        surfaceVariant = AutumnLimeGreen, // Lime green for subtle variant
        onSurfaceVariant = AutumnDarkGreen,
        error = AutumnTerracotta // Terracotta for error
    )

    private fun autumnHarvestDarkColorScheme() = darkColorScheme(
        primary = AutumnTerracotta, // Rich terracotta
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = AutumnDarkGreen, // Deep forest green container
        onPrimaryContainer = Color(0xFFE5E5E5),
        secondary = AutumnSageGreen, // Sage green for contrast
        onSecondary = Color(0xFF1A1A1A),
        secondaryContainer = AutumnTeal, // Teal as highlight
        onSecondaryContainer = Color(0xFFE5E5E5),
        tertiary = AutumnLimeGreen, // Lime green as accent
        onTertiary = Color(0xFF1A1A1A),
        tertiaryContainer = AutumnTerracotta, // Terracotta for harmony
        onTertiaryContainer = Color(0xFFE5E5E5),
        background = AutumnDarkGreen, // Deep green background for grounded feel
        onBackground = Color(0xFFE5E5E5),
        surface = AutumnDarkGreen, // Match surface to background for seamless look
        onSurface = Color(0xFFE5E5E5),
        surfaceVariant = AutumnTerracotta, // Terracotta for subtle variant
        onSurfaceVariant = AutumnSageGreen,
        error = AutumnTerracotta // Terracotta for error
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
        surface = SakuraCream,  // Gentle, neutral base for all surfaces
        onSurface = Color(0xFF1A1A1A),
        surfaceVariant = SakuraSoftPink, // Use pink only for cards/sections
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
        surface = Color(0xFF2A1F2A),  // Match background for all surfaces
        onSurface = Color(0xFFE5E5E5),
        surfaceVariant = SakuraRose, // Use pink only for cards/sections
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
        surfaceVariant = OceanSeafoam, // Use seafoam for cards/sections
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
        surface = Color(0xFF0A1A1A),  // Use deep ocean for all main surfaces
        onSurface = Color(0xFFE5E5E5),
        surfaceVariant = OceanDeepTeal, // Use deep teal for cards/sections
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
        surface = GoldenCream,  // Gentle, warm base for all surfaces
        onSurface = Color(0xFF1A1A1A),
        surfaceVariant = GoldenPeach, // Use peach for cards/sections
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
        surface = Color(0xFF2A1F0A),  // Deep brown for all main surfaces
        onSurface = Color(0xFFE5E5E5),
        surfaceVariant = GoldenPeach, // Use peach for cards/sections
        onSurfaceVariant = GoldenDeep,
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
        surfaceVariant = MoonlitBlue, // Use blue for cards/sections
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
        surfaceVariant = MoonlitLavender, // Use lavender for cards/sections
        onSurfaceVariant = MoonlitSilver,
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
        surfaceVariant = ForestSage, // Use sage for cards/sections
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
        surfaceVariant = ForestSage, // Use sage for cards/sections
        onSurfaceVariant = ForestDeepGreen,
        error = ForestEmerald
    )

    // 🌸 SERENE LOTUS THEME 🌸
    private fun sereneLotusLightColorScheme() = lightColorScheme(
        primary = SereneLavender,
        onPrimary = Color.White,
        primaryContainer = SereneCream,
        onPrimaryContainer = Color(0xFF1A1A1A),
        secondary = SereneGreen,
        onSecondary = Color.White,
        secondaryContainer = SereneCream,
        onSecondaryContainer = Color(0xFF1A1A1A),
        tertiary = SereneGold,
        onTertiary = Color.White,
        tertiaryContainer = SereneCream,
        onTertiaryContainer = Color(0xFF1A1A1A),
        background = SereneCream,
        onBackground = Color(0xFF1A1A1A),
        surface = SereneCream,
        onSurface = Color(0xFF1A1A1A),
        surfaceVariant = SereneGreen,
        onSurfaceVariant = SereneLavender,
        error = SereneGold
    )

    private fun sereneLotusDarkColorScheme() = darkColorScheme(
        primary = SereneGreen,
        onPrimary = Color.Black,
        primaryContainer = SereneMidnight,
        onPrimaryContainer = Color.White,
        secondary = SereneLavender,
        onSecondary = Color.Black,
        secondaryContainer = SereneMidnight,
        onSecondaryContainer = Color.White,
        tertiary = SereneGold,
        onTertiary = Color.Black,
        tertiaryContainer = SereneMidnight,
        onTertiaryContainer = Color.White,
        background = SereneMidnight,
        onBackground = Color.White,
        surface = SereneMidnight,
        onSurface = Color.White,
        surfaceVariant = SereneLavender,
        onSurfaceVariant = SereneGreen,
        error = SereneGold
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
