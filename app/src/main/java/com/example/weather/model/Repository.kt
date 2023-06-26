package com.example.weather.model

import com.example.weather.model.pojo.Location
import com.example.weather.model.pojo.WeatherResponse
import com.example.weather.network.RemoteSource
import com.example.weather.shared_preferences.SettingsSharedPreferencesSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response

class Repository(
    private val sharedPreferencesSource: SettingsSharedPreferencesSource,
    private val remoteSource: RemoteSource
) : RepoInterface {

    companion object {
        private var instance: Repository? = null
        fun getInstance(
            sharedPreferencesSource: SettingsSharedPreferencesSource, remoteSource: RemoteSource
        ): Repository {
            return instance ?: synchronized(this) {
                val temp = Repository(sharedPreferencesSource, remoteSource)
                instance = temp
                temp
            }
        }
    }

    override fun getFirstTime() = sharedPreferencesSource.getFirstTime()

    override fun setFirstTime(first: Boolean) {
        sharedPreferencesSource.setFirstTime(first)
    }

    override fun getLatitude(): Double = sharedPreferencesSource.getLat().toDouble()

    override fun setLatitude(latitude: Double) {
        sharedPreferencesSource.setLat(latitude.toFloat())
    }

    override fun getLongitude(): Double = sharedPreferencesSource.getLong().toDouble()

    override fun setLongitude(longitude: Double) {
        sharedPreferencesSource.setLong(longitude.toFloat())
    }

    override suspend fun getWeather(location: Location): Flow<Response<WeatherResponse>> {
        return flowOf(remoteSource.getWeather(location))
    }


}