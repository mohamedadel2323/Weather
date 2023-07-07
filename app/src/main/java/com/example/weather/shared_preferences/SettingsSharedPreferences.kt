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

    override fun getMapFirstTime() =
        sharedPreferences.getBoolean(Constants.START_MAP, true)


    override fun setMapFirstTime(first: Boolean) {
        editor.putBoolean(Constants.START_MAP, first)
        editor.commit()
    }

    override fun setLocationOption(way: String) {
        editor.putString(Constants.LOCATION_OPTION, way)
        editor.commit()
    }

    override fun getLocationOption() =
        sharedPreferences.getString(Constants.LOCATION_OPTION, Constants.GPS) ?: Constants.GPS


    override fun getLanguageOption() =
        sharedPreferences.getString(Constants.LANGUAGE, Constants.ENGLISH)

    override fun setLanguageOption(state: String) {
        editor.putString(Constants.LANGUAGE, state)
        editor.commit()
    }

    override fun getNotificationOption() =
        sharedPreferences.getBoolean(Constants.NOTIFICATION_OPTION, false)

    override fun setNotificationOption(state: Boolean) {
        editor.putBoolean(Constants.NOTIFICATION_OPTION, state)
        editor.commit()
    }

    override fun setUnitOption(option: String) {
        editor.putString(Constants.UNIT, option)
        editor.commit()
    }

    override fun getUnitOption() =
        sharedPreferences.getString(Constants.UNIT, Constants.METRIC)

    override fun setTemperatureOption(temp: String) {
        editor.putString(Constants.TEMPERATURE, temp)
        editor.commit()
    }

    override fun getTemperatureOption() =
        sharedPreferences.getString(Constants.TEMPERATURE, Constants.METRIC)

    override fun setMapFavorite(isFavorite: Boolean) {
        editor.putBoolean(Constants.MAP_FAVORITE, isFavorite)
        editor.commit()
    }

    override fun getDetails() =
        sharedPreferences.getBoolean(Constants.DETAILS, false)

    override fun setDetails(isDetails: Boolean) {
        editor.putBoolean(Constants.DETAILS, isDetails)
        editor.commit()
    }

    override fun getMapFavorite() =
        sharedPreferences.getBoolean(Constants.MAP_FAVORITE, false)

}