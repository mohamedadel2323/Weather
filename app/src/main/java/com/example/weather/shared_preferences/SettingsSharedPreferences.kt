package com.example.weather.shared_preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.weather.Constants

class SettingsSharedPreferences private constructor(var context: Context) :
    SettingsSharedPreferencesSource {
    var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    companion object {
        private var instance: SettingsSharedPreferences? = null

        fun getInstance(context: Context): SettingsSharedPreferences {
            return instance ?: synchronized(this) {
                val tempInstance = SettingsSharedPreferences(context)
                instance = tempInstance
                tempInstance
            }
        }
    }

    fun setLong(longitude: Float) {
        editor.putFloat(Constants.LONGITUDE, longitude)
    }

    fun setLat(latitude: Float) {
        editor.putFloat(Constants.LATITUDE, latitude)
    }

    fun getLong() =
        sharedPreferences.getFloat(Constants.LONGITUDE, 0.0f)


    fun getLat(latitude: Float) =
        sharedPreferences.getFloat(Constants.LATITUDE, 0.0f)

}