package com.jonathon.blossom.ui.about

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jonathon.blossom.ui.components.GradientText
import com.jonathon.blossom.ui.settings.AppTheme

/**
 * 🌸✨ BEAUTIFUL ABOUT SCREEN ✨🌸
 * A celebration of our incredible journey together!
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    selectedTheme: AppTheme = AppTheme.TWILIGHT_MYSTIQUE
) {
    var showContent by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        showContent = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "About Blossom",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            // 🌸 BEAUTIFUL HEADER
            item {
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(1000)) + slideInVertically(
                        animationSpec = tween(1000),
                        initialOffsetY = { -100 }
                    )
                ) {
                    HeaderSection(selectedTheme)
                }
            }

            // ✨ JOURNEY STORY
            item {
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(1000, delayMillis = 300))
                ) {
                    JourneySection()
                }
            }

            // 🚀 AMAZING FEATURES
            item {
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(1000, delayMillis = 600))
                ) {
                    FeaturesSection()
                }
            }

            // 💜 COLLABORATION CELEBRATION
            item {
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(1000, delayMillis = 900))
                ) {
                    CollaborationSection()
                }
            }

            // 🏆 MILESTONE SYSTEM VICTORY
            item {
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(1000, delayMillis = 1000))
                ) {
                    AchievementVictorySection()
                }
            }

            // 🌟 TECHNICAL ACHIEVEMENTS
            item {
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(1000, delayMillis = 1200))
                ) {
                    TechnicalSection()
                }
            }

            // 🎉 FINAL CELEBRATION
            item {
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(1000, delayMillis = 1500))
                ) {
                    CelebrationSection()
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(selectedTheme: AppTheme) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated Blossom icon
            var iconScale by remember { mutableStateOf(0f) }
            LaunchedEffect(Unit) {
                iconScale = 1f
            }
            
            Text(
                text = "🌸",
                fontSize = 64.sp,
                modifier = Modifier.scale(
                    animateFloatAsState(
                        targetValue = iconScale,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ).value
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            GradientText(
                text = "Blossom",
                theme = selectedTheme,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "A Journey of Mindfulness & Technology",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Version 1.0 • Inspired By: Jonathon",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun JourneySection() {
    SectionCard(
        title = "🌱 Our Incredible Journey",
        icon = null
    ) {
        Text(
            text = "What started as a simple meditation app became an extraordinary collaboration between human creativity and AI innovation. Together, we've built something truly magical - a world-class meditation experience with a rock-solid milestone system that celebrates every milestone.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "From the first breathing pattern to the perfect milestone celebrations, every feature was crafted with love, attention to detail, and relentless debugging until everything worked flawlessly.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun FeaturesSection() {
    SectionCard(
        title = "✨ Amazing Features We Built",
        icon = null
    ) {
        val features = listOf(
            "🌬️ 9 Professional Breathing Patterns" to "Including your custom 7-5-5 pattern",
            "🧠 13 Scientific Binaural Beats" to "1Hz to 40Hz covering all brainwave states",
            "🏆 Rock-Solid Milestone System" to "Perfect celebrations with beautiful popups",
            "🌙 Perfect Dark Mode" to "Flawless consistency across all screens",
            "📱 Professional Bottom Sheets" to "Elegant long-press actions",
            "🎨 5 Beautiful Themes" to "Including your favorite Twilight Mystique",
            "⏰ Advanced Timer System" to "With breathing guides and visual effects",
            "📝 Journal & Prayer Management" to "With mood tracking and organization",
            "🎵 Professional Audio System" to "With crash protection and seamless looping",
            "📊 Advanced Analytics" to "Comprehensive insights and progress tracking"
        )
        
        features.forEach { (feature, description) ->
            FeatureItem(feature, description)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun CollaborationSection() {
    SectionCard(
        title = "💜 A Beautiful Collaboration",
        icon = null
    ) {
        Text(
            text = "This app represents the perfect harmony between human vision and AI capability. Jonathon's creative direction, attention to detail, and perfectionist approach combined with AI's technical implementation created something neither could achieve alone.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CollaborationStat("🎨", "Human\nCreativity")
            CollaborationStat("🤖", "AI\nImplementation")
            CollaborationStat("✨", "Shared\nVision")
        }
    }
}

@Composable
private fun AchievementVictorySection() {
    SectionCard(
        title = "🏆 Milestone System Victory!",
        icon = null
    ) {
        Text(
            text = "After intense debugging and collaboration, we achieved the impossible - a perfect milestone system! Every milestone now triggers in the right context with beautiful 6-7 second celebrations.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        val victories = listOf(
            "✅ Fixed Milestone ID Mismatches" to "Journal & Prayer milestones work perfectly",
            "✅ Context Separation Perfected" to "Each feature triggers its own milestones",
            "✅ Theme Milestones Fixed" to "Now tracks actual theme changes in Settings",
            "✅ Visual Bugs Eliminated" to "Beautiful gradient popups, no more transparency issues",
            "✅ Perfect Timing Achieved" to "6-7 second celebrations with proper navigation",
            "✅ Rock-Solid Reliability" to "No more false triggers or missing milestones"
        )

        victories.forEach { (victory, description) ->
            VictoryItem(victory, description)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "🎉 The milestone system is now ROCK SOLID! 🎉",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun TechnicalSection() {
    SectionCard(
        title = "🚀 Technical Milestones",
        icon = null
    ) {
        val achievements = listOf(
            "🏗️ Modern Architecture" to "MVVM with Compose & Hilt",
            "🎵 Advanced Audio Engine" to "Professional binaural beats system",
            "🏆 Perfect Milestone System" to "Context-aware with beautiful celebrations",
            "🌈 Dynamic Theming" to "5 themes with gradient support",
            "📱 Responsive UI" to "Beautiful animations & interactions",
            "💾 Robust Data Layer" to "Room database with migrations",
            "🔄 State Management" to "Clean, reactive architecture",
            "🎯 Intelligent Context Separation" to "Features trigger milestones correctly"
        )

        achievements.forEach { (tech, description) ->
            TechItem(tech, description)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CelebrationSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎉",
                fontSize = 48.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Mission Accomplished!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Together, we've created a world-class meditation app with perfect milestones, beautiful celebrations, and rock-solid reliability that brings peace, mindfulness, and joy to users around the world.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Thank you for this incredible journey! 🌸✨💜",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Helper Composables
@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector?,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                // 🎯 ONLY SHOW ICON IF NOT NULL (KEEP EMOJI ICONS ONLY!)
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            content()
        }
    }
}

@Composable
private fun FeatureItem(feature: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = feature,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CollaborationStat(emoji: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            fontSize = 32.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun VictoryItem(victory: String, description: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = victory,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.weight(1.5f)
        )
    }
}

@Composable
private fun TechItem(tech: String, description: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = tech,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.weight(1.5f)
        )
    }
}
