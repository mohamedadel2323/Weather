package com.example.weather.model

import com.example.weather.Constants
import com.example.weather.database.LocalSource
import com.example.weather.model.pojo.FavoritePlace
import com.example.weather.model.pojo.Location
import com.example.weather.model.pojo.WeatherResponse
import com.example.weather.model.pojo.WeatherResponseEntity
import com.example.weather.network.RemoteSource
import com.example.weather.shared_preferences.SettingsSharedPreferencesSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response

class Repository(
    private val sharedPreferencesSource: SettingsSharedPreferencesSource,
    private val remoteSource: RemoteSource,
    private val localSource: LocalSource
) : RepoInterface {

    companion object {
        private var instance: Repository? = null
        fun getInstance(
            sharedPreferencesSource: SettingsSharedPreferencesSource,
            remoteSource: RemoteSource,
            localSource: LocalSource
        ): Repository {
            return instance ?: synchronized(this) {
                val temp = Repository(sharedPreferencesSource, remoteSource, localSource)
                instance = temp
                temp
            }
        }
    }

    override fun getFirstTime() = sharedPreferencesSource.getFirstTime()

    override fun setFirstTime(first: Boolean) {
        sharedPreferencesSource.setFirstTime(first)
    }

    override fun getMapFirstTime() = sharedPreferencesSource.getMapFirstTime()

    override fun setMapFirstTime(first: Boolean) {
        sharedPreferencesSource.setMapFirstTime(first)
    }

    override fun getLatitude(): Double = sharedPreferencesSource.getLat().toDouble()

    override fun setLatitude(latitude: Double) {
        sharedPreferencesSource.setLat(latitude.toFloat())
    }

    override fun getLongitude(): Double = sharedPreferencesSource.getLong().toDouble()

    override fun setLongitude(longitude: Double) {
        sharedPreferencesSource.setLong(longitude.toFloat())
    }

    override fun getLocationOption() = sharedPreferencesSource.getLocationOption()

    override fun setLocationOption(option: String) {
        sharedPreferencesSource.setLocationOption(option)
    }

    override fun setNotificationOption(option: Boolean) {
        sharedPreferencesSource.setNotificationOption(option)
    }

    override fun getNotificationOption() = sharedPreferencesSource.getNotificationOption()

    override fun setLanguageOption(language: String) {
        sharedPreferencesSource.setLanguageOption(language)
    }

    override fun getUnitOption() = sharedPreferencesSource.getUnitOption()

    override fun setUnitOption(option: String) {
        sharedPreferencesSource.setUnitOption(option)
    }

    override fun getTemperatureOption() = sharedPreferencesSource.getTemperatureOption()

    override fun setTemperatureOption(temp: String) {
        sharedPreferencesSource.setTemperatureOption(temp)
    }

    override fun getMapFavorite() = sharedPreferencesSource.getMapFavorite()

    override fun setMapFavorite(isFavorite: Boolean) {
        sharedPreferencesSource.setMapFavorite(isFavorite)
    }

    override fun getLanguageOption() = sharedPreferencesSource.getLanguageOption()

    override suspend fun getWeather(
        location: Location,
        unit: String,
        language: String
    ): Flow<Response<WeatherResponse>> =
        flowOf(remoteSource.getWeather(location, unit, language))


    override suspend fun insertWeatherToDatabase(weatherResponse: WeatherResponseEntity) {
        localSource.insertWeather(weatherResponse)
    }

    override suspend fun getWeatherFromDatabase(): Flow<WeatherResponseEntity> =
        localSource.getCurrentWeather()

    override suspend fun insertFavorite(favoritePlace: FavoritePlace) {
        localSource.insertFavorite(favoritePlace)
    }

    override fun getAllFavorites(): Flow<List<FavoritePlace>> =
        localSource.getAllFavorites()

    override fun deleteFavorite(favoritePlace: FavoritePlace) {
        localSource.deleteFavorite(favoritePlace)
    }
}