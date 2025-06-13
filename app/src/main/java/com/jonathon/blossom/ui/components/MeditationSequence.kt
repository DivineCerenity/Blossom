package com.jonathon.blossom.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * 🎭 MEDITATION SEQUENCE SYSTEM 🎭
 * Beautiful preparation countdown and completion celebration
 */

enum class MeditationPhase {
    PREPARATION,
    ACTIVE,
    COMPLETION
}

data class MeditationSequenceState(
    val phase: MeditationPhase = MeditationPhase.PREPARATION,
    val preparationStep: Int = 0, // 0-3 (Get ready, 3, 2, 1)
    val isActive: Boolean = false,
    val completionStep: Int = 0, // 0-2 (Chimes, Celebration, Summary)
    val sessionDuration: Int = 0, // in minutes
    val breathingPattern: String = "",
    val completedSuccessfully: Boolean = false
)

/**
 * 🌟 PREPARATION COUNTDOWN
 */
@Composable
fun PreparationCountdown(
    onCountdownComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val primaryColor = MaterialTheme.colorScheme.primary
    
    // Animation for the countdown circle
    val infiniteTransition = rememberInfiniteTransition(label = "preparation")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )
    
    // Countdown sequence (3...2...1)
    LaunchedEffect(Unit) {
        // Step 0: Get ready (2 seconds)
        currentStep = 0
        delay(2000)

        // Step 1: "3" (1 second)
        currentStep = 1
        delay(1000)

        // Step 2: "2" (1 second)
        currentStep = 2
        delay(1000)

        // Step 3: "1" (1 second)
        currentStep = 3
        delay(1000)

        // Complete countdown
        onCountdownComplete()
    }
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Background glow
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size((200 + index * 40).dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        primaryColor.copy(alpha = glowAlpha * (0.1f - index * 0.02f))
                    )
            )
        }
        
        // Main countdown circle (match timer size: 280.dp)
        Box(
            modifier = Modifier
                .size(280.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.8f),
                            primaryColor.copy(alpha = 0.4f),
                            primaryColor.copy(alpha = 0.1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (currentStep) {
                    0 -> {
                        Text(
                            text = "Prepare",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Get comfortable",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                    1, 2, 3 -> {
                        Text(
                            text = (4 - currentStep).toString(), // Shows 3, 2, 1
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 72.sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
        
        // Floating particles during preparation
        FloatingParticles(
            modifier = Modifier.matchParentSize(),
            particleCount = 10,
            colors = listOf(
                primaryColor.copy(alpha = 0.3f),
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
            )
        )
    }
}

/**
 * 🎉 COMPLETION CELEBRATION
 */
@Composable
fun CompletionCelebration(
    sessionDuration: Int, // in minutes
    breathingPattern: String,
    onCelebrationComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val primaryColor = MaterialTheme.colorScheme.primary
    
    // Celebration animations
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")
    val celebrationScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "celebration_scale"
    )
    
    val sparkleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle_alpha"
    )
    
    // Celebration sequence
    LaunchedEffect(Unit) {
        // Step 0: Completion chimes (2 seconds)
        currentStep = 0
        delay(2000)
        
        // Step 1: Celebration message (3 seconds)
        currentStep = 1
        delay(3000)
        
        // Step 2: Session summary (3 seconds)
        currentStep = 2
        delay(3000)
        
        // Complete celebration
        onCelebrationComplete()
    }
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Celebration sparkles
        repeat(5) { index ->
            Box(
                modifier = Modifier
                    .size((150 + index * 30).dp)
                    .scale(celebrationScale)
                    .clip(CircleShape)
                    .background(
                        primaryColor.copy(alpha = sparkleAlpha * (0.15f - index * 0.02f))
                    )
            )
        }
        
        // Main celebration content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            when (currentStep) {
                0 -> {
                    // Completion chimes
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(celebrationScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f),
                                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔔",
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Meditation Complete",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
                
                1 -> {
                    // Celebration message
                    Text(
                        text = "🌸",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 64.sp
                        ),
                        modifier = Modifier.scale(celebrationScale)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Well Done!",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = primaryColor,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "You've completed your meditation practice",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
                
                2 -> {
                    // Session summary
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Session Summary",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Duration:",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "$sessionDuration minutes",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = primaryColor
                                )
                            }
                            
                            if (breathingPattern.isNotEmpty()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Technique:",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        text = breathingPattern,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = primaryColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Enhanced floating particles for celebration
        FloatingParticles(
            modifier = Modifier.matchParentSize(),
            particleCount = 25,
            colors = listOf(
                primaryColor.copy(alpha = 0.4f),
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
            )
        )
    }
}
