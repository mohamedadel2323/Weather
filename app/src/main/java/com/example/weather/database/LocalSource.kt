package com.example.weather.database

import com.example.weather.model.pojo.WeatherResponse
import com.example.weather.model.pojo.WeatherResponseEntity
import kotlinx.coroutines.flow.Flow

interface LocalSource {
    suspend fun insertWeather(weatherResponse: WeatherResponseEntity)
    fun getCurrentWeather() : Flow<WeatherResponseEntity>
}