package com.example.weather.data.database

import com.example.weather.model.pojo.AlertEntity
import com.example.weather.model.pojo.FavoritePlace
import com.example.weather.model.pojo.WeatherResponseEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.*

class FakeLocalSource(
    private var weatherEntityList: MutableList<WeatherResponseEntity> = mutableListOf(),
    private var favoriteList: MutableList<FavoritePlace> = mutableListOf(),
    private var alertList: MutableList<AlertEntity> = mutableListOf()
) : LocalSource {
    override suspend fun insertWeather(weatherResponse: WeatherResponseEntity) {
        weatherEntityList.clear()
        weatherEntityList.add(weatherResponse)
    }

    override fun getCurrentWeather(): Flow<WeatherResponseEntity> =
        flowOf(weatherEntityList.first())


    override suspend fun insertFavorite(favoritePlace: FavoritePlace) {
        favoriteList.add(favoritePlace)
    }

    override fun getAllFavorites(): Flow<List<FavoritePlace>> = flowOf(favoriteList)


    override suspend fun deleteFavorite(favoritePlace: FavoritePlace) {
        favoriteList.remove(favoritePlace)
    }

    override suspend fun insertAlert(alert: AlertEntity) {
        alertList.add(alert)
    }

    override fun getAllAlerts(): Flow<List<AlertEntity>> = flowOf(alertList)


    override suspend fun deleteAlert(alert: AlertEntity) {
        alertList.remove(alert)
    }

    override suspend fun deleteAlertByUuid(uuid: UUID) {
        alertList.removeIf { it.id == uuid }
    }
}