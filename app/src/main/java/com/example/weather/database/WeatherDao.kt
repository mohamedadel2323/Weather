package com.example.weather.database

import androidx.room.*
import com.example.weather.model.pojo.FavoritePlace
import com.example.weather.model.pojo.WeatherResponse
import com.example.weather.model.pojo.WeatherResponseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weatherResponse: WeatherResponseEntity)

    @Query("SELECT * FROM weather")
    fun getCurrentWeather(): Flow<WeatherResponseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favoritePlace: FavoritePlace)

    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoritePlace>>

    @Delete
    suspend fun deleteFavorite(favoritePlace: FavoritePlace)
}