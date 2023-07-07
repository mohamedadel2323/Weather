package com.example.weather.alerts

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weather.Constants
import com.example.weather.R
import com.example.weather.database.ConcreteLocalSource
import com.example.weather.model.Repository
import com.example.weather.model.pojo.Alert
import com.example.weather.model.pojo.Location
import com.example.weather.model.pojo.WeatherResponseEntity
import com.example.weather.network.ApiClient
import com.example.weather.sendNotification
import com.example.weather.shared_preferences.SettingsSharedPreferences
import com.example.weather.toWeatherResponseEntity
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

class AlertWorker(private val context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    @RequiresApi(Build.VERSION_CODES.M)
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val repository = Repository.getInstance(
        SettingsSharedPreferences.getInstance(context),
        ApiClient,
        ConcreteLocalSource(context)
    )

    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun doWork(): Result {
        val longitude = inputData.getDouble(Constants.LONGITUDE, 0.0)
        val latitude = inputData.getDouble(Constants.LATITUDE, 0.0)
        val notificationOption = inputData.getBoolean(Constants.NOTIFICATION_OPTION, false)
        var weatherResponseEntity: WeatherResponseEntity? = null
        repository.getWeather(
            Location(longitude, latitude),
            repository.getTemperatureOption() as String,
            repository.getLanguageOption() as String
        ).collectLatest {
            if (it.isSuccessful) {
                weatherResponseEntity = it.body()?.toWeatherResponseEntity()
            }
        }
        Timber.e(weatherResponseEntity.toString())

        if (weatherResponseEntity != null) {
            if (weatherResponseEntity!!.alerts.isNullOrEmpty()) {
                when (notificationOption) {
                    true -> {
                        notificationManager.sendNotification(
                            context.getString(R.string.no_alerts),
                            null,
                            context
                        )
                    }

                    else -> {

                    }
                }
            } else {

                when (notificationOption) {
                    true -> {
                        weatherResponseEntity!!.alerts?.get(0)
                            ?.let {
                                weatherResponseEntity!!.alerts?.let { alerts ->
                                    notificationManager.sendNotification(
                                        it.description,
                                        alerts[0], context
                                    )
                                }
                            }
                    }
                    else -> {}
                }

            }
        } else {
            return Result.failure()
        }


        Timber.e("done${inputData.getDouble(Constants.LONGITUDE, 0.0)}")
        return Result.success()
    }
}