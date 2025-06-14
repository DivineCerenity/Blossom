package com.jonathon.blossom.ui.achievements

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jonathon.blossom.data.Achievement
import com.jonathon.blossom.data.AchievementCategory
import com.jonathon.blossom.ui.insights.InsightsViewModel

/**
 * 🏆 MILESTONES SCREEN
 * Complete view of all milestones - unlocked and locked
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    onNavigateBack: () -> Unit,
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // 📱 SCROLL STATE MANAGEMENT - Always start at top when navigating to this screen
    val listState = rememberLazyListState()
    
    // Reset scroll position to top when screen is navigated to
    LaunchedEffect(Unit) {
        listState.animateScrollToItem(0)
    }
    
    // Group achievements by category
    val achievementsByCategory = uiState.achievements.groupBy { it.category }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🏆 Milestones",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // 📊 MILESTONE SUMMARY
                item {
                    AchievementSummaryCard(
                        totalAchievements = uiState.achievements.size,
                        unlockedAchievements = uiState.achievements.count { it.unlockedAt != null }
                    )
                }
                
                // 🏆 ACHIEVEMENT CATEGORIES
                AchievementCategory.values().forEach { category ->
                    val categoryAchievements = achievementsByCategory[category] ?: emptyList()
                    if (categoryAchievements.isNotEmpty()) {
                        item {
                            AchievementCategorySection(
                                category = category,
                                achievements = categoryAchievements
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 📊 MILESTONE SUMMARY CARD
 */
@Composable
fun AchievementSummaryCard(
    totalAchievements: Int,
    unlockedAchievements: Int
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎯",
                fontSize = 48.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "$unlockedAchievements / $totalAchievements",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Milestones Unlocked",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress bar
            LinearProgressIndicator(
                progress = if (totalAchievements > 0) unlockedAchievements.toFloat() / totalAchievements else 0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        }
    }
}

/**
 * 🏅 MILESTONE CATEGORY SECTION
 */
@Composable
fun AchievementCategorySection(
    category: AchievementCategory,
    achievements: List<Achievement>
) {
    val categoryName = when (category) {
        AchievementCategory.MEDITATION_COUNT -> "🧘‍♂️ Meditation Milestones"
        AchievementCategory.MEDITATION_STREAK -> "🔥 Meditation Streaks"
        AchievementCategory.MEDITATION_TIME -> "⏰ Time Milestones"
        AchievementCategory.MEDITATION_CONSISTENCY -> "📅 Consistency"
        AchievementCategory.JOURNAL_ENTRIES -> "📝 Journal Milestones"
        AchievementCategory.JOURNAL_STREAK -> "📖 Journal Streaks"
        AchievementCategory.PRAYERS_ANSWERED -> "🙏 Prayer Milestones"
        AchievementCategory.PRAYER_CONSISTENCY -> "💒 Prayer Consistency"
        AchievementCategory.PATTERN_EXPLORER -> "🌬️ Breathing Patterns"
        AchievementCategory.FREQUENCY_FINDER -> "🎵 Binaural Beats"
        AchievementCategory.THEME_EXPLORER -> "🎨 Theme Explorer"
        AchievementCategory.MILESTONE_MASTER -> "🎯 Milestones"
        AchievementCategory.DEDICATION_WARRIOR -> "⚔️ Dedication"
        AchievementCategory.MINDFULNESS_GURU -> "🧠 Mindfulness"
        AchievementCategory.SPIRITUAL_SEEKER -> "✨ Spiritual Journey"
        AchievementCategory.HABIT_STREAK -> "🔥 Habit Streaks"
        AchievementCategory.HABIT_COUNT -> "✅ Habit Completions"
        AchievementCategory.HABIT_COMEBACK -> "🔄 Habit Comebacks"
        AchievementCategory.MULTI_HABIT_STREAK -> "🌟 Multi-Habit Streaks"
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = categoryName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            achievements.sortedBy { it.threshold }.forEach { achievement ->
                AchievementItem(achievement = achievement)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

/**
 * 🏅 INDIVIDUAL MILESTONE ITEM
 */
@Composable
fun AchievementItem(achievement: Achievement) {
    val isUnlocked = achievement.unlockedAt != null
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon or lock
            if (isUnlocked) {
                Text(
                    text = achievement.icon,
                    fontSize = 28.sp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = if (isUnlocked) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    }
                )
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUnlocked) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    }
                )
                
                if (isUnlocked && achievement.unlockedAt != null) {
                    Text(
                        text = "Unlocked ${formatUnlockDate(achievement.unlockedAt!!)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * 📅 FORMAT UNLOCK DATE
 */
fun formatUnlockDate(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val formatter = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
    return formatter.format(date)
}
