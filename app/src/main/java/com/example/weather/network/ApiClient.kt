package com.example.weather.network

import com.example.weather.model.pojo.Location
import com.example.weather.model.pojo.WeatherResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

object ApiClient : RemoteSource {
    override suspend fun getWeather(location: Location) : Response<WeatherResponse> {
        return Api.apiService.getWeather(location.latitude , location.longitude)
    }
}

object RetrofitHelper {
    var gson: Gson = GsonBuilder().create()
    var retrofitInstance: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}

object Api {
    val apiService: ApiService by lazy {
        RetrofitHelper.retrofitInstance.create(ApiService::class.java)
    }
}