package com.example.weather

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("dayName")
fun convertTimeStampToDayName(view: TextView, timeStamp: Int?) {
    val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
    val date = Date(timeStamp?.times(1000L) ?: 0)
    val dayOfWeek = sdf.format(date)
    view.text = dayOfWeek
}

@BindingAdapter("date")
fun convertTimeStampToDate(view: TextView, timeStamp: Int?) {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val date = Date(timeStamp?.times(1000L) ?: 0)
    val dayOfWeek = sdf.format(date)
    view.text = dayOfWeek
}

@BindingAdapter("hour")
fun convertTimeStampToHour(view: TextView, timeStamp: Int?) {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val date = Date(timeStamp?.times(1000L) ?: 0)
    val dayOfWeek = sdf.format(date)
    view.text = dayOfWeek
}

@BindingAdapter("state")
fun selectWeatherState(view: LottieAnimationView, iconId: String?) {
    when(iconId){
        "01d" -> {view.setAnimation(R.raw.sunny)}
        "01n" -> {view.setAnimation(R.raw.clear_night)}
        "02d" -> {view.setAnimation(R.raw.partly_cloudy)}
        "02n" -> {view.setAnimation(R.raw.partly_cloudy_night)}
        "03d" , "03n" -> {view.setAnimation(R.raw.scatteredclouds)}
        "04d" , "04n" -> {view.setAnimation(R.raw.broken)}
        "09d" , "09n" -> {view.setAnimation(R.raw.rain)}
        "10d" , "10n" -> {view.setAnimation(R.raw.rain)}
        "11d" , "11n" -> {view.setAnimation(R.raw.storm)}
        "13d" , "13n" -> {view.setAnimation(R.raw.weather_snow)}
        "50d" , "50n" -> {view.setAnimation(R.raw.windy)}
        else -> {}
    }
    view.playAnimation()
}