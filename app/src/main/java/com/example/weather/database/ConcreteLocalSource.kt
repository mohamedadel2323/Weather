package com.example.weather.database

import android.content.Context
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather.model.pojo.AlertEntity
import com.example.weather.model.pojo.FavoritePlace
import com.example.weather.model.pojo.WeatherResponseEntity
import kotlinx.coroutines.flow.Flow

class ConcreteLocalSource(private val context: Context) : LocalSource {

    private val dao: WeatherDao by lazy {
        val db: WeatherDatabase = WeatherDatabase.getInstance(context)
        db.getWeatherDAo()
    }
    private val alertDao: AlertDao by lazy {
        val db: WeatherDatabase = WeatherDatabase.getInstance(context)
        db.getAlertDao()
    }

    override suspend fun insertWeather(weatherResponse: WeatherResponseEntity) {
        dao.insertWeather(weatherResponse)
    }

    override fun getCurrentWeather(): Flow<WeatherResponseEntity> {
        return dao.getCurrentWeather()
    }

    override suspend fun insertFavorite(favoritePlace: FavoritePlace) {
        dao.insertFavorite(favoritePlace)
    }

    override fun getAllFavorites(): Flow<List<FavoritePlace>> {
        return dao.getAllFavorites()
    }

    override suspend fun deleteFavorite(favoritePlace: FavoritePlace) {
        dao.deleteFavorite(favoritePlace)
    }

    override suspend fun insertAlert(alert: AlertEntity){
        alertDao.insertAlert(alert)
    }

    override fun getAllAlerts(): Flow<List<AlertEntity>> = alertDao.getAllAlerts()

    override suspend fun deleteAlert(alert: AlertEntity){
        alertDao.deleteAlert(alert)
    }
}