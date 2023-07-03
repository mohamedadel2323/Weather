package com.example.weather.model.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoritePlace(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val placeName: String,
    val latitude: Double,
    val longitude: Double,
):java.io.Serializable