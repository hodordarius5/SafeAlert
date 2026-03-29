package com.example.safealert.data

import android.content.Context

class PreferencesManager(context: Context){

    private val prefs = context.getSharedPreferences("safealert_prefs", Context.MODE_PRIVATE)

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
}