package com.example.weather.model

import com.example.weather.model.pojo.Location
import com.example.weather.model.pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RepoInterface {
    fun getFirstTime() : Boolean
    fun setFirstTime(first : Boolean)
    fun getLatitude() : Double
    fun setLatitude(latitude : Double)
    fun getLongitude() : Double
    fun setLongitude(longitude : Double)
    suspend fun getWeather(location : Location) : Flow<Response<WeatherResponse>>
}