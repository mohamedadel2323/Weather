package com.example.weather.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.RepoInterface

class FavoritesFragmentViewModelFactory(private val repository : RepoInterface) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass::class.java.isInstance(FavoritesFragmentViewModel::class.java)) {
            FavoritesFragmentViewModel(repository) as T
        } else {
            throw IllegalArgumentException("View Model class not found")
        }
    }
}