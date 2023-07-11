package com.example.weather.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.RepoInterface

class HomeFragmentViewModelFactory(private val repository: RepoInterface) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass::class.java.isInstance(HomeFragmentViewModel::class.java)) {
            HomeFragmentViewModel(repository) as T
        } else {
            throw IllegalArgumentException("View Model class not found")
        }
    }
}