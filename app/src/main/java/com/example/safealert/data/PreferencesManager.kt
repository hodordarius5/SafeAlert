package com.example.safealert.data

import android.content.Context

class PreferencesManager(context: Context){

    private val prefs = context.getSharedPreferences("safealert_prefs", Context.MODE_PRIVATE)
    private val KEY_INACTIVITY_ENABLED = "inactivity_enabled" //pt feature inactivitate
    private val KEY_INACTIVITY_MINUTES = "inactivity_minutes"

    private val KEY_LOW_BATTERY_ENABLED = "low_battery_enabled" //pt feature baterie

    fun saveContacts(contact1: String, contact2: String, message: String){
        prefs.edit().apply{
            putString("contact1", contact1)
            putString("contact2", contact2)
            putString("message", message)
            apply()
        }
    }

    //gettere pentru datele de contact
    fun getContact1(): String {
        return prefs.getString("contact1", "") ?: ""
    }

    fun getContact2(): String {
        return prefs.getString("contact2", "") ?: ""
    }

    fun getMessage(): String {
        return prefs.getString("message", "") ?: ""
    }

    //functii pt inactivitate
    fun setInactivityEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_INACTIVITY_ENABLED, enabled).apply()
    }

    fun isInactivityEnabled(): Boolean {
        return prefs.getBoolean(KEY_INACTIVITY_ENABLED, false)
    }

    fun setInactivityMinutes(minutes: Int) {
        prefs.edit().putInt(KEY_INACTIVITY_MINUTES, minutes).apply()
    }

    fun getInactivityMinutes(): Int {
        return prefs.getInt(KEY_INACTIVITY_MINUTES, 10)
    }

    //functii pt detectare baterie
    fun setLowBatteryEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_LOW_BATTERY_ENABLED, enabled).apply()
    }

    fun isLowBatteryEnabled(): Boolean {
        return prefs.getBoolean(KEY_LOW_BATTERY_ENABLED, false)
    }

}