package com.example.quotevault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.quotevault.data.local.datastore.UserPreferencesManager
import com.example.quotevault.data.remote.api.AuthService
import com.example.quotevault.presentation.MainScreen
import com.example.quotevault.presentation.splash.SplashScreen
import com.example.quotevault.core.navigation.Screen
import com.example.quotevault.core.theme.QuoteVaultTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authService: AuthService

    @Inject
    lateinit var prefs: UserPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Observe persisted dark mode and theme preferences
            val isDarkMode by prefs.isDarkModeFlow.collectAsState(initial = false)
            val themeName by prefs.themeFlow.collectAsState(initial = "Default")
            val fontSize by prefs.fontSizeFlow.collectAsState(initial = 100)

            // Splash screen state
            var showSplash by remember { mutableStateOf(true) }
            var startDestination by remember { mutableStateOf<String?>(null) }

            // Check auth state when splash completes
            LaunchedEffect(Unit) {
                startDestination = if (authService.isUserLoggedIn()) {
                    Screen.Home.route
                } else {
                    Screen.Login.route
                }
            }

            // Convert theme name to AppTheme object
            val appTheme = com.example.quotevault.core.theme.AppTheme.fromName(themeName)

            QuoteVaultTheme(
                darkTheme = isDarkMode,
                appTheme = appTheme,
                fontSizePercent = fontSize
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when {
                        showSplash -> {
                            // Show animated splash screen
                            SplashScreen(
                                onSplashComplete = {
                                    showSplash = false
                                }
                            )
                        }
                        startDestination != null -> {
                            // Show main app content after splash
                            MainScreen(startDestination = startDestination!!)
                        }
                    }
                }
            }
        }
    }
}
