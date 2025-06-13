package com.jonathon.blossom.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

enum class ScreenType {
    Dashboard, Checklist, Meditation, Journal, Prayer
}

@Composable
fun ScreenBackground(
    screenType: ScreenType,
    content: @Composable () -> Unit
) {
    // You can customize background per screenType if desired
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        content()
    }
}
