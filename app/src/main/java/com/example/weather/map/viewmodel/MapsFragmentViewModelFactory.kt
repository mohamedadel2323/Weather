package com.example.weather.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.RepoInterface

class MapsFragmentViewModelFactory(private val repository : RepoInterface) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass::class.java.isInstance(MapsFragmentsViewModel::class.java)) {
            MapsFragmentsViewModel(repository) as T
        } else {
            throw IllegalArgumentException("View Model class not found")
        }
    }
}