package com.example.weather.alerts

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weather.Constants
import timber.log.Timber

class AlertWorker(private val context: Context , workerParameters: WorkerParameters) : CoroutineWorker(context , workerParameters) {
    override suspend fun doWork(): Result {

        Timber.e("done${inputData.getDouble(Constants.LONGITUDE , 0.0)}")
        return Result.success()
    }
}