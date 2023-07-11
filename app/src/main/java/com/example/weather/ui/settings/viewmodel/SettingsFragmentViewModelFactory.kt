package com.example.weather.ui.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.RepoInterface

class SettingsFragmentViewModelFactory(private val repository: RepoInterface) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass::class.java.isInstance(SettingsFragmentViewModel::class.java)) {
            SettingsFragmentViewModel(repository) as T
        } else {
            throw IllegalArgumentException("View Model class not found")
        }
    }

}