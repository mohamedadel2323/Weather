package com.example.weather.ui.viewmodel


import androidx.lifecycle.ViewModel
import com.example.weather.model.RepoInterface

class MainActivityViewModel(private val repository: RepoInterface) : ViewModel() {
    fun setFirstTime() {
        repository.setFirstTime(false)
    }

    fun getFirstTime() = repository.getFirstTime()

    fun getLanguage() = repository.getLanguageOption()

    fun setLocationOption(option: String) {
        repository.setLocationOption(option)
    }
    fun setNotificationOption(option : Boolean){
        repository.setNotificationOption(option)
    }
}