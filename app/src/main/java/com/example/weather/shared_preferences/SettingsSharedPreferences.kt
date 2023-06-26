package com.example.weather.shared_preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.weather.Constants
import com.example.weather.Constants.Companion.START_DIALOG_KEY

class SettingsSharedPreferences private constructor(var context: Context) :
    SettingsSharedPreferencesSource {
    var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE)
    private var editor = sharedPreferences.edit()

    init {
        sharedPreferences =
            context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    companion object {
        @Volatile
        private var instance: SettingsSharedPreferences? = null

        fun getInstance(context: Context): SettingsSharedPreferences {
            return instance ?: synchronized(this) {
                val tempInstance = SettingsSharedPreferences(context)
                instance = tempInstance
                tempInstance
            }
        }
    }

    override fun setLong(longitude: Float) {
        editor.putFloat(Constants.LONGITUDE, longitude)
        editor.commit()
    }

    override fun setLat(latitude: Float) {
        editor.putFloat(Constants.LATITUDE, latitude)
        editor.commit()
    }

    override fun getLong() =
        sharedPreferences.getFloat(Constants.LONGITUDE, 0.0f)


    override fun getLat() =
        sharedPreferences.getFloat(Constants.LATITUDE, 0.0f)

    override fun getFirstTime() =
        sharedPreferences.getBoolean(START_DIALOG_KEY, true)


    override fun setFirstTime(first: Boolean) {
        editor.putBoolean(START_DIALOG_KEY, first)
        editor.commit()
    }

}