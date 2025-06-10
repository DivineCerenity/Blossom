package com.example.blossom

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.blossom.ui.components.GradientText
import com.example.blossom.ui.settings.SettingsScreen
import com.example.blossom.ui.settings.SettingsViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.blossom.ui.journal.AddEditJournalScreen
import com.example.blossom.ui.journal.JournalListScreen
import com.example.blossom.ui.journal.JournalListViewModel
import com.example.blossom.ui.journal.JournalViewModel
import com.example.blossom.ui.checklist.ChecklistScreen
import com.example.blossom.ui.meditation.MeditationScreen
import com.example.blossom.ui.prayer.PrayerRequestsScreen
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlossomApp() {
    val navController = rememberNavController()

    // Get current theme for dynamic gradient text
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val settingsUiState by settingsViewModel.uiState.collectAsState()

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
                        containerColor = Color.Transparent
                    )
                )
            },
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Home.route, Modifier.padding(innerPadding)) {
            composable(Screen.Home.route) {
                DashboardScreen()
            }
            composable(Screen.Meditate.route) {
                MeditationScreen()
            }
            composable(Screen.Checklist.route) {
                ChecklistScreen()
            }
            composable(Screen.Prayers.route) {
                PrayerRequestsScreen()
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
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
            }
        }
    }
}