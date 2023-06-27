package com.example.weather.model

import com.example.weather.model.pojo.Location
import com.example.weather.model.pojo.WeatherResponse
import com.example.weather.model.pojo.WeatherResponseEntity
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RepoInterface {
    fun getFirstTime() : Boolean
    fun setFirstTime(first : Boolean)
    fun getLatitude() : Double
    fun setLatitude(latitude : Double)
    fun getLongitude() : Double
    fun setLongitude(longitude : Double)
    fun getLocationOption() : String
    fun setLocationOption(option : String)
    fun setNotificationOption(option: Boolean)
    suspend fun getWeather(location : Location) : Flow<Response<WeatherResponse>>
    suspend fun insertWeatherToDatabase(weatherResponse: WeatherResponseEntity)
    suspend fun getWeatherFromDatabase() : Flow<WeatherResponseEntity>
}