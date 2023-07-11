package com.example.weather.ui.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.RepoInterface
import com.example.weather.model.pojo.FavoritePlace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapsFragmentsViewModel(private val repository: RepoInterface) : ViewModel() {
    fun setLongitude(longitude: Double) {
        repository.setLongitude(longitude)
    }

    fun setLatitude(latitude: Double) {
        repository.setLatitude(latitude)
    }

    fun getMapFavorite() = repository.getMapFavorite()

    fun setMapFavorite(isFavorite: Boolean) {
        repository.setMapFavorite(isFavorite)
    }

    fun addFavoritePlace(favoritePlace: FavoritePlace) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertFavorite(favoritePlace)
        }
    }
}