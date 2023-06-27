package com.example.weather.network

import com.example.weather.model.pojo.WeatherResponse
import com.example.weather.model.pojo.WeatherResponseEntity


sealed class ApiState{
    class Success(val data: WeatherResponse?) : ApiState()
    class SuccessOffline(val data: WeatherResponseEntity?) : ApiState()
    class Failure(val msg: String) : ApiState()
    object Loading : ApiState()
}
