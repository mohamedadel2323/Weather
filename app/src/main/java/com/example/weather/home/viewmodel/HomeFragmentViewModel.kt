package com.example.weather.home.viewmodel

import android.annotation.SuppressLint
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.RepoInterface
import com.example.weather.model.pojo.Location
import com.example.weather.network.ApiState
import com.example.weather.toWeatherResponseEntity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber

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
        fusedClient.lastLocation.addOnSuccessListener {
            _locationStateFlow.value = ApiState.SuccessLocation(Location(it.longitude, it.latitude))
        }
        return locationStateFlow
    }

    fun getWeather(location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getWeather(location).catch {
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
}