package com.example.safealert.data

import android.content.Context

class PreferencesManager(context: Context){

    private val prefs = context.getSharedPreferences("safealert_prefs", Context.MODE_PRIVATE)
    private val KEY_INACTIVITY_ENABLED = "inactivity_enabled" //pt feature inactivitate
    private val KEY_INACTIVITY_MINUTES = "inactivity_minutes"

    private val KEY_LOW_BATTERY_ENABLED = "low_battery_enabled" //pt feature baterie

    private val KEY_WEATHER_ALERTS_ENABLED = "weather_alerts_enabled" //pt functia de vreme
    private val KEY_WEATHER_SMS_ENABLED = "weather_sms_enabled"

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

    //functii pt functia de vreme
    fun setWeatherAlertsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_WEATHER_ALERTS_ENABLED, enabled).apply()
    }

    fun isWeatherAlertsEnabled(): Boolean {
        return prefs.getBoolean(KEY_WEATHER_ALERTS_ENABLED, false)
    }

    fun setWeatherSmsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_WEATHER_SMS_ENABLED , enabled).apply()
    }

    fun isWeatherSmsEnabled(): Boolean {
        return prefs.getBoolean(KEY_WEATHER_SMS_ENABLED , false)
    }

    //funcții pentru programare mesaje
    fun setScheduledMessageEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("scheduled_message_enabled", enabled).apply()
    }

    fun isScheduledMessageEnabled(): Boolean {
        return prefs.getBoolean("scheduled_message_enabled", false)
    }

    fun setScheduledMessageText(message: String) {
        prefs.edit().putString("scheduled_message_text", message).apply()
    }

    fun getScheduledMessageText(): String {
        return prefs.getString("scheduled_message_text", "") ?: ""
    }

    fun setScheduledMessageHours(hours: Int) {
        prefs.edit().putInt("scheduled_message_hours", hours).apply()
    }

    fun getScheduledMessageHours(): Int {
        return prefs.getInt("scheduled_message_hours", 3)
    }

    //functii geofencing
    fun setSafeZoneEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("safe_zone_enabled", enabled).apply()
    }

    fun isSafeZoneEnabled(): Boolean {
        return prefs.getBoolean("safe_zone_enabled", false)
    }

    fun setSafeZoneLatitude(latitude: String) {
        prefs.edit().putString("safe_zone_latitude", latitude).apply()
    }

    fun getSafeZoneLatitude(): String {
        return prefs.getString("safe_zone_latitude", "") ?: ""
    }

    fun setSafeZoneLongitude(longitude: String) {
        prefs.edit().putString("safe_zone_longitude", longitude).apply()
    }

    fun getSafeZoneLongitude(): String {
        return prefs.getString("safe_zone_longitude", "") ?: ""
    }

    fun setSafeZoneRadius(radiusMeters: Int) {
        prefs.edit().putInt("safe_zone_radius", radiusMeters).apply()
    }

    fun getSafeZoneRadius(): Int {
        return prefs.getInt("safe_zone_radius", 100)
    }

}