package com.example.weather.ui.settings.viewmodel

import androidx.lifecycle.ViewModel
import com.example.weather.model.RepoInterface

class SettingsFragmentViewModel(private val repository: RepoInterface) : ViewModel() {

    fun setLocationOption(option: String) {
        repository.setLocationOption(option)
    }

    fun getLocationOption() = repository.getLocationOption()

    fun setNotificationOption(option: Boolean) {
        repository.setNotificationOption(option)
    }

    fun getNotificationOption() = repository.getNotificationOption()

    fun setLanguageOption(language: String) {
        repository.setLanguageOption(language)
    }

    fun getLanguageOption() = repository.getLanguageOption()

    fun setUnitOption(option: String) {
        repository.setUnitOption(option)
    }

    fun getUnitOption() = repository.getUnitOption()

    fun setTemperatureOption(temp: String) {
        repository.setTemperatureOption(temp)
    }

    fun getTemperatureOption() = repository.getTemperatureOption()
}