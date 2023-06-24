package com.example.weather.model

import com.example.weather.shared_preferences.SettingsSharedPreferencesSource

class Repository(val sharedPreferencesSource: SettingsSharedPreferencesSource) : RepoInterface{

    companion object {
        private var instance: Repository? = null
        fun getInstance(
            sharedPreferencesSource: SettingsSharedPreferencesSource
        ): Repository {
            return instance ?: synchronized(this) {
                val temp = Repository(sharedPreferencesSource)
                instance = temp
                temp
            }
        }
    }

}