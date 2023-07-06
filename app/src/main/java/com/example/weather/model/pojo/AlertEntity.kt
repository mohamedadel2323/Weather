package com.example.weather.model.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "alert")
data class AlertEntity(
    @PrimaryKey
    var id: UUID = UUID.fromString("c9a646d7-ff7a-4d1e-8b3c-3f1a22e6e3ef"),
    var startTime: Long,
    var endTime: Long,
    val longitude: Double,
    val latitude: Double
)