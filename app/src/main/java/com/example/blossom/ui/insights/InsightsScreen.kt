package com.example.blossom.ui.insights

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.blossom.data.*

/**
 * 📊✨ BEAUTIFUL INSIGHTS SCREEN ✨📊
 * Your spiritual journey analytics and progress tracking
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    // 📊 REFRESH DATA WHEN SCREEN BECOMES VISIBLE
    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // 🔥 MEDITATION STREAK CARD
        item {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically() + fadeIn()
            ) {
                StreakCard(
                    currentStreak = uiState.meditationStats.currentStreak,
                    longestStreak = uiState.meditationStats.longestStreak
                )
            }
        }
        
        // 📊 QUICK STATS ROW
        item {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(animationSpec = tween(delayMillis = 100)) + fadeIn()
            ) {
                QuickStatsRow(
                    totalSessions = uiState.meditationStats.totalSessions,
                    totalTime = uiState.meditationStats.totalTime,
                    averageTime = uiState.meditationStats.averageSessionLength
                )
            }
        }
        
        // 📈 WEEKLY MEDITATION CHART
        item {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(animationSpec = tween(delayMillis = 200)) + fadeIn()
            ) {
                WeeklyMeditationChart(weeklyData = uiState.weeklyData)
            }
        }
        
        // 🏆 ACHIEVEMENTS SECTION
        item {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(animationSpec = tween(delayMillis = 300)) + fadeIn()
            ) {
                AchievementSection(achievements = uiState.achievements)
            }
        }
        
        // 🌬️ FAVORITE PATTERNS
        item {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(animationSpec = tween(delayMillis = 400)) + fadeIn()
            ) {
                FavoritePatternCard(
                    favoritePattern = uiState.meditationStats.favoritePattern,
                    favoriteBeat = uiState.meditationStats.favoriteBinauralBeat,
                    favoriteTheme = uiState.meditationStats.favoriteTheme
                )
            }
        }
        
        // 📝 JOURNAL INSIGHTS
        item {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(animationSpec = tween(delayMillis = 500)) + fadeIn()
            ) {
                JournalInsightsCard(insights = uiState.journalInsights)
            }
        }
        
        // 🙏 PRAYER INSIGHTS
        item {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(animationSpec = tween(delayMillis = 600)) + fadeIn()
            ) {
                PrayerInsightsCard(insights = uiState.prayerInsights)
            }
        }

        // 🔄 RESET STATS BUTTON
        item {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(animationSpec = tween(delayMillis = 700)) + fadeIn()
            ) {
                ResetStatsCard(onResetClick = { showResetDialog = true })
            }
        }
    }

    // 🔄 RESET CONFIRMATION DIALOG
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = "Reset All Statistics?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("This will permanently delete all your meditation sessions, streaks, and achievements. This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetAllStats()
                        showResetDialog = false
                    }
                ) {
                    Text("Reset", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * 🔥 STREAK CARD
 * Beautiful animated streak counter
 */
