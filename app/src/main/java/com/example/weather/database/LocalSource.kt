package com.example.weather.database

import com.example.weather.model.pojo.AlertEntity
import com.example.weather.model.pojo.FavoritePlace
import com.example.weather.model.pojo.WeatherResponseEntity
import kotlinx.coroutines.flow.Flow

interface LocalSource {
    suspend fun insertWeather(weatherResponse: WeatherResponseEntity)
    fun getCurrentWeather() : Flow<WeatherResponseEntity>
    suspend fun insertFavorite(favoritePlace: FavoritePlace)
    fun getAllFavorites(): Flow<List<FavoritePlace>>
    suspend fun deleteFavorite(favoritePlace: FavoritePlace)
    suspend fun insertAlert(alert: AlertEntity)
    fun getAllAlerts(): Flow<List<AlertEntity>>
    suspend fun deleteAlert(alert: AlertEntity)
}