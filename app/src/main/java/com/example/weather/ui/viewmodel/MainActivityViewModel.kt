package com.example.weather.ui.viewmodel

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.weather.model.RepoInterface
import com.example.weather.model.pojo.Location
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

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