package com.example.weather.ui.alerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.RepoInterface

class AlertsViewModelFactory(private val repository: RepoInterface) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass::class.java.isInstance(AlertsViewModel::class.java)) {
            AlertsViewModel(repository) as T
        } else {
            throw IllegalArgumentException("View Model class not found")
        }
    }
}