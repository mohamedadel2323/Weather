package com.example.weather.network

import com.example.weather.model.pojo.Location
import com.example.weather.model.pojo.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("onecall")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") language: String = "en",
        @Query("units") unit: String = "metric",
        @Query("appid") apiKey: String = "167774d7803ed3e0cc606576255ddf6c"
    ): Response<WeatherResponse>
}