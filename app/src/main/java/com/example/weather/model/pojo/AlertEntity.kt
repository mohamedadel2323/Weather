package com.example.weather.model.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alert")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var startTime: Long,
    var endTime: Long,
    val longitude: Double,
    val latitude: Double
)