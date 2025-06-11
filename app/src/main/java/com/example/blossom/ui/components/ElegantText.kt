package com.example.blossom.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ✨ ELEGANT TYPOGRAPHY SYSTEM ✨
 * Beautiful, animated text components with sophisticated styling
 */

@Composable
fun ElegantTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    animationDelay: Int = 0,
    textAlign: TextAlign = TextAlign.Start
) {
    var isVisible by remember { mutableStateOf(false) }
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            delayMillis = animationDelay,
            easing = FastOutSlowInEasing
        ),
        label = "title_alpha"
    )
    
    val slideOffset by animateIntAsState(
        targetValue = if (isVisible) 0 else 50,
        animationSpec = tween(
            durationMillis = 800,
            delayMillis = animationDelay,
            easing = FastOutSlowInEasing
        ),
        label = "title_slide"
    )
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Text(
        text = text,
        modifier = modifier
            .alpha(alpha)
            .offset(y = slideOffset.dp),
        style = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp
        ),
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun ElegantSubtitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    animationDelay: Int = 100,
    textAlign: TextAlign = TextAlign.Start
) {
    var isVisible by remember { mutableStateOf(false) }
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            delayMillis = animationDelay,
            easing = FastOutSlowInEasing
        ),
        label = "subtitle_alpha"
    )
    
    val slideOffset by animateIntAsState(
        targetValue = if (isVisible) 0 else 30,
        animationSpec = tween(
            durationMillis = 800,
            delayMillis = animationDelay,
            easing = FastOutSlowInEasing
        ),
        label = "subtitle_slide"
    )
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Text(
        text = text,
        modifier = modifier
            .alpha(alpha)
            .offset(y = slideOffset.dp),
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.25.sp
        ),
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun ElegantBody(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    animationDelay: Int = 200,
    textAlign: TextAlign = TextAlign.Start,
    maxLines: Int = Int.MAX_VALUE
) {
    var isVisible by remember { mutableStateOf(false) }
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            delayMillis = animationDelay,
            easing = FastOutSlowInEasing
        ),
        label = "body_alpha"
    )
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Text(
        text = text,
        modifier = modifier.alpha(alpha),
        style = MaterialTheme.typography.bodyLarge.copy(
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        color = color,
        textAlign = textAlign,
        maxLines = maxLines
    )
}

@Composable
fun GradientText(
    text: String,
    gradient: Brush,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineMedium,
    animationDelay: Int = 0
) {
    var isVisible by remember { mutableStateOf(false) }
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = animationDelay,
            easing = FastOutSlowInEasing
        ),
        label = "gradient_text_alpha"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = animationDelay,
            easing = FastOutSlowInEasing
        ),
        label = "gradient_text_scale"
    )
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Text(
        text = text,
        modifier = modifier
            .alpha(alpha)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        style = style.copy(
            brush = gradient,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
fun AnimatedCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineMedium,
    color: Color = MaterialTheme.colorScheme.primary,
    animationDuration: Int = 1000,
    animationDelay: Int = 0
) {
    var currentValue by remember { mutableStateOf(0) }
    
    val animatedValue by animateIntAsState(
        targetValue = currentValue,
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = animationDelay,
            easing = FastOutSlowInEasing
        ),
        label = "counter_animation"
    )
    
    LaunchedEffect(targetValue) {
        currentValue = targetValue
    }
    
    Text(
        text = animatedValue.toString(),
        modifier = modifier,
        style = style.copy(
            fontWeight = FontWeight.Bold
        ),
        color = color
    )
}

@Composable
fun TypewriterText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onSurface,
    typingSpeed: Long = 50L
) {
    var displayedText by remember { mutableStateOf("") }
    
    LaunchedEffect(text) {
        displayedText = ""
        text.forEachIndexed { index, _ ->
            kotlinx.coroutines.delay(typingSpeed)
            displayedText = text.substring(0, index + 1)
        }
    }
    
    Text(
        text = displayedText,
        modifier = modifier,
        style = style,
        color = color
    )
}

@Composable
fun PulsingText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsing_text")
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )
    
    Text(
        text = text,
        modifier = modifier.alpha(alpha),
        style = style,
        color = color
    )
}

/**
 * 🌟 ELEGANT SPACING SYSTEM
 */
object ElegantSpacing {
    val tiny = 4.dp
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val extraLarge = 32.dp
    val huge = 48.dp
    val massive = 64.dp
}

/**
 * 🎨 SOPHISTICATED DIVIDERS
 */
@Composable
fun ElegantDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
    thickness: androidx.compose.ui.unit.Dp = 1.dp,
    animationDelay: Int = 0
) {
    var isVisible by remember { mutableStateOf(false) }
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            delayMillis = animationDelay,
            easing = FastOutSlowInEasing
        ),
        label = "divider_alpha"
    )
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    HorizontalDivider(
        modifier = modifier.alpha(alpha),
        color = color,
        thickness = thickness
    )
}
