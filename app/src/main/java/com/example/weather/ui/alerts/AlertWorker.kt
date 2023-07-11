package com.example.weather.ui.alerts

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weather.uitils.Constants
import com.example.weather.R
import com.example.weather.data.database.ConcreteLocalSource
import com.example.weather.databinding.CustomAlertViewBinding
import com.example.weather.model.Repository
import com.example.weather.model.pojo.Alert
import com.example.weather.model.pojo.Location
import com.example.weather.model.pojo.WeatherResponseEntity
import com.example.weather.data.network.ApiClient
import com.example.weather.uitils.sendNotification
import com.example.weather.data.shared_preferences.SettingsSharedPreferences
import com.example.weather.uitils.toWeatherResponseEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

class AlertWorker(private val context: Context, private val workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    @RequiresApi(Build.VERSION_CODES.M)
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val repository = Repository.getInstance(
        SettingsSharedPreferences.getInstance(context),
        ApiClient,
        ConcreteLocalSource(context)
    )
    private val notificationMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.weather_alert)
    }
    private val alertMediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.summer)
    }
    private var timezone = ""
    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun doWork(): Result {
        val longitude = inputData.getDouble(Constants.LONGITUDE, 0.0)
        val latitude = inputData.getDouble(Constants.LATITUDE, 0.0)
        val notificationOption = inputData.getBoolean(Constants.NOTIFICATION_OPTION, false)
        val startTime = inputData.getLong(Constants.START_TIME, 0)
        val endTime = inputData.getLong(Constants.END_TIME, 0)
        var weatherResponseEntity: WeatherResponseEntity? = null

        withContext(Dispatchers.IO) {
            repository.getWeather(
                Location(longitude, latitude),
                repository.getTemperatureOption() as String,
                repository.getLanguageOption() as String
            ).collectLatest {
                if (it.isSuccessful) {
                    weatherResponseEntity = it.body()?.toWeatherResponseEntity()
                }
            }
        }
        Timber.e(weatherResponseEntity.toString())

        if (weatherResponseEntity != null) {
            if (weatherResponseEntity!!.alerts.isNullOrEmpty()) {
                notificationOrAlertFineWeather(notificationOption)
            } else {
                val alert = weatherResponseEntity!!.alerts?.get(0)
                alert?.let {
                    Timber.e("start time : $startTime end Time : $endTime , alert: $alert")
                    if ((startTime.toInt() >= alert.start && startTime.toInt() <= alert.end) || (endTime.toInt() >= alert.start && endTime.toInt() <= alert.end)) {
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
                                notificationMediaPlayer.start()
                                withContext(Dispatchers.IO){
                                    repository.deleteAlertByUuid(workerParameters.id)
                                }
                            }
                            else -> {
                                val alert = weatherResponseEntity!!.alerts?.get(0)
                                timezone = weatherResponseEntity!!.timezone

                                withContext(Dispatchers.Main) {
                                    if (alert != null) {
                                        showAlertDialog(
                                            alert
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        notificationOrAlertFineWeather(notificationOption)
                    }

                }
            }
            Timber.e("done${inputData.getDouble(Constants.LONGITUDE, 0.0)}")
            return Result.success()
        } else {
            return Result.failure()
        }
    }

    private fun showAlertDialog(alert: Alert) {
        val alertCustomAlertViewBinding =
            CustomAlertViewBinding.inflate(LayoutInflater.from(context))

        alertMediaPlayer.isLooping = true
        AlertDialog.Builder(context).create().apply {
            setOnShowListener {
                alertMediaPlayer.start()
            }
            setOnDismissListener {
                alertMediaPlayer.stop()
            }
            setView(alertCustomAlertViewBinding.root)

            alertCustomAlertViewBinding.alert = alert
            alertCustomAlertViewBinding.timeZone = timezone

            alertCustomAlertViewBinding.dismissBtn.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.IO) {
                        repository.deleteAlertByUuid(workerParameters.id)

                    }
                }
                dismiss()
            }
            alertCustomAlertViewBinding.customAlertCv.setOnClickListener {
                alertMediaPlayer.stop()
            }
            alertCustomAlertViewBinding.alertDialogDescription.setOnClickListener {
                alertMediaPlayer.stop()
            }
            window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setGravity(Gravity.TOP)
            show()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    delay(30000)
                    withContext(Dispatchers.Main) {
                        dismiss()
                    }
                }finally {
                    cancel()
                }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun notificationOrAlertFineWeather(notificationOption: Boolean) {
        when (notificationOption) {
            true -> {
                notificationManager.sendNotification(
                    context.getString(R.string.no_alerts),
                    null,
                    context
                )
                notificationMediaPlayer.start()
                withContext(Dispatchers.IO){
                    repository.deleteAlertByUuid(workerParameters.id)
                }
            }
            else -> {
                withContext(Dispatchers.Main) {
                    showAlertDialog(
                        Alert(
                            context.getString(R.string.weather_is_fine), 0, "", "", 0,
                            emptyList()
                        )
                    )
                }
            }
        }
    }
}