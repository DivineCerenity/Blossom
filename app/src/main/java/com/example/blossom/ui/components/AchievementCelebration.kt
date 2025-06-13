package com.example.blossom.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.blossom.data.Achievement
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * 🎉 ACHIEVEMENT CELEBRATION POPUP
 * Epic confetti animation with achievement display
 */
@Composable
fun AchievementCelebrationDialog(
    achievements: List<Achievement>,
    onDismiss: () -> Unit
) {
    if (achievements.isEmpty()) return

    var showDialog by remember { mutableStateOf(true) }
    var showConfetti by remember { mutableStateOf(true) }

    // 🎉 Auto-dismiss after 7 seconds for proper celebration time!
    LaunchedEffect(achievements) {
        delay(7000)
        showDialog = false
        onDismiss()
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = {
                showDialog = false
                onDismiss()
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // 🎊 CONFETTI BACKGROUND
                if (showConfetti) {
                    ConfettiAnimation()
                }

                // 🏆 ACHIEVEMENT CARD
                AnimatedVisibility(
                    visible = showDialog,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    AchievementCard(
                        achievements = achievements,
                        onDismiss = {
                            showDialog = false
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

/**
 * 🎊 CONFETTI ANIMATION
 * Beautiful falling confetti particles
 */
@Composable
fun ConfettiAnimation() {
    val confettiColors = listOf(
        Color(0xFFFFD700), // Gold
        Color(0xFFFF6B6B), // Red
        Color(0xFF4ECDC4), // Teal
        Color(0xFF45B7D1), // Blue
        Color(0xFF96CEB4), // Green
        Color(0xFFFECA57), // Yellow
        Color(0xFFFF9FF3), // Pink
        Color(0xFFBB6BD9)  // Purple
    )

    // Create multiple confetti pieces
    repeat(20) { index ->
        ConfettiPiece(
            color = confettiColors[index % confettiColors.size],
            delay = index * 100L
        )
    }
}

/**
 * 🎊 INDIVIDUAL CONFETTI PIECE
 */
@Composable
fun ConfettiPiece(
    color: Color,
    delay: Long
) {
    var animationStarted by remember { mutableStateOf(false) }
    
    val animatedY by animateFloatAsState(
        targetValue = if (animationStarted) 1000f else -100f,
        animationSpec = tween(
            durationMillis = 3000 + Random.nextInt(2000),
            delayMillis = delay.toInt(),
            easing = LinearEasing
        ),
        label = "confetti_fall"
    )
    
    val animatedRotation by animateFloatAsState(
        targetValue = if (animationStarted) 720f else 0f,
        animationSpec = tween(
            durationMillis = 3000 + Random.nextInt(2000),
            delayMillis = delay.toInt(),
            easing = LinearEasing
        ),
        label = "confetti_rotation"
    )

    LaunchedEffect(Unit) {
        animationStarted = true
    }

    Box(
        modifier = Modifier
            .offset(
                x = Random.nextInt(-200, 200).dp,
                y = animatedY.dp
            )
            .size(8.dp)
            .scale(Random.nextFloat() * 0.5f + 0.5f)
            .clip(RoundedCornerShape(2.dp))
            .background(color)
    )
}

/**
 * 🏆 ACHIEVEMENT CARD
 * Main achievement display card
 */
@Composable
fun AchievementCard(
    achievements: List<Achievement>,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .wrapContentHeight(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // 🎉 CELEBRATION HEADER
            Text(
                text = "🎉",
                fontSize = 48.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = if (achievements.size == 1) "Achievement Unlocked!" else "Achievements Unlocked!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🏆 ACHIEVEMENT LIST
            LazyColumn(
                modifier = Modifier.heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(achievements) { achievement ->
                    AchievementCelebrationItem(achievement = achievement)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🌟 MOTIVATIONAL MESSAGE
            Text(
                text = when {
                    achievements.size >= 3 -> "Incredible progress! You're on fire! 🔥"
                    achievements.size == 2 -> "Amazing work! Keep it up! ⭐"
                    else -> "Great job! Every step counts! 🌟"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 🏅 ACHIEVEMENT CELEBRATION ITEM
 * Individual achievement in the celebration popup
 */
@Composable
fun AchievementCelebrationItem(achievement: Achievement) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = achievement.icon,
                fontSize = 32.sp
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
