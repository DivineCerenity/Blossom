package com.example.blossom.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import com.example.blossom.ui.theme.DancingScript // <-- IMPORTANT: Import your existing font family

@Composable
fun GradientText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.displaySmall
) {
    // Define the gradient colors. You can customize these!
    // These will come from your ui/theme/Color.kt file
    val gradientColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary
    )

    Text(
        text = text,
        modifier = modifier,
        style = style.copy(
            // Apply the gradient brush to the text color
            brush = Brush.linearGradient(colors = gradientColors),
            // Apply your existing font family
            fontFamily = DancingScript
        )
    )
}