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
import com.google.android.gms.common.api.Scope
import com.jonathon.blossom.data.AnalyticsRepository
import com.jonathon.blossom.data.Achievement
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import com.google.android.gms.drive.Drive // 🔧 Uncommented to enable Drive API
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val analyticsRepository: AnalyticsRepository,
    @ApplicationContext private val context: Context,
    private val backupManager: BackupManager
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
        Log.i("SettingsViewModel", "Setting up Google Sign-In client...")

        // Full configuration using web client ID for ID token
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken("360747118344-9p9v9qsst2ng0qta52egmgckejv9b3mv.apps.googleusercontent.com")
            .requestServerAuthCode("360747118344-9p9v9qsst2ng0qta52egmgckejv9b3mv.apps.googleusercontent.com")
            .requestScopes(com.google.android.gms.common.api.Scope("https://www.googleapis.com/auth/drive.file"))
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
        Log.i("SettingsViewModel", "Google Sign-In client created: $googleSignInClient")
    }

    private fun checkGoogleSignInStatus() {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        Log.i("SettingsViewModel", "Checking sign-in status: account=${account?.email}, isSignedIn=${account != null}")
        _uiState.value = _uiState.value.copy(
            isGoogleSignedIn = account != null,
            googleUserEmail = account?.email
        )
    }

    fun signInWithGoogle(launcher: ActivityResultLauncher<Intent>) {
        Log.i("SettingsViewModel", "=== GOOGLE SIGN-IN DEBUG START ===")
        Log.i("SettingsViewModel", "signInWithGoogle called")
        Log.i("SettingsViewModel", "googleSignInClient: $googleSignInClient")

        val signInIntent = googleSignInClient?.signInIntent
        Log.i("SettingsViewModel", "signInIntent: $signInIntent")

        if (signInIntent != null) {
            Log.i("SettingsViewModel", "Launching Google sign-in intent")
            Log.i("SettingsViewModel", "Intent extras: ${signInIntent.extras}")
            launcher.launch(signInIntent)
        } else {
            Log.e("SettingsViewModel", "Google sign-in intent is null - client setup failed!")
        }
        Log.i("SettingsViewModel", "=== GOOGLE SIGN-IN DEBUG END ===")
    }

    fun handleGoogleSignInResult(data: Intent?) {
        Log.i("SettingsViewModel", "handleGoogleSignInResult called with data: $data")
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            Log.i("SettingsViewModel", "Google sign in successful: ${account?.email}")
            Log.i("SettingsViewModel", "Account scopes: ${account?.grantedScopes}")
            Log.i("SettingsViewModel", "Account ID token: ${account?.idToken != null}")
            Log.i("SettingsViewModel", "Account server auth code: ${account?.serverAuthCode != null}")
            _uiState.value = _uiState.value.copy(
                isGoogleSignedIn = true,
                googleUserEmail = account?.email
            )
        } catch (e: ApiException) {
            Log.e("SettingsViewModel", "Google sign in failed with ApiException:")
            Log.e("SettingsViewModel", "  Status code: ${e.statusCode}")
            Log.e("SettingsViewModel", "  Status message: ${e.status?.statusMessage}")
            Log.e("SettingsViewModel", "  Exception message: ${e.message}")
            Log.e("SettingsViewModel", "  Common status codes:")
            Log.e("SettingsViewModel", "    7 = NETWORK_ERROR")
            Log.e("SettingsViewModel", "    8 = INTERNAL_ERROR")
            Log.e("SettingsViewModel", "    10 = DEVELOPER_ERROR")
            Log.e("SettingsViewModel", "    12500 = SIGN_IN_CANCELLED")
            Log.e("SettingsViewModel", "    12501 = SIGN_IN_CURRENTLY_IN_PROGRESS")
            Log.e("SettingsViewModel", "    12502 = SIGN_IN_FAILED")
            _uiState.value = _uiState.value.copy(
                isGoogleSignedIn = false,
                googleUserEmail = null
            )
        } catch (e: Exception) {
            Log.e("SettingsViewModel", "Unexpected error during Google sign in", e)
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
                googleUserEmail = null,
                backupStatus = "",
                restoreStatus = ""
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

    /**
     * Trigger backup to Google Drive
     */
    fun triggerBackup() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(backupStatus = "Backing up...")
            val result = backupManager.performBackup()
            _uiState.value = _uiState.value.copy(
                backupStatus = result.getOrNull() ?: "Backup failed: ${result.exceptionOrNull()?.message}"
            )
        }
    }

    /**
     * Trigger restore from Google Drive
     */
    fun triggerRestore() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(restoreStatus = "Restoring...")
            val result = backupManager.performRestore()
            _uiState.value = _uiState.value.copy(
                restoreStatus = result.getOrNull() ?: "Restore failed: ${result.exceptionOrNull()?.message}"
            )
        }
    }
    
    /**
     * Update the habit reset time
     */
    fun updateHabitResetTime(hour: Int) {
        viewModelScope.launch {
            settingsRepository.saveHabitResetTime(hour)
            _uiState.value = _uiState.value.copy(habitResetTime = hour)
        }
    }
}
