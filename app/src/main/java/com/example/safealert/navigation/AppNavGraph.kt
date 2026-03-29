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
import android.util.Log
import android.annotation.SuppressLint
import android.telephony.SmsManager
import com.google.android.gms.location.LocationServices

//lista ecranelor si a rutelor
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
}

//descrierea navigarii propriu-zisa
@Composable
fun AppNavGraph(
    navController: NavHostController,
    onRequestSmsPermission: (action: () -> Unit) -> Unit,
    onRequestLocationPermission: (action: () -> Unit) -> Unit
) {

    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

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
                        val contacts = listOf(c1, c2).filter { it.isNotEmpty() }

                        onRequestLocationPermission {
                            try {
                                @SuppressLint("MissingPermission")
                                fusedLocationClient.lastLocation
                                    .addOnSuccessListener { location ->
                                        val finalMessage = if (location != null) {
                                            val latitude = location.latitude
                                            val longitude = location.longitude
                                            val mapsLink = "https://maps.google.com/?q=$latitude,$longitude"

                                            "🚨 SOS ALERT 🚨\n$msg\n\nLocație:\n$mapsLink"
                                        } else {
                                            "🚨 SOS ALERT 🚨\n$msg\n\nLocația nu a putut fi accesată."
                                        }

                                        onRequestSmsPermission {
                                            val smsManager = SmsManager.getDefault()
                                            //lungimea mesajului nu trimitea mesajul, il dividem
                                            contacts.forEach { phone ->
                                                val parts = smsManager.divideMessage(finalMessage)

                                                if (parts.size > 1) {
                                                    smsManager.sendMultipartTextMessage(
                                                        phone,
                                                        null,
                                                        parts,
                                                        null,
                                                        null
                                                    )
                                                } else {
                                                    smsManager.sendTextMessage(
                                                        phone,
                                                        null,
                                                        finalMessage,
                                                        null,
                                                        null
                                                    )
                                                }
                                            }

                                            Toast.makeText(
                                                context,
                                                "SOS cu locație trimis",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            Log.d("SOS", "Mesaj trimis: $finalMessage")
                                        }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "Locația nu a putut fi luată",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Eroare locație: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
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