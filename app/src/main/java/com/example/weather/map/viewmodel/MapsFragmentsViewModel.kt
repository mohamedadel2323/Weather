package com.example.weather.map.viewmodel

import androidx.lifecycle.ViewModel
import com.example.weather.model.RepoInterface

class MapsFragmentsViewModel(private val repository: RepoInterface) : ViewModel() {
    fun setLongitude(longitude: Double) {
        repository.setLongitude(longitude)
    }

    fun setLatitude(latitude: Double) {
        repository.setLatitude(latitude)
    }
}