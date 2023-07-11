package com.example.weather.data.shared_preferences

class FakeSettingsSharedPreferences(
    private var longitude: Float = 0f,
    private var initialTime: Boolean = false
) : SettingsSharedPreferencesSource {
    override fun setLong(longitude: Float) {
        this.longitude = longitude
    }

    override fun setLat(latitude: Float) {
        TODO("Not yet implemented")
    }

    override fun getLong(): Float = longitude

    override fun getLat(): Float {
        TODO("Not yet implemented")
    }

    override fun getFirstTime(): Boolean = initialTime

    override fun setFirstTime(first: Boolean) {
        initialTime = first
    }

    override fun setLocationOption(way: String) {
        TODO("Not yet implemented")
    }

    override fun getLocationOption(): String {
        TODO("Not yet implemented")
    }

    override fun setNotificationOption(state: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getNotificationOption(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getMapFirstTime(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setMapFirstTime(first: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getLanguageOption(): String? {
        TODO("Not yet implemented")
    }

    override fun setLanguageOption(state: String) {
        TODO("Not yet implemented")
    }

    override fun setUnitOption(option: String) {
        TODO("Not yet implemented")
    }

    override fun getUnitOption(): String? {
        TODO("Not yet implemented")
    }

    override fun setTemperatureOption(temp: String) {
        TODO("Not yet implemented")
    }

    override fun getTemperatureOption(): String? {
        TODO("Not yet implemented")
    }

    override fun setMapFavorite(isFavorite: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getMapFavorite(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getDetails(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setDetails(isDetails: Boolean) {
        TODO("Not yet implemented")
    }

}