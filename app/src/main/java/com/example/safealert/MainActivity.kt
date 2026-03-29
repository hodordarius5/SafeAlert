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

class MainActivity : ComponentActivity() {

    private var pendingSmsAction: (() -> Unit)? = null
    private var pendingLocationAction: (() -> Unit)? = null

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

        setContent {
            SafeAlertTheme {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    onRequestSmsPermission = requestSmsPermissionIfNeeded,
                    onRequestLocationPermission = requestLocationPermissionIfNeeded
                )
            }
        }
    }
}