package com.jonathon.blossom.ui.insights

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import com.jonathon.blossom.data.*

/**
 * 📊✨ BEAUTIFUL INSIGHTS SCREEN ✨📊
 * Your spiritual journey analytics and progress tracking
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    onNavigateToMilestones: () -> Unit = {},
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    // � SCROLL STATE MANAGEMENT - Always start at top when navigating to this screen
    val listState = rememberLazyListState()

    // �📊 REFRESH DATA WHEN SCREEN BECOMES VISIBLE
    LaunchedEffect(Unit) {
        viewModel.refreshData()
        // Reset scroll position to top when screen is navigated to
        listState.animateScrollToItem(0)
    }
    
    LazyColumn(
        state = listState,
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
        
        // 🏆 MILESTONES SECTION
        item {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(animationSpec = tween(delayMillis = 300)) + fadeIn()
            ) {
                MilestoneSection(
                    achievements = uiState.achievements,
                    onViewAllMilestones = onNavigateToMilestones
                )
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
                Text("This will permanently delete all your meditation sessions, streaks, and milestones. This action cannot be undone.")
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
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                tint = color.copy(alpha = 0.8f),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = color.copy(alpha = 0.8f),
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
    val maxMinutes = weeklyData.meditationTimes.maxOrNull()?.coerceAtLeast(1) ?: 1
    val barMaxHeight = 100.dp
    val barColor = MaterialTheme.colorScheme.primary
    val barEmptyColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weeklyData.dates.forEachIndexed { index, date ->
                    val timeMinutes = (weeklyData.meditationTimes.getOrNull(index) ?: 0) / 60f
                    val animatedHeight by animateDpAsState(
                        targetValue = if (maxMinutes > 0) (timeMinutes / maxMinutes * barMaxHeight.value).dp else 4.dp,
                        animationSpec = tween(durationMillis = 600), label = "bar_height"
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(maxOf(animatedHeight, 4.dp))
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (timeMinutes > 0f) barColor else barEmptyColor
                                )
                        )
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
 * 🏆 MILESTONE SECTION
 * Display unlocked milestones organized by category - CLICKABLE to view all
 */
@Composable
fun MilestoneSection(
    achievements: List<Achievement>,
    onViewAllMilestones: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewAllMilestones() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    text = "Milestones",
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
                    text = "Complete your first habit, meditation, journal, or prayer to unlock milestones! 🌟",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Group achievements by category
                val categorizedAchievements = mutableListOf<Pair<String, List<Achievement>>>()
                
                // Meditation achievements
                val meditationAchievements = unlockedAchievements.filter { 
                    it.category in listOf(
                        AchievementCategory.MEDITATION_COUNT,
                        AchievementCategory.MEDITATION_STREAK,
                        AchievementCategory.MEDITATION_TIME,
                        AchievementCategory.MEDITATION_CONSISTENCY
                    )
                }
                if (meditationAchievements.isNotEmpty()) {
                    categorizedAchievements.add("🧘‍♂️ Meditation" to meditationAchievements)
                }
                
                // Journal achievements
                val journalAchievements = unlockedAchievements.filter { 
                    it.category in listOf(
                        AchievementCategory.JOURNAL_ENTRIES,
                        AchievementCategory.JOURNAL_STREAK
                    )
                }
                if (journalAchievements.isNotEmpty()) {
                    categorizedAchievements.add("📝 Journal" to journalAchievements)
                }
                
                // Prayer achievements
                val prayerAchievements = unlockedAchievements.filter { 
                    it.category in listOf(
                        AchievementCategory.PRAYERS_ANSWERED,
                        AchievementCategory.PRAYER_CONSISTENCY
                    )
                }
                if (prayerAchievements.isNotEmpty()) {
                    categorizedAchievements.add("🙏 Prayers" to prayerAchievements)
                }
                
                // Habit achievements
                val habitAchievements = unlockedAchievements.filter { 
                    it.category in listOf(
                        AchievementCategory.HABIT_STREAK,
                        AchievementCategory.HABIT_COUNT,
                        AchievementCategory.HABIT_COMEBACK,
                        AchievementCategory.MULTI_HABIT_STREAK
                    )
                }
                if (habitAchievements.isNotEmpty()) {
                    categorizedAchievements.add("✅ Habits" to habitAchievements)
                }
                
                // Other achievements
                val otherAchievements = unlockedAchievements.filter { 
                    it.category !in listOf(
                        AchievementCategory.MEDITATION_COUNT,
                        AchievementCategory.MEDITATION_STREAK,
                        AchievementCategory.MEDITATION_TIME,
                        AchievementCategory.MEDITATION_CONSISTENCY,
                        AchievementCategory.JOURNAL_ENTRIES,
                        AchievementCategory.JOURNAL_STREAK,
                        AchievementCategory.PRAYERS_ANSWERED,
                        AchievementCategory.PRAYER_CONSISTENCY,
                        AchievementCategory.HABIT_STREAK,
                        AchievementCategory.HABIT_COUNT,
                        AchievementCategory.HABIT_COMEBACK,
                        AchievementCategory.MULTI_HABIT_STREAK
                    )
                }
                if (otherAchievements.isNotEmpty()) {
                    categorizedAchievements.add("🌟 Other" to otherAchievements)
                }

                // Display categorized achievements
                var totalShown = 0
                val maxToShow = 6 // Show up to 6 achievements total
                
                categorizedAchievements.forEach { (categoryName, categoryAchievements) ->
                    if (totalShown < maxToShow) {
                        // Category header with elegant design
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text(
                                text = categoryName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            
                            // Elegant divider line
                            HorizontalDivider(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                thickness = 1.dp
                            )
                        }
                        
                        // Show achievements in this category (up to 2 per category)
                        val achievementsToShow = categoryAchievements.take(2.coerceAtMost(maxToShow - totalShown))
                        achievementsToShow.forEach { achievement ->
                            AchievementItem(achievement = achievement)
                            totalShown++
                            if (totalShown < maxToShow && achievement != achievementsToShow.last()) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        
                        // Add spacing between categories if not the last category and not at limit
                        if (totalShown < maxToShow && categoryName != categorizedAchievements.last().first) {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }

                // Show total count if there are more achievements
                if (unlockedAchievements.size > maxToShow) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "+${unlockedAchievements.size - maxToShow} more milestones",
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
 * 🏅 MILESTONE ITEM
 * Individual milestone display
 */
// Achievement Item with matched inner and outer colors
@Composable
fun AchievementItem(achievement: Achievement) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Achievement icon with subtle background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = achievement.icon,
                    fontSize = 24.sp
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    lineHeight = 16.sp
                )
            }
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
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
            color = MaterialTheme.colorScheme.onSurface
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
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                text = "Clear all meditation sessions, streaks, and milestones to start fresh.",
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
 * Convert date to day abbreviation (Mon, Tue, etc.)
 */
fun getDayLabel(date: String): String {
    return try {
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val parsedDate = dateFormat.parse(date)
        val dayFormat = java.text.SimpleDateFormat("EEE", java.util.Locale.getDefault())
        dayFormat.format(parsedDate ?: return date.takeLast(2))
    } catch (e: Exception) {
        // Fallback to last 2 characters if parsing fails
        date.takeLast(2)
    }
}
