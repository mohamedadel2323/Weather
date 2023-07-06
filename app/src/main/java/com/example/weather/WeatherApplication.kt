package com.example.weather

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import timber.log.Timber
import java.util.Locale


class WeatherApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        val sharedPreferences = getSharedPreferences(Constants.PREFERENCES_NAME, MODE_PRIVATE)
        val locale = Locale(sharedPreferences.getString(Constants.LANGUAGE, "en"))
        Locale.setDefault(locale)
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val sharedPreferences = getSharedPreferences(Constants.PREFERENCES_NAME, MODE_PRIVATE)
        val locale = Locale(sharedPreferences.getString(Constants.LANGUAGE, "en"))
        Locale.setDefault(locale)
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            StrictMode.setThreadPolicy(
                ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }
    }
}