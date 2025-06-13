package com.example.blossom.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.blossom.ui.theme.*
import com.example.blossom.ui.settings.AppTheme

@Composable
fun GradientText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.displaySmall,
    theme: AppTheme = AppTheme.TWILIGHT_MYSTIQUE
) {
    // 🌈 DYNAMIC THEME-BASED GRADIENT COLORS! 🌈
    val gradientColors = when (theme) {
        AppTheme.COASTAL_SERENITY -> listOf(
            CoastalBlueGray,    // Sophisticated blue-gray
            CoastalGolden,      // Warm golden
            CoastalTerracotta   // Rich terracotta
        )
        AppTheme.AUTUMN_HARVEST -> listOf(
            AutumnTerracotta,   // Rich terracotta
            AutumnDarkGreen,    // Deep forest green for strong contrast
            Color(0xFFF6F3E8)   // Light earthy cream for highlight
        )
        AppTheme.AURORA_DREAMS -> listOf(
            AuroraDeepPurple,   // Deep cosmic purple
            AuroraMidPurple,    // Vibrant purple
            AuroraMint          // Ethereal mint
        )
        AppTheme.SAKURA_WHISPER -> listOf(
            SakuraBlush,        // Gentle blush pink
            SakuraRose,         // Muted dusty rose
            SakuraSage          // Soft sage green
        )
        AppTheme.OCEAN_DEPTHS -> listOf(
            OceanDeepTeal,      // Deep ocean teal
            OceanMidTeal,       // Medium teal
            OceanSeafoam        // Seafoam green
        )
        AppTheme.GOLDEN_HOUR -> listOf(
            GoldenDeep,         // Deep gold
            GoldenWarm,         // Warm gold
            GoldenPeach         // Soft peach
        )
        AppTheme.MOONLIT_GARDEN -> listOf(
            MoonlitSilver,      // Silver blue
            MoonlitBlue,        // Soft blue
            MoonlitLavender     // Gentle lavender
        )
        AppTheme.FOREST_WHISPER -> listOf(
            ForestDeepGreen,    // Deep forest green
            ForestEmerald,      // Emerald green
            ForestSage          // Forest sage
        )
        AppTheme.SERENE_LOTUS -> listOf(
            SereneLavender,     // Soft lavender
            SereneGreen         // Gentle green
        )
        AppTheme.TWILIGHT_MYSTIQUE -> listOf(
            TwilightPurple,     // Deep purple
            TwilightRose,       // Dusty rose
            TwilightBeige       // Warm beige
        )
    }

    Text(
        text = text,
        modifier = modifier,
        style = TextStyle(
            // 🌟 PREMIUM STYLING - PERFECT GRADIENT POSITIONING! 🌟
            // For "Blossom" - start gradient transition on first 'S' (position ~0.3)
            brush = Brush.linearGradient(
                colors = gradientColors,
                start = Offset(0.3f, 0f),  // Start gradient at first 'S'
                end = Offset(1.0f, 0f)     // End at the end of text
            ),
            fontFamily = Marcellus,
            fontSize = 40.sp // Large, but fits well in the app bar
        )
    )
}