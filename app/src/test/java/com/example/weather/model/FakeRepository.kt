package com.example.weather.model

import com.example.weather.model.pojo.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response
import java.util.*

class FakeRepository(
    private var weatherList: MutableList<WeatherResponseEntity>,
    private var favoriteList: MutableList<FavoritePlace>,
    private var alertList: MutableList<AlertEntity>,
    var weatherResponse: WeatherResponse?
) : RepoInterface {


    override suspend fun getWeather(
        location: Location,
        unit: String,
        language: String
    ): Flow<Response<WeatherResponse>> {
        weatherResponse?.let {
            return flowOf(Response.success(weatherResponse))
        }
        weatherResponse = WeatherResponse(
            listOf(), Current(
                0, 0.0, 0, 0.0, 0, 0, 0, 0, 0.0, 0.0, 0,
                listOf(), 0, 0.0, 0.0
            ), listOf(), listOf(), listOf(), 0.0, 0.0, "failed", 1
        )
        return flowOf(Response.success(weatherResponse))
    }

    override suspend fun insertWeatherToDatabase(weatherResponse: WeatherResponseEntity) {
        weatherList.clear()
        weatherList.add(weatherResponse)
    }

    override suspend fun getWeatherFromDatabase(): Flow<WeatherResponseEntity> =
        flowOf(weatherList.first())


    override suspend fun insertFavorite(favoritePlace: FavoritePlace) {
        favoriteList.add(favoritePlace)
    }

    override fun getAllFavorites(): Flow<List<FavoritePlace>> = flowOf(favoriteList)

    override suspend fun deleteFavorite(favoritePlace: FavoritePlace) {
        favoriteList.remove(favoritePlace)
    }


    override fun getAllAlerts(): Flow<List<AlertEntity>> = flowOf(alertList)

    override suspend fun insertAlert(alert: AlertEntity) {
        alertList.add(alert)
    }

    override suspend fun deleteAlert(alert: AlertEntity) {
        alertList.remove(alert)
    }

    override suspend fun deleteAlertByUuid(uuid: UUID) {
        alertList.removeIf { it.id == uuid }
    }


    override fun getFirstTime(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setFirstTime(first: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getLatitude(): Double {
        TODO("Not yet implemented")
    }

    override fun setLatitude(latitude: Double) {
        TODO("Not yet implemented")
    }

    override fun getLongitude(): Double {
        TODO("Not yet implemented")
    }

    override fun setLongitude(longitude: Double) {
        TODO("Not yet implemented")
    }

    override fun getLocationOption(): String {
        TODO("Not yet implemented")
    }

    override fun setLocationOption(option: String) {
        TODO("Not yet implemented")
    }

    override fun setNotificationOption(option: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getMapFirstTime(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setMapFirstTime(first: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getNotificationOption(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setLanguageOption(language: String) {
        TODO("Not yet implemented")
    }

    override fun getLanguageOption(): String? {
        TODO("Not yet implemented")
    }

    override fun getUnitOption(): String? {
        TODO("Not yet implemented")
    }

    override fun setUnitOption(option: String) {
        TODO("Not yet implemented")
    }

    override fun getTemperatureOption(): String? {
        TODO("Not yet implemented")
    }

    override fun setTemperatureOption(temp: String) {
        TODO("Not yet implemented")
    }

    override fun getMapFavorite(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setMapFavorite(isFavorite: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getDetails(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setDetails(isDetails: Boolean) {
        TODO("Not yet implemented")
    }
}