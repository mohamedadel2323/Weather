package com.example.weather.shared_preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.example.weather.Constants

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
        @SuppressLint("StaticFieldLeak")
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
        sharedPreferences.getBoolean(Constants.START_DIALOG_KEY, true)


    override fun setFirstTime(first: Boolean) {
        editor.putBoolean(Constants.START_DIALOG_KEY, first)
        editor.commit()
    }

    override fun setLocationOption(way: String) {
        editor.putString(Constants.LOCATION_OPTION, way)
        editor.commit()
    }

    override fun getLocationOption() =
        sharedPreferences.getString(Constants.LOCATION_OPTION, Constants.GPS) ?: Constants.GPS

    override fun setNotificationOption(state: Boolean) {
        editor.putBoolean(Constants.NOTIFICATION_OPTION, state)
        editor.commit()
    }

    override fun getNotificationOption() =
        sharedPreferences.getBoolean(Constants.NOTIFICATION_OPTION, false)


}