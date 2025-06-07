package com.example.blossom

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * This is the single source of truth for all navigation routes in the app.
 */
sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object JournalList : Screen("journal", "Journal", Icons.Default.Book)
    object Meditate : Screen("meditate", "Meditate", Icons.Default.SelfImprovement)
    object Checklist : Screen("checklist", "Checklist", Icons.Default.CheckBox)

    // This screen is not on the bottom bar, so its label and icon can be empty/placeholders.
    object AddEditJournal : Screen("addEditJournal", "", Icons.Default.Home)
}

// This list defines the items that appear in the bottom navigation bar.
val bottomNavItems = listOf(
    Screen.Home,
    Screen.JournalList,
    Screen.Meditate,
    Screen.Checklist
)