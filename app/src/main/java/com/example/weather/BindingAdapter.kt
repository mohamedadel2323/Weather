package com.example.weather

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("dayName")
fun convertTimeStampToDayName(view: TextView, timeStamp: Int?) {
    val sharedPreferences = view.context.getSharedPreferences(
        Constants.PREFERENCES_NAME,
        AppCompatActivity.MODE_PRIVATE
    )
    val sdf =
        SimpleDateFormat("EEEE",
            sharedPreferences.getString(Constants.LANGUAGE, "en")?.let { Locale(it) })
    val date = Date(timeStamp?.times(1000L) ?: 0)
    val dayOfWeek = sdf.format(date)
    view.text = dayOfWeek
}

@BindingAdapter(value = ["bind:date", "bind:timeOffset"], requireAll = true)
fun convertTimeStampToCurrentHour(view: TextView, currentDate: Int?, offset:Int?) {
    val sharedPreferences = view.context.getSharedPreferences(
        Constants.PREFERENCES_NAME,
        AppCompatActivity.MODE_PRIVATE
    )
    val sdf = SimpleDateFormat(
        "dd MMM yyyy\nhh:mm a",
        sharedPreferences.getString(Constants.LANGUAGE, "en")?.let { Locale(it) }
    )
    sdf.timeZone = TimeZone.getTimeZone("GMT")
    val date = currentDate?.let { Date(it.times(1000L).plus(offset!!.times(1000L)) ) }
    val currentTime = sdf.format(date)
    view.text = currentTime
}

@BindingAdapter("time")
fun convertTimeStampToCompleteDate(view: TextView, timeStamp: Long?) {
    val sharedPreferences = view.context.getSharedPreferences(
        Constants.PREFERENCES_NAME,
        AppCompatActivity.MODE_PRIVATE
    )
    val sdf = SimpleDateFormat(
        "h:mm a\ndd MMM",
        sharedPreferences.getString(Constants.LANGUAGE, "en")?.let { Locale(it) }
    )
    val date = Date(timeStamp ?: 0)
    val dayOfWeek = sdf.format(date)
    view.text = dayOfWeek
}

@BindingAdapter("fullDate")
fun convertTimeStampToCompleteDate2(view: TextView, timeStamp: Int?) {
    val sharedPreferences = view.context.getSharedPreferences(
        Constants.PREFERENCES_NAME,
        AppCompatActivity.MODE_PRIVATE
    )
    val sdf = SimpleDateFormat(
        "dd MMM yyyy h:mm a",
        sharedPreferences.getString(Constants.LANGUAGE, "en")?.let { Locale(it) }
    )
    val date = Date(timeStamp?.times(1000L) ?: 0)
    val dayOfWeek = sdf.format(date)
    view.text = dayOfWeek
}

@BindingAdapter("hour")
fun convertTimeStampToHour(view: TextView, timeStamp: Int?) {
    val sharedPreferences = view.context.getSharedPreferences(
        Constants.PREFERENCES_NAME,
        AppCompatActivity.MODE_PRIVATE
    )
    val sdf =
        SimpleDateFormat("hh:mm a",
            sharedPreferences.getString(Constants.LANGUAGE, "en")?.let { Locale(it) })
    val date = Date(timeStamp?.times(1000L) ?: 0)
    val dayOfWeek = sdf.format(date)
    view.text = dayOfWeek
}

@BindingAdapter("currentDegree")
fun currentDegreeFormatter(view: TextView, degree: Double?) {
    val sharedPreferences = view.context.getSharedPreferences(
        Constants.PREFERENCES_NAME,
        AppCompatActivity.MODE_PRIVATE
    )
    when (sharedPreferences.getString(Constants.TEMPERATURE, Constants.METRIC)) {
        Constants.METRIC -> view.text =
            String.format(view.context.getString(R.string.singleDegreeFormatC), degree)
        Constants.IMPERIAL -> view.text =
            String.format(view.context.getString(R.string.singleDegreeFormatF), degree)
        else -> view.text =
            String.format(view.context.getString(R.string.singleDegreeFormatK), degree)
    }
}

@BindingAdapter("uvi")
fun uviFormatter(view: TextView, uvi: Double) {
    view.text = String.format(view.context.getString(R.string.uvi_Format), uvi)
}

@BindingAdapter(value = ["bind:first", "bind:second"], requireAll = true)
fun concatDegreeFormatter(view: TextView, minDegree: Double?, maxDegree: Double?) {
    val sharedPreferences = view.context.getSharedPreferences(
        Constants.PREFERENCES_NAME,
        AppCompatActivity.MODE_PRIVATE
    )
    when (sharedPreferences.getString(Constants.TEMPERATURE, Constants.METRIC)) {
        Constants.METRIC -> view.text =
            String.format(view.context.getString(R.string.degreeFormatC), minDegree, maxDegree)
        Constants.IMPERIAL -> view.text =
            String.format(view.context.getString(R.string.degreeFormatF), minDegree, maxDegree)
        else -> view.text =
            String.format(view.context.getString(R.string.degreeFormatK), minDegree, maxDegree)
    }
}

@BindingAdapter("windSpeed")
fun concatDegreeFormatter(view: TextView, windSpeed: Double?) {
    val sharedPreferences = view.context.getSharedPreferences(
        Constants.PREFERENCES_NAME,
        AppCompatActivity.MODE_PRIVATE
    )
    when (sharedPreferences.getString(Constants.TEMPERATURE, Constants.METRIC)) {
        Constants.IMPERIAL -> view.text =
            String.format(view.context.getString(R.string.windFormatMile), windSpeed)
        else -> view.text =
            String.format(view.context.getString(R.string.windFormatMeter), windSpeed)
    }
}

@BindingAdapter("state")
fun selectWeatherState(view: LottieAnimationView, iconId: String?) {
    when (iconId) {
        "01d" -> {
            view.setAnimation(R.raw.sunny)
        }
        "01n" -> {
            view.setAnimation(R.raw.clear_night)
        }
        "02d" -> {
            view.setAnimation(R.raw.partly_cloudy)
        }
        "02n" -> {
            view.setAnimation(R.raw.broken_clouds)
        }
        "03d", "03n" -> {
            view.setAnimation(R.raw.scatteredclouds)
        }
        "04d", "04n" -> {
            view.setAnimation(R.raw.broken_clouds)
        }
        "09d", "09n" -> {
            view.setAnimation(R.raw.rain)
        }
        "10d", "10n" -> {
            view.setAnimation(R.raw.rain)
        }
        "11d", "11n" -> {
            view.setAnimation(R.raw.storm)
        }
        "13d", "13n" -> {
            view.setAnimation(R.raw.weather_snow)
        }
        "50d", "50n" -> {
            view.setAnimation(R.raw.windy)
        }
        else -> {}
    }
    view.playAnimation()
}