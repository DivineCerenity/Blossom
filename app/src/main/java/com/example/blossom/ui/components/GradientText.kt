package com.example.blossom.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.blossom.ui.theme.DancingScript
import com.example.blossom.ui.theme.AccentBlueGray
import com.example.blossom.ui.theme.AccentGolden
import com.example.blossom.ui.theme.AccentTerracotta

@Composable
fun GradientText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.displaySmall
) {
    // 🎨 BEAUTIFUL EARTH-TONE GRADIENT 🎨
    // Using your gorgeous palette for perfect harmony!
    val gradientColors = listOf(
        AccentBlueGray,    // Sophisticated blue-gray start
        AccentGolden,      // Warm golden middle
        AccentTerracotta   // Rich terracotta end
    )

    Text(
        text = text,
        modifier = modifier,
        style = style.copy(
            // 🌟 PREMIUM STYLING 🌟
            brush = Brush.linearGradient(colors = gradientColors),
            fontFamily = DancingScript,
            fontWeight = FontWeight.Bold,  // Make it bold for impact!
            fontSize = 32.sp,              // Bigger size to make it pop!
            letterSpacing = 1.2.sp         // Elegant letter spacing
        )
    )
}