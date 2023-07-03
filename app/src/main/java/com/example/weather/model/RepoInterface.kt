package com.example.weather.model

import com.example.weather.model.pojo.FavoritePlace
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
    suspend fun getWeather(location : Location, unit : String , language : String) : Flow<Response<WeatherResponse>>
    suspend fun insertWeatherToDatabase(weatherResponse: WeatherResponseEntity)
    suspend fun getWeatherFromDatabase() : Flow<WeatherResponseEntity>
    suspend fun insertFavorite(favoritePlace: FavoritePlace)
    fun getAllFavorites(): Flow<List<FavoritePlace>>
    suspend fun deleteFavorite(favoritePlace: FavoritePlace)
    fun getMapFirstTime(): Boolean
    fun setMapFirstTime(first: Boolean)
    fun getNotificationOption(): Boolean
    fun setLanguageOption(language: String)
    fun getLanguageOption(): String?
    fun getUnitOption(): String?
    fun setUnitOption(option: String)
    fun getTemperatureOption(): String?
    fun setTemperatureOption(temp: String)
    fun getMapFavorite(): Boolean
    fun setMapFavorite(isFavorite: Boolean)
}