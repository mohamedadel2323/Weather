package com.example.weather.network

import com.example.weather.model.pojo.Location
import com.example.weather.model.pojo.WeatherResponse
import retrofit2.Response

interface RemoteSource {
    suspend fun  getWeather(location : Location) : Response<WeatherResponse>
}