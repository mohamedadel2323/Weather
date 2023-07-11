package com.example.weather.data.database

import androidx.room.TypeConverter
import com.example.weather.model.pojo.*
import com.google.gson.Gson

class Converter {

    @TypeConverter
    fun fromAlertsList(alerts: List<Alert>?) = Gson().toJson(alerts)

    @TypeConverter
    fun toAlertsList(alerts: String?): List<Alert>? {
        alerts?.let {
            return Gson().fromJson(it, Array<Alert>::class.java)?.toList()
        }
        return listOf<Alert>()
    }

    @TypeConverter
    fun fromCurrent(current: Current) = Gson().toJson(current)

    @TypeConverter
    fun toCurrent(current: String) =
        Gson().fromJson(current, Current::class.java)

    @TypeConverter
    fun fromDailyList(dailylist: List<Daily>) = Gson().toJson(dailylist)

    @TypeConverter
    fun toDailyList(dailies: String) = Gson().fromJson(dailies, Array<Daily>::class.java).toList()

    @TypeConverter
    fun fromHourlyList(hourlyList: List<Hourly>) = Gson().toJson(hourlyList)

    @TypeConverter
    fun toHourlyList(hourlies: String) =
        Gson().fromJson(hourlies, Array<Hourly>::class.java).toList()

    @TypeConverter
    fun fromMinutelyList(minutelyList: List<Minutely>) = Gson().toJson(minutelyList)

    @TypeConverter
    fun toMinutelyList(minutelies: String) =
        Gson().fromJson(minutelies, Array<Minutely>::class.java).toList()

    @TypeConverter
    fun fromWeatherList(weatherList: List<Weather>) = Gson().toJson(weatherList)

    @TypeConverter
    fun to1weatherList(weathers: String) =
        Gson().fromJson(weathers, Array<Weather>::class.java).toList()

}