package com.jonathon.blossom.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.compose.material3.Shapes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = TwilightMauve,
    onPrimaryContainer = Color(0xFFE5E5E5),
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = TwilightPurple,
    onSecondaryContainer = Color(0xFFE5E5E5),
    tertiary = TwilightRose,
    onTertiary = OnPrimary,
    tertiaryContainer = TwilightBeige,
    onTertiaryContainer = Color(0xFFE5E5E5),
    background = Color(0xFF1A1A1A),  // Dark background
    onBackground = Color(0xFFE5E5E5),  // Light text on dark
    surface = Color(0xFF1A1A1A),  // Same as background for navbar
    onSurface = Color(0xFFE5E5E5),  // Light text on dark surface
    surfaceVariant = Color(0xFF2A2A2A),  // Darker surface variant
    onSurfaceVariant = Color(0xFFB0B0B0),  // Medium light text
    error = Error
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = TwilightBeige,
    onPrimaryContainer = OnBackground,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = TwilightBeige,
    onSecondaryContainer = OnBackground,
    tertiary = TwilightPurple,
    onTertiary = OnPrimary,
    tertiaryContainer = TwilightCream,
    onTertiaryContainer = OnBackground,
    background = Background,
    onBackground = OnBackground,
    surface = Background,  // Make surface same as background for navbar
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    error = Error
)

private val BlossomShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun BlossomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    selectedTheme: com.jonathon.blossom.ui.settings.AppTheme = com.jonathon.blossom.ui.settings.AppTheme.TWILIGHT_MYSTIQUE,
    // Disable dynamic color to ensure consistent mindful theme
    dynamicColor: Boolean = false,
    // Immersive mode options
    hideStatusBar: Boolean = false,
    content: @Composable () -> Unit
) {
    // Use theme provider to get the appropriate color scheme
    val colorScheme = if (darkTheme) {
        ThemeProvider.getDarkColorScheme(selectedTheme)
    } else {
        ThemeProvider.getLightColorScheme(selectedTheme)
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)

            // 🎨 IMMERSIVE EXPERIENCE SETUP 🎨

            if (hideStatusBar) {
                // Option 1: Hide status bar completely for fullscreen experience
                insetsController.hide(WindowInsetsCompat.Type.statusBars())
            } else {
                // Option 2: Keep status bar but make it blend seamlessly
                window.statusBarColor = colorScheme.background.toArgb()
                insetsController.isAppearanceLightStatusBars = !darkTheme
            }

            // 🌈 BEAUTIFUL NAVIGATION BAR STYLING 🌈
            // Make navigation bar match your beautiful theme!
            window.navigationBarColor = colorScheme.background.toArgb()
            insetsController.isAppearanceLightNavigationBars = !darkTheme

            // Optional: Make navigation bar semi-transparent for modern look
            // window.navigationBarColor = Color.TRANSPARENT.toArgb()
            // window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    // The MaterialTheme composable is what passes down all the essential
    // things, including the LifecycleOwner. Our content is placed *inside* it.
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = BlossomShapes,
        content = content // <-- THIS LINE ENSURES THE "VIP PASS" IS HANDED DOWN
    )
}