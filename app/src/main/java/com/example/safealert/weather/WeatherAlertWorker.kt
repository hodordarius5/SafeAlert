package com.example.safealert.weather

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.SmsManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.safealert.data.PreferencesManager
import okhttp3.OkHttpClient
import okhttp3.Request


class WeatherAlertWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val prefs = PreferencesManager(appContext)
    private val client = OkHttpClient()

    override suspend fun doWork(): Result {
        if (!prefs.isWeatherAlertsEnabled()) return Result.success()

        val request = Request.Builder()
            .url("https://feeds.meteoalarm.org/feeds/meteoalarm-legacy-atom-romania")
            .build()

        return try {
            android.util.Log.d("WEATHER_WORKER", "doWork started")
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                return Result.retry()
            }

            val xml = response.body?.string().orEmpty()

            //val hasSevereAlert = true

            val hasSevereAlert =
                xml.contains("red", ignoreCase = true) ||
                        xml.contains("orange", ignoreCase = true)
           // android.util.Log.d("WEATHER_WORKER", "Alertă vreme severă, se pregateste mesaj")

            if (hasSevereAlert && prefs.isWeatherSmsEnabled()) {
                sendWeatherSms(
                    "⚠️ ALERTĂ METEO SEVERĂ ⚠️\nA fost detectată o alertă meteo severă în România. Verifică situația mea."
                )
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendWeatherSms(message: String) {
        val c1 = prefs.getContact1().trim()
        val c2 = prefs.getContact2().trim()
        val contacts = listOf(c1, c2).filter { it.isNotEmpty() }

        if (contacts.isEmpty()) return

        val smsManager = SmsManager.getDefault()

        contacts.forEach { phone ->
            val parts = smsManager.divideMessage(message)
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
                    message,
                    null,
                    null
                )
            }
        }
    }
}