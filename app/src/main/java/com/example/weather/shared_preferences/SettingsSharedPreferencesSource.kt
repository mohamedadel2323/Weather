package com.example.weather.shared_preferences

interface SettingsSharedPreferencesSource {
    fun setLong(longitude: Float)
    fun setLat(latitude: Float)
    fun getLong(): Float
    fun getLat(): Float
    fun getFirstTime(): Boolean
    fun setFirstTime(first: Boolean)
    fun setLocationOption(way: String)
    fun getLocationOption(): String
    fun setNotificationOption(state: Boolean)
    fun getNotificationOption(): Boolean

    fun getMapFirstTime(): Boolean
    fun setMapFirstTime(first: Boolean)
}