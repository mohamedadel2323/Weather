package com.example.weather

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.weather.model.pojo.Alert

private const val NOTIFICATION_ID = 1

fun NotificationManager.sendNotification(body: String, alert: Alert?, context: Context) {

    val contentIntent = Intent(context, AlertDetailsActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        alert?.let {
            putExtra(Constants.START_TIME, alert.start)
            putExtra(Constants.END_TIME, alert.end)
            putExtra(Constants.DESCRIPTION, alert.description)
            putExtra(Constants.SENDER_NAME, alert.sender_name)
            putExtra(Constants.EVENT, alert.event)
        }
    }
    val contentPendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )


    val builder =
        NotificationCompat.Builder(context, context.getString(R.string.alert_notification_id))
            .apply {
                setSmallIcon(R.drawable.alarm_icon)
                setAutoCancel(true)
                setContentTitle(context.getString(R.string.alert_notification_title))
                setContentText(body)
                setContentIntent(contentPendingIntent)
                setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(body)
                )
                priority = NotificationCompat.PRIORITY_MAX
            }
    notify(NOTIFICATION_ID, builder.build())
}

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            context.getString(R.string.alert_notification_id),
            context.getString(R.string.channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(true)
            lightColor = Color.RED
            enableVibration(true)
            description = context.getString(R.string.channel_description)
        }
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}
