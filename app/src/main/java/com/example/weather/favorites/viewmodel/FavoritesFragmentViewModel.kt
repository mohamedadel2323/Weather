package com.example.weather.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.RepoInterface
import com.example.weather.model.pojo.FavoritePlace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoritesFragmentViewModel(private val repository: RepoInterface) : ViewModel() {

    private var _favoritesStateFlow = MutableStateFlow<List<FavoritePlace>>(listOf())
    val favoritesStateFlow: StateFlow<List<FavoritePlace>>
        get() = _favoritesStateFlow

    fun setMapFavorite(isFavorite: Boolean) {
        repository.setMapFavorite(isFavorite)
    }

    fun getAllFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllFavorites().collect {
                _favoritesStateFlow.emit(it)
            }
        }
    }

    fun deleteFavorite(favoritePlace: FavoritePlace) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFavorite(favoritePlace)
        }
    }
}