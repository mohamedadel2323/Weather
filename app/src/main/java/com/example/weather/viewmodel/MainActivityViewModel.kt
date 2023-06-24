package com.example.weather.viewmodel

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.weather.model.pojo.Location
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

class MainActivityViewModel : ViewModel() {
    private var _locationStateFlow = MutableStateFlow<Location>(Location())
    val locationStateFlow: StateFlow<Location>
        get() = _locationStateFlow


    @SuppressLint("MissingPermission")
    fun requestNewLocationData(fusedClient: FusedLocationProviderClient): StateFlow<Location> {
        val locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000
            maxWaitTime = 7000
        }


        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                Timber.e(locationResult?.lastLocation.toString())
                locationResult?.let { it ->
                    var longitude = it.lastLocation.longitude
                    var latitude = it.lastLocation.latitude
                    _locationStateFlow.value = Location(longitude, latitude)
                    Timber.e("onLocationResult: ${it.lastLocation.longitude}")
                }

            }
        }

        fusedClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        return locationStateFlow
    }


}