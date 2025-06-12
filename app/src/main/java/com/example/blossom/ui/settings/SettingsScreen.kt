package com.example.blossom.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onThemeChanged: (AppTheme) -> Unit = {},
    selectedTheme: AppTheme = AppTheme.TWILIGHT_MYSTIQUE,
    isDarkMode: Boolean = false,
    onDarkModeChanged: (Boolean) -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
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
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dark Mode Toggle Section
            item {
                DarkModeSection(
                    isDarkMode = uiState.isDarkMode,
                    onToggleDarkMode = viewModel::toggleDarkMode
                )
            }

            // Theme Selection Section
            item {
                ThemeSelectionSection(
                    selectedTheme = uiState.selectedTheme,
                    onThemeSelected = viewModel::selectTheme
                )
            }

            // App Info Section
            item {
                AppInfoSection(onNavigateToAbout = onNavigateToAbout)
            }
        }
    }
}

@Composable
fun DarkModeSection(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Dark Mode",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isDarkMode) "Dark theme enabled" else "Light theme enabled",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Switch(
                checked = isDarkMode,
                onCheckedChange = { onToggleDarkMode() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.primary,
                    checkedTrackColor = if (isDarkMode) Color.White.copy(alpha = 0.3f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

@Composable
fun ThemeSelectionSection(
    selectedTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    Icons.Default.Palette,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "App Theme",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            AppTheme.values().forEach { theme ->
                ThemeOption(
                    theme = theme,
                    isSelected = theme == selectedTheme,
                    onSelected = { onThemeSelected(theme) }
                )
            }
        }
    }
}

@Composable
fun ThemeOption(
    theme: AppTheme,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .selectable(
                selected = isSelected,
                onClick = onSelected
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelected
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = theme.displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = theme.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun AppInfoSection(
    onNavigateToAbout: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "About Blossom",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "A mindful companion for prayer, journaling, and spiritual growth.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Inspired By: Jonathon",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🌸 BEAUTIFUL CONTRAST-AWARE ABOUT BUTTON
            Card(
                onClick = onNavigateToAbout,
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "About Blossom",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🌸",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
