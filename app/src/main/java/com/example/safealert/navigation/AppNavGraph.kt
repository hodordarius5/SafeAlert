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
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.DisposableEffect
import kotlin.math.sqrt
import android.net.Uri
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import kotlinx.coroutines.delay
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.BatteryManager
import android.provider.Settings

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
    onRequestLocationPermission: (action: () -> Unit) -> Unit,
    onRequestAudioPermission:(action: () -> Unit) -> Unit,
    onRequestCallPermission:(action: () -> Unit) -> Unit
) {

    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val accelerometer = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    var showCallCountdownDialog by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(10) }

    var lastUserInteractionTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var lastMovementTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var inactivityAlertSent by remember { mutableStateOf(false) }

    var lowBatteryAlertSent by remember { mutableStateOf(false) }
    var finalBatteryAlertSent by remember { mutableStateOf(false) }

    fun triggerSos() {
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
                    @android.annotation.SuppressLint("MissingPermission")
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
                                val smsManager = android.telephony.SmsManager.getDefault()

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

                                android.util.Log.d("SOS", "Mesaj trimis: $finalMessage")
                                showCallCountdownDialog = true //porneste countdown
                                countdown = 10
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
    }

    fun triggerInactivitySos(minutes: Int) {
        val c1 = prefs.getContact1().trim()
        val c2 = prefs.getContact2().trim()

        if (c1.isEmpty() && c2.isEmpty()) {
            Toast.makeText(
                context,
                "Nu există contacte pentru alerta de inactivitate.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val customMessage = "Nu am mai fost activ de $minutes minute. Verifică dacă sunt în siguranță."

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
                            "⚠️ ALERTĂ DE INACTIVITATE ⚠️\n$customMessage\n\nLocație:\n$mapsLink"
                        } else {
                            "⚠️ ALERTĂ DE INACTIVITATE ⚠️\n$customMessage\n\nLocația nu a putut fi accesată."
                        }

                        onRequestSmsPermission {
                            val smsManager = SmsManager.getDefault()

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
                                "Alerta de inactivitate a fost trimisă.",
                                Toast.LENGTH_SHORT
                            ).show()

                            Log.d("INACTIVITY", "Mesaj trimis: $finalMessage")
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Locația nu a putut fi luată pentru alerta de inactivitate.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Eroare la alerta de inactivitate: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun triggerBatteryAlert(title: String, body: String, openBatterySaver: Boolean = false) {
        val c1 = prefs.getContact1().trim()
        val c2 = prefs.getContact2().trim()

        if (c1.isEmpty() && c2.isEmpty()) {
            Toast.makeText(
                context,
                "Nu există contacte pentru alerta de baterie.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

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
                            "$title\n$body\n\nLocație:\n$mapsLink"
                        } else {
                            "$title\n$body\n\nLocația nu a putut fi accesată."
                        }

                        onRequestSmsPermission {
                            val smsManager = SmsManager.getDefault()

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
                                "Alerta de baterie a fost trimisă.",
                                Toast.LENGTH_SHORT
                            ).show()

                            Log.d("BATTERY_ALERT", "Mesaj trimis: $finalMessage")

                            if (openBatterySaver) {
                                try {
                                    val intent = Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Nu am putut deschide setările pentru economisirea bateriei.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Locația nu a putut fi luată pentru alerta de baterie.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Eroare alertă baterie: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    val shakeListener = remember {
        object : SensorEventListener {
            private var lastShakeTime = 0L

            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val gForce = sqrt(x * x + y * y + z * z) / SensorManager.GRAVITY_EARTH

                val currentTime = System.currentTimeMillis()

                if (gForce > 1.2f) {
                    lastMovementTime = currentTime
                    inactivityAlertSent = false
                }

                if (gForce > 2.7f && currentTime - lastShakeTime > 1500) {
                    lastShakeTime = currentTime
                    Toast.makeText(context, "Shake detectat", Toast.LENGTH_SHORT).show()
                    triggerSos()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    val batteryReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(contextReceiver: Context?, intent: Intent?) {
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

                if (level < 0 || scale <= 0) return

                val batteryPct = (level * 100) / scale

                if (!prefs.isLowBatteryEnabled()) return

                if (batteryPct <= 10 && !lowBatteryAlertSent) {
                    lowBatteryAlertSent = true

                    triggerBatteryAlert(
                        title = "⚠️ ALERTĂ BATERIE SCĂZUTĂ ⚠️",
                        body = "Telefonul meu a ajuns la $batteryPct% baterie. Activez economisirea de energie și vă trimit locația mea curentă.",
                        openBatterySaver = true
                    )
                }

                if (batteryPct <= 5 && !finalBatteryAlertSent) {
                    finalBatteryAlertSent = true

                    triggerBatteryAlert(
                        title = "🔴 Alertă locație finală 🔴",
                        body = "Telefonul meu este pe cale să se închidă. Aceasta este ultima mea locație cunoscută.",
                        openBatterySaver = false
                    )
                }

                if (batteryPct > 10) {
                    lowBatteryAlertSent = false
                    finalBatteryAlertSent = false
                }
            }
        }
    }

    DisposableEffect(accelerometer) {
        if (accelerometer != null) {
            sensorManager.registerListener(
                shakeListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        onDispose {
            sensorManager.unregisterListener(shakeListener)
        }
    }

    DisposableEffect(Unit) {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(batteryReceiver, filter)

        onDispose {
            try {
                context.unregisterReceiver(batteryReceiver)
            } catch (_: Exception) {
            }
        }
    }

    LaunchedEffect(showCallCountdownDialog, countdown) {
        if (showCallCountdownDialog && countdown > 0) {
            delay(1000)
            countdown--
        } else if (showCallCountdownDialog && countdown == 0) {
            showCallCountdownDialog = false

            val phone = prefs.getContact1().trim()

            if (phone.isNotEmpty()) {
                onRequestCallPermission {
                    val intent = Intent(Intent.ACTION_CALL).apply {
                        data = Uri.parse("tel:$phone")
                    }
                    context.startActivity(intent)
                }
            } else {
                Toast.makeText(
                    context,
                    "Nu există contact principal pentru apel.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000)

            if (!prefs.isInactivityEnabled()) continue

            val now = System.currentTimeMillis()
            val timeoutMinutes = prefs.getInactivityMinutes()
            val timeoutMillis = timeoutMinutes * 60 * 1000L

            val inactiveByTouch = now - lastUserInteractionTime >= timeoutMillis
            val inactiveByMovement = now - lastMovementTime >= timeoutMillis

            if (inactiveByTouch && inactiveByMovement && !inactivityAlertSent) {
                inactivityAlertSent = true
                triggerInactivitySos(timeoutMinutes)
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route //ecranul de deschidere al aplicatiei
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onSettingsClick = {
                    lastUserInteractionTime = System.currentTimeMillis()
                    inactivityAlertSent = false
                    navController.navigate(Screen.Settings.route)
                },
                onSosClick = {
                    lastUserInteractionTime = System.currentTimeMillis()
                    inactivityAlertSent = false
                    triggerSos()
                },
                onVoiceClick = {
                    lastUserInteractionTime = System.currentTimeMillis()
                    inactivityAlertSent = false
                    onRequestAudioPermission {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(
                                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                            )
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Spune SOS, help sau ajutor")
                        }

                        speechRecognizer.setRecognitionListener(object : RecognitionListener {
                            override fun onReadyForSpeech(params: Bundle?) {
                                Toast.makeText(context, "Te ascult...", Toast.LENGTH_SHORT).show()
                            }

                            override fun onBeginningOfSpeech() {}

                            override fun onRmsChanged(rmsdB: Float) {}

                            override fun onBufferReceived(buffer: ByteArray?) {}

                            override fun onEndOfSpeech() {}

                            override fun onError(error: Int) {
                                Toast.makeText(context, "Nu am înțeles. Încearcă din nou.", Toast.LENGTH_SHORT).show()
                            }

                            override fun onResults(results: Bundle?) {
                                val matches = results
                                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                                    ?.map { it.lowercase() }
                                    ?: emptyList()

                                val hasTriggerWord = matches.any {
                                    it.contains("sos") || it.contains("help") || it.contains("ajutor")
                                }

                                if (hasTriggerWord) {
                                    Toast.makeText(context, "Comandă vocală detectată", Toast.LENGTH_SHORT).show()
                                    triggerSos()
                                } else {
                                    Toast.makeText(context, "Nu am detectat o comandă SOS", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onPartialResults(partialResults: Bundle?) {}

                            override fun onEvent(eventType: Int, params: Bundle?) {}
                        })

                        speechRecognizer.startListening(intent)
                    }
                }
            )
        }


        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = {
                    lastUserInteractionTime = System.currentTimeMillis()
                    inactivityAlertSent = false
                    navController.popBackStack()
                }
            )
        }
    }
    if (showCallCountdownDialog) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(
                    onClick = {
                        showCallCountdownDialog = false
                    }
                ) {
                    Text("Anulează")
                }
            },
            title = {
                Text("Alertă de urgență")
            },
            text = {
                Text("Se va apela automat contactul principal în $countdown secunde.")
            }
        )
    }
}