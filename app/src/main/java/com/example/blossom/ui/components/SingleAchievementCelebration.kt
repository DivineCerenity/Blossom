package com.example.blossom.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.blossom.data.Achievement
import kotlinx.coroutines.delay

/**
 * 🎉 SINGLE ACHIEVEMENT CELEBRATION DIALOG
 * Beautiful popup for individual achievement unlocks
 */
@Composable
fun SingleAchievementCelebrationDialog(
    achievement: Achievement,
    onDismiss: () -> Unit
) {
    var showDialog by remember { mutableStateOf(true) }
    
    // 🎉 Auto-dismiss after 6 seconds for proper celebration time!
    LaunchedEffect(Unit) {
        delay(6000)
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
            SingleAchievementCard(achievement = achievement)
        }
    }
}

/**
 * 🏆 SINGLE ACHIEVEMENT CARD
 * Beautiful animated card for individual achievement
 */
@Composable
fun SingleAchievementCard(achievement: Achievement) {
    // Animation states
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    // Scale animation
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "achievement_scale"
    )
    
    // Icon bounce animation
    val iconScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "icon_scale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .scale(scale),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                        ),
                        radius = 400f
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 🎉 Achievement Unlocked Header
                Text(
                    text = "🎉 Achievement Unlocked! 🎉",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )
                
                // 🏆 Achievement Icon
                Text(
                    text = achievement.icon,
                    fontSize = (64 * iconScale).sp,
                    modifier = Modifier.scale(iconScale)
                )
                
                // 🏅 Achievement Title
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )
                
                // 📝 Achievement Description
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // ✨ Congratulations Message
                Text(
                    text = "Keep up the amazing work! ✨",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * 🎊 ACHIEVEMENT CELEBRATION MANAGER
 * Manages showing individual achievement celebrations
 */
@Composable
fun AchievementCelebrationManager(
    achievements: List<Achievement>,
    onAllDismissed: () -> Unit
) {
    var currentIndex by remember { mutableStateOf(0) }
    
    if (achievements.isNotEmpty() && currentIndex < achievements.size) {
        SingleAchievementCelebrationDialog(
            achievement = achievements[currentIndex],
            onDismiss = {
                if (currentIndex < achievements.size - 1) {
                    currentIndex++
                } else {
                    onAllDismissed()
                }
            }
        )
    }
}
