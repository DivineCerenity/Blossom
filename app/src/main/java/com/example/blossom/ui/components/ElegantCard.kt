package com.example.blossom.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object ElegantElevation {
    val Low: Dp = 2.dp
    val Medium: Dp = 8.dp
    val High: Dp = 16.dp
}

@Composable
fun ElegantCard(
    modifier: Modifier = Modifier,
    elevation: Dp = ElegantElevation.Medium,
    cornerRadius: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Box(modifier = Modifier) {
            content()
        }
    }
}
