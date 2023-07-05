package com.example.weather.model.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(tableName = "weather")
data class WeatherResponseEntity(
    val alerts: List<Alert>? = null,
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int,
    @PrimaryKey
    val id: Int = 1
)
