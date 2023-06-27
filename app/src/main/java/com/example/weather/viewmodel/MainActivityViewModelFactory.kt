package com.example.weather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.home.viewmodel.HomeFragmentViewModel
import com.example.weather.model.RepoInterface

class MainActivityViewModelFactory(private val repository: RepoInterface) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass::class.java.isInstance(MainActivityViewModel::class.java)) {
            MainActivityViewModel(repository) as T
        } else {
            throw IllegalArgumentException("View Model class not found")
        }
    }
}