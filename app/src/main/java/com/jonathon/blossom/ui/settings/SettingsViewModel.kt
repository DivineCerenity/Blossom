package com.jonathon.blossom.ui.settings

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.jonathon.blossom.data.AnalyticsRepository
import com.jonathon.blossom.data.Achievement
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
// import com.google.android.gms.drive.Drive // 🔧 COMMENTED OUT - Drive API not yet added
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val analyticsRepository: AnalyticsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    // 🏆 ACHIEVEMENT STATE
    private val _newAchievements = MutableStateFlow<List<Achievement>>(emptyList())
    val newAchievements: StateFlow<List<Achievement>> = _newAchievements.asStateFlow()

    private var googleSignInClient: GoogleSignInClient? = null

    init {
        loadSettings()
        setupGoogleSignInClient()
        checkGoogleSignInStatus()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.getSelectedTheme().collect { theme ->
                _uiState.value = _uiState.value.copy(selectedTheme = theme)
            }
        }
        viewModelScope.launch {
            settingsRepository.getDarkMode().collect { isDarkMode ->
                _uiState.value = _uiState.value.copy(isDarkMode = isDarkMode)
            }
        }
    }

    private fun setupGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            // .requestScopes(Drive.SCOPE_FILE, Drive.SCOPE_APPFOLDER) // 🔧 COMMENTED OUT - Drive API not yet added
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    private fun checkGoogleSignInStatus() {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        _uiState.value = _uiState.value.copy(
            isGoogleSignedIn = account != null,
            googleUserEmail = account?.email
        )
    }

    fun signInWithGoogle(launcher: ActivityResultLauncher<Intent>) {
        val signInIntent = googleSignInClient?.signInIntent
        if (signInIntent != null) {
            launcher.launch(signInIntent)
        }
    }

    fun handleGoogleSignInResult(data: Intent?) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            _uiState.value = _uiState.value.copy(
                isGoogleSignedIn = true,
                googleUserEmail = account?.email
            )
        } catch (e: ApiException) {
            Log.w("SettingsViewModel", "Google sign in failed", e)
            _uiState.value = _uiState.value.copy(
                isGoogleSignedIn = false,
                googleUserEmail = null
            )
        }
    }

    fun signOutGoogle() {
        googleSignInClient?.signOut()?.addOnCompleteListener {
            _uiState.value = _uiState.value.copy(
                isGoogleSignedIn = false,
                googleUserEmail = null
            )
        }
    }

    fun selectTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsRepository.saveSelectedTheme(theme)
            _uiState.value = _uiState.value.copy(selectedTheme = theme)

            // 🏆 RECORD THEME CHANGE AND CHECK FOR ACHIEVEMENTS!
            val achievements = analyticsRepository.recordThemeChange(theme.displayName)
            if (achievements.isNotEmpty()) {
                _newAchievements.value = achievements
            }
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            val newDarkMode = !_uiState.value.isDarkMode
            settingsRepository.saveDarkMode(newDarkMode)
            _uiState.value = _uiState.value.copy(isDarkMode = newDarkMode)
        }
    }

    /**
     * 🏆 CLEAR ACHIEVEMENTS
     * Call this after showing achievement celebrations
     */
    fun clearAchievements() {
        _newAchievements.value = emptyList()
    }
}
