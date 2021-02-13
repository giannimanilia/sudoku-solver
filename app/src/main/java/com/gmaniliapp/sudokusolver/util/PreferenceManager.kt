package com.gmaniliapp.sudokusolver.util

import android.content.Context
import android.content.SharedPreferences
import java.util.*

class PreferenceManager(context: Context, name: String?) {

    private val instance: SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    fun putInt(key: String?, value: Int): Boolean {
        val editor = instance.edit()
        editor.putInt(key, value)
        return editor.commit()
    }

    fun getInt(key: String?, default_value: Int): Int {
        return try {
            instance.getString(key, default_value.toString() + "")!!.toInt()
        } catch (e: Exception) {
            instance.getInt(key, default_value)
        }
    }

    fun putBoolean(key: String?, value: Boolean?) {
        val editor = instance.edit()
        editor.putBoolean(key, value!!)
        editor.apply()
    }

    fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return instance.getBoolean(key, defValue)
    }

    fun putDate(key: String?, value: Date) {
        val time = value.time
        val editor = instance.edit()
        editor.putLong(key, time)
        editor.apply()
    }

    fun getDate(key: String?): Date? {
        return try {
            val value = instance.getLong(key, -1)
            if (value != -1L) {
                Date(value)
            } else null
        } catch (e: Exception) {
            null
        }
    }

}