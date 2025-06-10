package com.example.blossom

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * This is the single source of truth for all navigation routes in the app.
 */
sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object JournalList : Screen("journal", "Journal", Icons.Default.Book)
    object Checklist : Screen("checklist", "Checklist", Icons.Default.ChecklistRtl)
    object Prayers : Screen("prayers", "Prayers", Icons.Default.FavoriteBorder)
    object Meditate : Screen("meditate", "Meditate", Icons.Default.SelfImprovement)

    // Screens not on the bottom bar
    object AddEditJournal : Screen("addEditJournal", "", Icons.Default.Home)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

// This list defines the items that appear in the bottom navigation bar.
// Order: Journal > Checklist > Home > Prayers > Meditate
val bottomNavItems = listOf(
    Screen.JournalList,
    Screen.Checklist,
    Screen.Home,
    Screen.Prayers,
    Screen.Meditate
)