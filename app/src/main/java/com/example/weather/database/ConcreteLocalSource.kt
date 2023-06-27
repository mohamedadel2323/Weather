package com.example.weather.database

import android.content.Context
import com.example.weather.model.pojo.WeatherResponseEntity
import kotlinx.coroutines.flow.Flow

class ConcreteLocalSource(private val context: Context) : LocalSource {

    private val dao: WeatherDao by lazy {
        val db: WeatherDatabase = WeatherDatabase.getInstance(context)
        db.getWeatherDAo()
    }

    override suspend fun insertWeather(weatherResponse: WeatherResponseEntity) {
        dao.insertWeather(weatherResponse)
    }

    override fun getCurrentWeather(): Flow<WeatherResponseEntity> {
        return dao.getCurrentWeather()
    }
}