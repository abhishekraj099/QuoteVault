package com.example.quotevault.core.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.quotevault.presentation.auth.forgot_password.ForgotPasswordScreen
import com.example.quotevault.presentation.auth.login.LoginScreen
import com.example.quotevault.presentation.auth.login.LoginViewModel
import com.example.quotevault.presentation.auth.signup.SignUpScreen
import com.example.quotevault.presentation.collections.detail.CollectionDetailScreen
import com.example.quotevault.presentation.collections.list.CollectionsScreen
import com.example.quotevault.presentation.favorites.FavoritesScreen
import com.example.quotevault.presentation.home.HomeScreen
import com.example.quotevault.presentation.profile.ProfileScreen
import com.example.quotevault.presentation.search.SearchScreen
import com.example.quotevault.presentation.settings.SettingsRoute

private const val TAG = "QV_NavGraph"

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Auth screens
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(navController = navController)
        }

        composable(Screen.ResetPassword.route) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            ForgotPasswordScreen(
                onBack = {
                    navController.navigateUp()
                },
                onResetPassword = { email ->
                    // This lambda is already suspend, so we can call suspend functions directly
                    loginViewModel.resetPassword(email)
                }
            )
        }

        // Main screens
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.Search.route) {
            SearchScreen(navController = navController)
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(navController = navController)
        }

        composable(Screen.Collections.route) {
            CollectionsScreen(navController = navController)
        }

        composable(
            route = Screen.CollectionDetail.route,
            arguments = listOf(
                navArgument("collectionId") { type = NavType.StringType }
            )
        ) {
            CollectionDetailScreen(navController = navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

        // Settings
        composable(Screen.Settings.route) {
            Log.d(TAG, "Settings composable entered - about to render SettingsRoute")
            LaunchedEffect(Unit) {
                Log.d(TAG, "Settings LaunchedEffect - SettingsRoute is being composed")
            }
            SettingsRoute(
                onNavigateBack = {
                    Log.d(TAG, "Settings onNavigateBack called")
                    navController.navigateUp()
                }
            )
        }
    }
}
