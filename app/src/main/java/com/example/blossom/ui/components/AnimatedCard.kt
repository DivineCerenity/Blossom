package com.example.blossom.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

/**
 * 🎨 ANIMATED CARD SYSTEM 🎨
 * Beautiful, responsive cards with elegant animations
 */

@Composable
fun AnimatedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    animationDelay: Int = 0,
    elevation: CardElevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    colors: CardColors = CardDefaults.cardColors(),
    content: @Composable ColumnScope.() -> Unit
) {
    // Entry animation state
    var isVisible by remember { mutableStateOf(false) }
    
    // Interaction states
    val interactionSource = remember { MutableInteractionSource() }
    var isPressed by remember { mutableStateOf(false) }
    
    // Animation values
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )
    
    val elevationAnimation by animateFloatAsState(
        targetValue = if (isPressed) 2f else 4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_elevation"
    )
    
    // Entry animation
    val slideOffset by animateIntAsState(
        targetValue = if (isVisible) 0 else 100,
        animationSpec = tween(
            durationMillis = 600,
            delayMillis = animationDelay,
            easing = FastOutSlowInEasing
        ),
        label = "slide_animation"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            delayMillis = animationDelay,
            easing = FastOutSlowInEasing
        ),
        label = "alpha_animation"
    )
    
    // Trigger entry animation
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationY = slideOffset.toFloat()
                this.alpha = alpha
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick?.invoke()
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevationAnimation.dp
        ),
        colors = colors,
        content = content
    )
    
    // Handle press state
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is androidx.compose.foundation.interaction.PressInteraction.Press -> {
                    isPressed = true
                }
                is androidx.compose.foundation.interaction.PressInteraction.Release -> {
                    isPressed = false
                }
                is androidx.compose.foundation.interaction.PressInteraction.Cancel -> {
                    isPressed = false
                }
            }
        }
    }
}

/**
 * 🌟 STAGGERED LIST ANIMATION
 */
@Composable
fun <T> AnimatedLazyColumn(
    items: List<T>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    itemContent: @Composable (item: T, index: Int, animationDelay: Int) -> Unit
) {
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement
    ) {
        itemsIndexed(items) { index, item ->
            val animationDelay = index * 100 // Stagger by 100ms
            itemContent(item, index, animationDelay)
        }
    }
}

/**
 * 🎭 ELEGANT BUTTON ANIMATION
 */
@Composable
fun AnimatedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    content: @Composable RowScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "button_scale"
    )
    
    Button(
        onClick = {
            onClick()
        },
        modifier = modifier
            .scale(scale),
        enabled = enabled,
        colors = colors,
        content = content
    )
}

/**
 * 💫 FLOATING ACTION BUTTON WITH ANIMATION
 */
@Composable
fun AnimatedFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    val scale by animateFloatAsState(
        targetValue = when {
            !isVisible -> 0f
            isPressed -> 0.9f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "fab_scale"
    )
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(300)
        isVisible = true
    }
    
    FloatingActionButton(
        onClick = {
            isPressed = true
            onClick()
            // Reset press state after animation
            scope.launch {
                kotlinx.coroutines.delay(150)
                isPressed = false
            }
        },
        modifier = modifier.scale(scale),
        containerColor = containerColor,
        content = content
    )
}

/**
 * 🌈 GRADIENT BACKGROUND ANIMATION
 */
@Composable
fun AnimatedGradientBackground(
    colors: List<androidx.compose.ui.graphics.Color>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient_animation")
    
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_offset"
    )
    
    Box(
        modifier = modifier
            .graphicsLayer {
                // Subtle gradient animation
                alpha = 0.8f + (animatedOffset * 0.2f)
            }
    ) {
        content()
    }
}
