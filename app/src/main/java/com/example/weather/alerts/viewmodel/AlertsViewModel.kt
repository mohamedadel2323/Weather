package com.example.weather.alerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.RepoInterface
import com.example.weather.model.pojo.AlertEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlertsViewModel(private val repository: RepoInterface) : ViewModel() {

    private var _alertsStateFlow = MutableStateFlow<List<AlertEntity>>(listOf())
    val alertsStateFlow: StateFlow<List<AlertEntity>>
        get() = _alertsStateFlow

    fun insertAlert(alertEntity: AlertEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAlert(alertEntity)
        }
    }

    fun deleteAlert(alertEntity: AlertEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAlert(alertEntity)
        }
    }

    fun getAllAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllAlerts().collect {
                _alertsStateFlow.emit(it)
            }
        }
    }

    fun getLatitude() = repository.getLatitude()

    fun getLongitude() = repository.getLongitude()

    fun setNotificationOption(isEnabled: Boolean) {
        repository.setNotificationOption(isEnabled)
    }

    fun getNotificationOption() = repository.getNotificationOption()
}