package com.example.weather.home.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.RepoInterface
import com.example.weather.model.pojo.Location
import com.example.weather.network.ApiState
import com.example.weather.toWeatherResponseEntity
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class HomeFragmentViewModel(private val repository: RepoInterface) : ViewModel() {
    private var _locationStateFlow = MutableStateFlow<ApiState>(ApiState.Loading)
    val locationStateFlow: StateFlow<ApiState>
        get() = _locationStateFlow


    private var _offlineWeatherStateFlow = MutableStateFlow<ApiState>(ApiState.Loading)
    val offlineWeatherStateFlow: StateFlow<ApiState>
        get() = _offlineWeatherStateFlow

    private var _weatherResponseStateFlow = MutableStateFlow<ApiState>(ApiState.Loading)
    val weatherResponseStateFlow: StateFlow<ApiState>
        get() = _weatherResponseStateFlow


    @SuppressLint("MissingPermission")
    fun requestNewLocationData(fusedClient: FusedLocationProviderClient): StateFlow<ApiState> {
        fusedClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                _locationStateFlow.value =
                    ApiState.SuccessLocation(Location(location.longitude, location.latitude))
            } else {
                _locationStateFlow.value = ApiState.Failure("Location not available")
            }

        }
        return locationStateFlow
    }

    fun getWeather(location: Location, unit: String, language: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getWeather(location, unit, language).catch {
                    _weatherResponseStateFlow.emit(ApiState.Failure(it.localizedMessage))
                }.collect {
                    if (it.isSuccessful) {
                        _weatherResponseStateFlow.emit(ApiState.Success(it.body()))

                        it.body()
                            ?.let { it1 -> repository.insertWeatherToDatabase(it1.toWeatherResponseEntity()) }

                    } else {
                        _weatherResponseStateFlow.emit(ApiState.Failure(it.code().toString()))
                    }
                }
            } catch (e: SocketTimeoutException) {
                _weatherResponseStateFlow.emit(ApiState.Failure(e.localizedMessage))
            }
        }
    }

    fun getWeatherFromDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getWeatherFromDatabase().collect {
                _offlineWeatherStateFlow.emit(ApiState.SuccessOffline((it)))
            }
        }
    }

    fun getLocationOption() = repository.getLocationOption()

    fun getMapFirstTime() = repository.getMapFirstTime()

    fun setMapFirstTime() {
        repository.setMapFirstTime(false)
    }

    fun setLatitude(latitude: Double) {
        repository.setLatitude(latitude)
    }

    fun setLongitude(longitude: Double) {
        repository.setLongitude(longitude)
    }

    fun getLatitude() = repository.getLatitude()

    fun getLongitude() = repository.getLongitude()

    fun getLanguageOption() = repository.getLanguageOption()

    fun getTemperatureOption() = repository.getTemperatureOption()

    fun setDetails(isDetails: Boolean) {
        repository.setDetails(isDetails)
    }

    fun getDetails() = repository.getDetails()

}