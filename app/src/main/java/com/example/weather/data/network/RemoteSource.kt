package com.example.weather.data.network

import com.example.weather.model.pojo.Location
import com.example.weather.model.pojo.WeatherResponse
import retrofit2.Response

interface RemoteSource {
    suspend fun  getWeather(location : Location, unit : String , language : String) : Response<WeatherResponse>
}