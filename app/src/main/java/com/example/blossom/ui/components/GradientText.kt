package com.example.blossom.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
            AutumnTeal,         // Beautiful teal
            AutumnDarkGreen     // Deep forest green
        )
        AppTheme.MEADOW_DREAMS -> listOf(
            MeadowLime,         // Vibrant lime
            MeadowForest,       // Forest green
            MeadowNavy          // Deep navy
        )
        AppTheme.DESERT_BLOOM -> listOf(
            DesertGold,         // Rich gold
            DesertCoral,        // Warm coral
            DesertSage          // Sage gray-green
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
            // 🌟 PREMIUM STYLING - THEME-AWARE GRADIENT! 🌟
            brush = Brush.linearGradient(colors = gradientColors),
            fontFamily = DancingScript,
            fontWeight = FontWeight.Bold,  // Make it bold for impact!
            fontSize = 64.sp,              // DOUBLED! Much bigger size to really pop!
            letterSpacing = 1.5.sp         // Elegant letter spacing
        )
    )
}