package com.example.weather.database

import android.content.Context
import androidx.room.*
import com.example.weather.model.pojo.WeatherResponseEntity

@Database(entities = [WeatherResponseEntity::class], version = 2)
@TypeConverters(Converter::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun getWeatherDAo(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null
        fun getInstance(ctx: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext, WeatherDatabase::class.java, "weather_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}