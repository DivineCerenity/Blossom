package com.example.blossom

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel // IMPORTANT import
import com.example.blossom.ui.dashboard.DashboardViewModel
import com.example.blossom.ui.settings.SettingsViewModel
import com.example.blossom.ui.theme.BlossomTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🎨 IMMERSIVE EXPERIENCE SETUP 🎨
        // Enable edge-to-edge for modern, immersive experience
        enableEdgeToEdge()

        // Force portrait orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContent {
            // Get the current theme from settings
            val settingsViewModel: SettingsViewModel = viewModel()
            val settingsUiState by settingsViewModel.uiState.collectAsState()

            BlossomTheme(
                darkTheme = settingsUiState.isDarkMode,
                selectedTheme = settingsUiState.selectedTheme
            ) {
                BlossomApp()
            }
        }
    }
}