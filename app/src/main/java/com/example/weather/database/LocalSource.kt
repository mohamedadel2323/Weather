package com.example.weather.database

import com.example.weather.model.pojo.FavoritePlace
import com.example.weather.model.pojo.WeatherResponseEntity
import kotlinx.coroutines.flow.Flow

interface LocalSource {
    suspend fun insertWeather(weatherResponse: WeatherResponseEntity)
    fun getCurrentWeather() : Flow<WeatherResponseEntity>
    suspend fun insertFavorite(favoritePlace: FavoritePlace)
    fun getAllFavorites(): Flow<List<FavoritePlace>>
    fun deleteFavorite(favoritePlace: FavoritePlace)
}