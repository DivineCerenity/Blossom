package com.jonathon.blossom.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import kotlin.random.Random

/**
 * ✨ VISUAL EFFECTS SYSTEM ✨
 * Beautiful visual enhancements and micro-interactions
 */

@Composable
fun FloatingParticles(
    modifier: Modifier = Modifier,
    particleCount: Int = 20,
    colors: List<Color> = listOf(
        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
    )
) {
    val particles = remember {
        (0 until particleCount).map {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 8f + 2f,
                speed = Random.nextFloat() * 0.5f + 0.1f,
                color = colors.random()
            )
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_animation"
    )
    
    Box(modifier = modifier) {
        particles.forEach { particle ->
            val currentY = (particle.y + animationProgress * particle.speed) % 1f
            val currentX = particle.x + sin(animationProgress * 2 * Math.PI * particle.speed).toFloat() * 0.1f
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.TopStart)
                    .offset(
                        x = (currentX * 300).dp,
                        y = (currentY * 800).dp
                    )
                    .size(particle.size.dp)
                    .clip(CircleShape)
                    .background(particle.color)
                    .alpha(0.6f)
            )
        }
    }
}

data class Particle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val color: Color
)

@Composable
fun PulsingOrb(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    size: androidx.compose.ui.unit.Dp = 100.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsing_orb")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_scale"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_alpha"
    )
    
    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .alpha(alpha)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.8f),
                        color.copy(alpha = 0.2f),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
fun ShimmingEffect(
    modifier: Modifier = Modifier,
    isLoading: Boolean = true
) {
    if (!isLoading) return
    
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    
    val shimmerTranslateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.linearGradient(
                    colors = shimmerColors,
                    start = androidx.compose.ui.geometry.Offset(shimmerTranslateAnim - 200f, 0f),
                    end = androidx.compose.ui.geometry.Offset(shimmerTranslateAnim, 0f)
                )
            )
    )
}

@Composable
fun GlowingBorder(
    modifier: Modifier = Modifier,
    glowColor: Color = MaterialTheme.colorScheme.primary,
    borderWidth: androidx.compose.ui.unit.Dp = 2.dp,
    glowRadius: androidx.compose.ui.unit.Dp = 8.dp,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glowing_border")
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )
    
    Box(
        modifier = modifier
    ) {
        // Glow effect
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(glowRadius)
                .background(
                    glowColor.copy(alpha = glowAlpha),
                    RoundedCornerShape(12.dp)
                )
        )
        
        // Content with border
        Box(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(8.dp)
                )
                .padding(borderWidth)
        ) {
            content()
        }
    }
}

@Composable
fun RippleEffect(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    isActive: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ripple")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = if (isActive) 0f else 1f,
        targetValue = if (isActive) 2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple_scale"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = if (isActive) 0.8f else 0f,
        targetValue = if (isActive) 0f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple_alpha"
    )
    
    if (isActive) {
        Box(
            modifier = modifier
                .scale(scale)
                .alpha(alpha)
                .clip(CircleShape)
                .background(color)
        )
    }
}

@Composable
fun BreathingAnimation(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    isActive: Boolean = true,
    breathingDuration: Int = 4000
) {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = if (isActive) 0.8f else 1f,
        targetValue = if (isActive) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(breathingDuration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_scale"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = if (isActive) 0.4f else 0.8f,
        targetValue = if (isActive) 0.8f else 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(breathingDuration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_alpha"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .alpha(alpha)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.6f),
                        color.copy(alpha = 0.2f),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
fun LoadingDots(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    dotCount: Int = 3,
    animationDuration: Int = 1200
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_dots")
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(dotCount) { index ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = animationDuration,
                        delayMillis = index * 200,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_scale_$index"
            )
            
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
fun SuccessCheckmark(
    modifier: Modifier = Modifier,
    color: Color = Color.Green,
    isVisible: Boolean = false
) {
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "checkmark_scale"
    )
    
    val rotation by animateFloatAsState(
        targetValue = if (isVisible) 0f else -90f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "checkmark_rotation"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .graphicsLayer {
                rotationZ = rotation
            }
            .size(24.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "✓",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