@Composable
fun StreakCard(
    currentStreak: Int,
    longestStreak: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Fire emoji with animation
            val infiniteTransition = rememberInfiniteTransition(label = "fire")
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "fire_scale"
            )
            
            Text(
                text = "🔥",
                fontSize = (32 * scale).sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Current streak
            Text(
                text = "$currentStreak",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            
            Text(
                text = "Day Streak",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Longest streak
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Longest: $longestStreak days",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 * 📊 QUICK STATS ROW
 * Three key metrics in a row
 */
@Composable
fun QuickStatsRow(
    totalSessions: Int,
    totalTime: Int,
    averageTime: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Total Sessions
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.SelfImprovement,
            value = totalSessions.toString(),
            label = "Sessions",
            color = MaterialTheme.colorScheme.primary
        )
        
        // Total Time
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Schedule,
            value = formatTotalTime(totalTime), // 🕐 ELEGANT H:M:S FORMAT
            label = "Total Time",
            color = MaterialTheme.colorScheme.secondary
        )
        
        // Average Time
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.TrendingUp,
            value = formatTime(averageTime),
            label = "Average",
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

/**
 * 📈 STAT CARD
 * Individual statistic card
 */
@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = color,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 📈 WEEKLY MEDITATION CHART
 * Beautiful bar chart showing weekly progress
 */
@Composable
fun WeeklyMeditationChart(weeklyData: WeeklyData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "This Week",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Simple bar chart representation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weeklyData.dates.forEachIndexed { index, date ->
                    val time = weeklyData.meditationTimes.getOrNull(index) ?: 0
                    val maxTime = weeklyData.meditationTimes.maxOrNull() ?: 1
                    val height = if (maxTime > 0) ((time.toFloat() / maxTime) * 60).dp else 4.dp

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Bar
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(maxOf(height, 4.dp))
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (time > 0) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                        )

                        // Day label
                        Text(
                            text = getDayLabel(date),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 🏆 ACHIEVEMENT SECTION
 * Display unlocked achievements
 */
@Composable
fun AchievementSection(achievements: List<Achievement>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🏆",
                    fontSize = 24.sp
                )
                Text(
                    text = "Achievements",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val unlockedAchievements = achievements.filter { it.unlockedAt != null }

            if (unlockedAchievements.isEmpty()) {
                Text(
                    text = "Complete your first meditation to unlock achievements! 🌟",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                unlockedAchievements.take(3).forEach { achievement ->
                    AchievementItem(achievement = achievement)
                    if (achievement != unlockedAchievements.last()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                if (unlockedAchievements.size > 3) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "+${unlockedAchievements.size - 3} more achievements",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * ⏰ FORMAT TIME HELPER
 * Convert seconds to readable format with hours, minutes and seconds
 */
fun formatTime(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60

    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 && remainingSeconds > 0 -> "${minutes}m ${remainingSeconds}s"
        minutes > 0 -> "${minutes}m"
        else -> "${remainingSeconds}s"
    }
}

/**
 * ⏰ FORMAT TOTAL TIME FOR INSIGHTS
 * Convert seconds to elegant Hours:Minutes:Seconds format for main stats
 */
fun formatTotalTime(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60

    return when {
        hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, remainingSeconds)
        minutes > 0 -> String.format("%d:%02d", minutes, remainingSeconds)
        else -> "0:${String.format("%02d", remainingSeconds)}"
    }
}

/**
 * 🏅 ACHIEVEMENT ITEM
 * Individual achievement display
 */
@Composable
fun AchievementItem(achievement: Achievement) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = achievement.icon,
            fontSize = 24.sp
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = achievement.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = achievement.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * 🌬️ FAVORITE PATTERN CARD
 * Display user's favorite meditation settings
 */
@Composable
fun FavoritePatternCard(
    favoritePattern: String?,
    favoriteBeat: String?,
    favoriteTheme: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🌟",
                    fontSize = 24.sp
                )
                Text(
                    text = "Your Favorites",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Favorite Pattern
            if (favoritePattern != null) {
                FavoriteItem(
                    icon = "🌬️",
                    label = "Breathing Pattern",
                    value = favoritePattern
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Favorite Binaural Beat
            if (favoriteBeat != null) {
                FavoriteItem(
                    icon = "🧠",
                    label = "Binaural Beat",
                    value = favoriteBeat
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Favorite Theme
            if (favoriteTheme != null) {
                FavoriteItem(
                    icon = "🎨",
                    label = "Theme",
                    value = favoriteTheme
                )
            }

            if (favoritePattern == null && favoriteBeat == null && favoriteTheme == null) {
                Text(
                    text = "Complete more meditations to see your favorites! ✨",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * ⭐ FAVORITE ITEM
 * Individual favorite setting display
 */
@Composable
fun FavoriteItem(
    icon: String,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = icon,
            fontSize = 20.sp
        )

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * 📝 JOURNAL INSIGHTS CARD
 * Display journal analytics
 */
@Composable
fun JournalInsightsCard(insights: JournalInsights) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "📝",
                    fontSize = 24.sp
                )
                Text(
                    text = "Journal Insights",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InsightItem(
                    value = insights.totalEntries.toString(),
                    label = "Total Entries"
                )
                InsightItem(
                    value = insights.entriesThisWeek.toString(),
                    label = "This Week"
                )
                InsightItem(
                    value = insights.mostCommonMood ?: "N/A",
                    label = "Common Mood"
                )
            }
        }
    }
}

/**
 * 🙏 PRAYER INSIGHTS CARD
 * Display prayer analytics
 */
@Composable
fun PrayerInsightsCard(insights: PrayerInsights) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🙏",
                    fontSize = 24.sp
                )
                Text(
                    text = "Prayer Insights",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InsightItem(
                    value = insights.totalPrayers.toString(),
                    label = "Total Prayers"
                )
                InsightItem(
                    value = insights.answeredPrayers.toString(),
                    label = "Answered"
                )
                InsightItem(
                    value = "${insights.answeredPercentage.toInt()}%",
                    label = "Success Rate"
                )
            }
        }
    }
}

/**
 * 📊 INSIGHT ITEM
 * Individual insight metric
 */
@Composable
fun InsightItem(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 🔄 RESET STATS CARD
 * Option to reset all analytics data
 */
@Composable
fun ResetStatsCard(
    onResetClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🔄",
                    fontSize = 24.sp
                )
                Text(
                    text = "Reset Statistics",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Clear all meditation sessions, streaks, and achievements to start fresh.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onResetClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset All Data")
            }
        }
    }
}

/**
 * 📅 GET DAY LABEL
 * Convert date to day abbreviation
 */
fun getDayLabel(date: String): String {
    // Simple day extraction - could be enhanced
    return when (date.takeLast(2)) {
        else -> date.takeLast(2)
    }
}
