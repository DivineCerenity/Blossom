package com.jonathon.blossom

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jonathon.blossom.ui.components.GradientText
import com.jonathon.blossom.ui.settings.SettingsScreen
import com.jonathon.blossom.ui.about.AboutScreen
import com.jonathon.blossom.ui.settings.AppTheme
import com.jonathon.blossom.ui.settings.SettingsViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.jonathon.blossom.ui.journal.AddEditJournalScreen
import com.jonathon.blossom.ui.journal.JournalListScreen
import com.jonathon.blossom.ui.journal.JournalListViewModel
import com.jonathon.blossom.ui.journal.JournalViewModel
import com.jonathon.blossom.ui.components.AchievementCelebrationManager
import com.jonathon.blossom.ui.habits.DailyHabitsScreen
import com.jonathon.blossom.ui.habits.AddEditHabitScreen
import com.jonathon.blossom.ui.insights.InsightsScreen
import com.jonathon.blossom.ui.meditate.MeditateScreen
import com.jonathon.blossom.ui.prayer.PrayerRequestsScreen
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlossomApp(
    onThemeChanged: (AppTheme) -> Unit = {},
    selectedTheme: AppTheme = AppTheme.TWILIGHT_MYSTIQUE,
    isDarkMode: Boolean = false,
    onDarkModeChanged: (Boolean) -> Unit = {}
) {
    val navController = rememberNavController()

    // Get current theme for dynamic gradient text
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val settingsUiState by settingsViewModel.uiState.collectAsState()

    // Shared state for triggering add dialogs
    var triggerAddPrayer by remember { mutableStateOf(false) }

    Scaffold(
            topBar = {
                // Use CenterAlignedTopAppBar for a centered title
                CenterAlignedTopAppBar(
                    title = {
                        GradientText(
                            text = "Blossom",
                            theme = settingsUiState.selectedTheme,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                navController.navigate(Screen.Settings.route)
                            }
                        ) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },

            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    val currentRoute = currentDestination?.route

                    bottomNavItems.forEach { screen ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                        // Special handling for Journal, Habits, and Prayers tabs when selected
                        if ((screen == Screen.JournalList || screen == Screen.Habits || screen == Screen.Prayers) && isSelected) {
                            NavigationBarItem(
                                icon = {
                                    Box(
                                        modifier = androidx.compose.ui.Modifier
                                            .size(28.dp)
                                            .background(
                                                // Use actual theme primary color
                                                MaterialTheme.colorScheme.primary,
                                                RoundedCornerShape(8.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = when (screen) {
                                                Screen.JournalList -> "Add Entry"
                                                Screen.Habits -> "Add Habit"
                                                Screen.Prayers -> "Add Prayer"
                                                else -> "Add"
                                            },
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = androidx.compose.ui.Modifier.size(18.dp)
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = screen.label,
                                        style = MaterialTheme.typography.labelSmall // 🎯 SMALLER for navbar
                                    )
                                },
                                selected = true,
                                onClick = {
                                    when (screen) {
                                        Screen.JournalList -> {
                                            navController.navigate(Screen.AddEditJournal.route)
                                        }
                                        Screen.Habits -> {
                                            navController.navigate("addHabit")
                                        }
                                        Screen.Prayers -> {
                                            triggerAddPrayer = true
                                        }
                                        else -> {}
                                    }
                                }
                            )
                        } else {
                            // Regular navigation items
                            NavigationBarItem(
                                icon = { Icon(screen.icon, contentDescription = screen.label) },
                                label = {
                                    Text(
                                        text = screen.label,
                                        style = MaterialTheme.typography.labelSmall // 🎯 SMALLER for navbar
                                    )
                                },
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = false
                                        }
                                        launchSingleTop = true
                                        restoreState = false
                                    }
                                }
                            )
                        }
                    }
                }
            }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Insights.route, Modifier.padding(innerPadding)) {
            composable(Screen.Home.route) {
                DashboardScreen()  // Keep for settings navigation
            }
            composable(Screen.Meditate.route) {
                MeditateScreen()
            }
            composable(Screen.Habits.route) {
                DailyHabitsScreen(
                    onNavigateToAddHabit = { navController.navigate("addHabit") },
                    onNavigateToEditHabit = { habitId -> navController.navigate("editHabit/$habitId") }
                )
            }
            composable(Screen.Insights.route) {
                InsightsScreen(
                    onNavigateToMilestones = { navController.navigate("achievements") }
                )  // 📊 BEAUTIFUL NEW INSIGHTS SCREEN!
            }
            composable(Screen.Prayers.route) {
                PrayerRequestsScreen(
                    addPrayerTrigger = triggerAddPrayer,
                    onAddPrayerTriggered = { triggerAddPrayer = false }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onThemeChanged = onThemeChanged,
                    selectedTheme = selectedTheme,
                    isDarkMode = isDarkMode,
                    onDarkModeChanged = onDarkModeChanged,
                    onNavigateToAbout = { navController.navigate("about") }
                )
            }

            composable("about") {
                AboutScreen(
                    onNavigateBack = { navController.popBackStack() },
                    selectedTheme = selectedTheme
                )
            }

            composable("achievements") {
                com.jonathon.blossom.ui.achievements.AchievementsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("addHabit") {
                AddEditHabitScreen(
                    onNavigateBack = { navController.popBackStack() },
                    habitId = null
                )
            }

            composable(
                route = "editHabit/{habitId}",
                arguments = listOf(
                    navArgument("habitId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val habitId = backStackEntry.arguments?.getInt("habitId") ?: -1
                AddEditHabitScreen(
                    onNavigateBack = { navController.popBackStack() },
                    habitId = habitId
                )
            }

            composable(Screen.JournalList.route) {
                val viewModel: JournalListViewModel = hiltViewModel()
                JournalListScreen(
                    viewModel = viewModel,
                    onNavigateToAddEntry = {
                        navController.navigate(Screen.AddEditJournal.route)
                    },
                    onNavigateToEditEntry = { entryId ->
                        // Use Screen.AddEditJournal.route for consistency and append query parameter
                        navController.navigate(Screen.AddEditJournal.route + "?entryId=$entryId")
                    }
                )
            }

            composable(
                // Use Screen.AddEditJournal.route and append optional query parameter for entryId
                route = Screen.AddEditJournal.route + "?entryId={entryId}",
                arguments = listOf(
                    navArgument("entryId") {
                        type = NavType.IntType
                        defaultValue = -1
                    }
                )
            ) { backStackEntry ->
                val entryId = backStackEntry.arguments?.getInt("entryId") ?: -1
                val viewModel: JournalViewModel = hiltViewModel()

                LaunchedEffect(key1 = entryId) {
                    if (entryId != -1) {
                        viewModel.loadEntry(entryId)
                    }
                }

                val newAchievements by viewModel.newAchievements.collectAsState()

                AddEditJournalScreen(
                    uiState = viewModel.uiState.collectAsState().value,
                    onTitleChanged = viewModel::onTitleChanged,
                    onContentChanged = viewModel::onContentChanged,
                    onMoodSelected = viewModel::onMoodSelected,
                    onImageUriChanged = viewModel::onImageUriChanged,
                    onAddImage = viewModel::onAddImage,
                    onDeleteImage = viewModel::onDeleteImage,
                    onSetFeaturedImage = viewModel::onSetFeaturedImage,
                    saveJournalEntry = viewModel::saveJournalEntry,
                    eventHandled = viewModel::eventHandled,
                    onNavigateBack = { navController.popBackStack() },
                    isEditing = entryId != -1
                )

                // 🏆 ACHIEVEMENT CELEBRATION
                if (newAchievements.isNotEmpty()) {
                    AchievementCelebrationManager(
                        achievements = newAchievements,
                        onAllDismissed = {
                            viewModel.clearAchievements()
                        }
                    )
                }
            }
        }
    }
}