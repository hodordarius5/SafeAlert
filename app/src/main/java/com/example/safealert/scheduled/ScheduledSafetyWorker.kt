package com.example.safealert.scheduled

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.SmsManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.safealert.data.PreferencesManager

class ScheduledSafetyWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val prefs = PreferencesManager(appContext)

    override suspend fun doWork(): Result {
        if (!prefs.isScheduledMessageEnabled()) return Result.success()

        val c1 = prefs.getContact1().trim()
        val c2 = prefs.getContact2().trim()
        val contacts = listOf(c1, c2).filter { it.isNotEmpty() }

        if (contacts.isEmpty()) return Result.success()

        val message = prefs.getScheduledMessageText().trim()
        if (message.isEmpty()) return Result.success()

        sendScheduledSms("⚠️ ALERTĂ CHECK-IN ⚠️\n$message")

        return Result.success()
    }

    @SuppressLint("MissingPermission")
    private fun sendScheduledSms(message: String) {
        val c1 = prefs.getContact1().trim()
        val c2 = prefs.getContact2().trim()
        val contacts = listOf(c1, c2).filter { it.isNotEmpty() }

        val smsManager = SmsManager.getDefault()

        contacts.forEach { phone ->
            val parts = smsManager.divideMessage(message)
            if (parts.size > 1) {
                smsManager.sendMultipartTextMessage(phone, null, parts, null, null)
            } else {
                smsManager.sendTextMessage(phone, null, message, null, null)
            }
        }
    }
}