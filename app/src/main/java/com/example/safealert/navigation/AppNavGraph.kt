//harta de ecrane
package com.example.safealert.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.safealert.ui.screens.HomeScreen
import com.example.safealert.ui.screens.SettingsScreen
import android.widget.Toast
import com.example.safealert.data.PreferencesManager
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

//lista ecranelor si a rutelor
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
}

//descrierea navigarii propriu-zisa
@Composable
fun AppNavGraph(navController: NavHostController) {

    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }

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
                    val c1 = prefs.getContact1().trim()
                    val c2 = prefs.getContact2().trim()
                    val msg = prefs.getMessage().trim()

                    if (c1.isEmpty() && c2.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Adaugă măcar un contact în setări.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (msg.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Adaugă un mesaj de urgență în setări.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Datele SOS sunt gata. Următorul pas: trimitere alertă.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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