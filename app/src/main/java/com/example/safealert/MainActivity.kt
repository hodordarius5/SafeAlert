package com.example.safealert

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.safealert.navigation.AppNavGraph
import com.example.safealert.ui.theme.SafeAlertTheme
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.safealert.weather.WeatherAlertWorker
import java.util.concurrent.TimeUnit
import androidx.work.OneTimeWorkRequestBuilder

class MainActivity : ComponentActivity() {

    private var pendingSmsAction: (() -> Unit)? = null
    private var pendingLocationAction: (() -> Unit)? = null
    private var pendingAudioAction: (() -> Unit)? = null

    private var pendingCallAction: (() -> Unit)? = null

    private val requestCallPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                pendingCallAction?.invoke()
            } else {
                Toast.makeText(this, "Permisiune apel respinsa", Toast.LENGTH_SHORT).show()
            }
        }

    private val requestSmsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                pendingSmsAction?.invoke()
            } else {
                Toast.makeText(this, "Permisiune SMS inactivă", Toast.LENGTH_SHORT).show()
            }
        }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                pendingLocationAction?.invoke()
            } else {
                Toast.makeText(this, "Permisiune locație inactivă", Toast.LENGTH_SHORT).show()
            }
        }

    private val requestAudioPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                pendingAudioAction?.invoke()
            } else {
                Toast.makeText(this, "Permisiune audio respinsă", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val requestSmsPermissionIfNeeded: ((() -> Unit) -> Unit) = { action ->
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.SEND_SMS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    action()
                }

                else -> {
                    pendingSmsAction = action
                    requestSmsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
                }
            }
        }

        val requestLocationPermissionIfNeeded: ((() -> Unit) -> Unit) = { action ->
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                action()
            } else {
                pendingLocationAction = action
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        val requestAudioPermissionIfNeeded: ((() -> Unit) -> Unit) = { action ->
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                action()
            } else {
                pendingAudioAction = action
                requestAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }

        val requestCallPermissionIfNeeded: ((() -> Unit) -> Unit) = { action ->
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                action()
            } else {
                pendingCallAction = action
                requestCallPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
        }

        val weatherWorkRequest =
            PeriodicWorkRequestBuilder<WeatherAlertWorker>(15, TimeUnit.MINUTES)
                .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
             "weather_alert_monitor",
              ExistingPeriodicWorkPolicy.UPDATE,
              weatherWorkRequest
        )

//        val testWeatherWorkRequest =
//            OneTimeWorkRequestBuilder<WeatherAlertWorker>()
//                .build()
//
//        WorkManager.getInstance(this).enqueue(testWeatherWorkRequest)

                setContent {
            SafeAlertTheme {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    onRequestSmsPermission = requestSmsPermissionIfNeeded,
                    onRequestLocationPermission = requestLocationPermissionIfNeeded,
                    onRequestAudioPermission = requestAudioPermissionIfNeeded,
                    onRequestCallPermission = requestCallPermissionIfNeeded
                )
            }
        }
    }
}