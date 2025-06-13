package com.jonathon.blossom

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * This is the single source of truth for all navigation routes in the app.
 */
sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object JournalList : Screen("journal", "Journal", Icons.Default.Book)
    object Habits : Screen("habits", "Habits", Icons.Default.TrackChanges)
    object Insights : Screen("insights", "Insights", Icons.Default.Analytics)  // 📊 BEAUTIFUL NEW INSIGHTS TAB!
    object Prayers : Screen("prayers", "Prayers", Icons.Default.FavoriteBorder)
    object Meditate : Screen("meditate", "Meditate", Icons.Default.SelfImprovement)

    // Screens not on the bottom bar
    object Home : Screen("home", "Home", Icons.Default.Home)  // 🏠 MOVED TO NON-NAV SCREENS

    // Screens not on the bottom bar
    object AddEditJournal : Screen("addEditJournal", "", Icons.Default.Home)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

// This list defines the items that appear in the bottom navigation bar.
// Order: Journal > Habits > Insights > Prayers > Meditate (PERFECT 5-TAB LAYOUT!)
val bottomNavItems = listOf(
    Screen.JournalList,
    Screen.Habits,
    Screen.Insights,  // 📊 BEAUTIFUL NEW INSIGHTS TAB!
    Screen.Prayers,   // 🙏 NOW WITH DAILY VERSE!
    Screen.Meditate
)