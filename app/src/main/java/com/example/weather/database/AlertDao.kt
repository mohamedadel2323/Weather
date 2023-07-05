package com.example.weather.database

import androidx.room.*
import com.example.weather.model.pojo.AlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertEntity)

    @Query("SELECT * FROM alert")
    fun getAllAlerts(): Flow<List<AlertEntity>>

    @Delete
    suspend fun deleteAlert(alert: AlertEntity)
}