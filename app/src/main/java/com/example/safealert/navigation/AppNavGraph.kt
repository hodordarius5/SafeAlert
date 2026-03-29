//harta de ecrane
package com.example.safealert.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.safealert.ui.screens.HomeScreen
import com.example.safealert.ui.screens.SettingsScreen

//lista ecranelor si a rutelor
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
}

//descrierea navigarii propriu-zisa
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route //ecranul de deschidere al aplicatiei
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onSosClick = {
                    // aici punem mai târziu logica SOS
                },
                onVoiceClick = {
                    // aici punem mai târziu logica pentru voce
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}