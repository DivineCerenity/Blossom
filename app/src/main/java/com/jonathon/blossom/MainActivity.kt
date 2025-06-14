package com.jonathon.blossom

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel // IMPORTANT import
import com.jonathon.blossom.ui.dashboard.DashboardViewModel
import com.jonathon.blossom.ui.settings.SettingsViewModel
import com.jonathon.blossom.ui.theme.BlossomTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                val requestPermissionLauncher =
                    registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                        // You can show a message if needed
                    }
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // 🎨 IMMERSIVE EXPERIENCE SETUP 🎨
        // Enable edge-to-edge for modern, immersive experience
        enableEdgeToEdge()

        // Force portrait orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContent {
            // Get the current theme from settings
            val settingsViewModel: SettingsViewModel = viewModel()
            val settingsUiState by settingsViewModel.uiState.collectAsState()

            // Force recomposition when theme changes using the refresh key
            key(settingsUiState.themeRefreshKey) {
                BlossomTheme(
                    darkTheme = settingsUiState.isDarkMode,
                    selectedTheme = settingsUiState.selectedTheme
                ) {
                    BlossomApp()
                }
            }
        }
    }
}