package com.example.weather.database

import androidx.room.*
import com.example.weather.model.pojo.AlertEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface AlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertEntity)

    @Query("SELECT * FROM alert")
    fun getAllAlerts(): Flow<List<AlertEntity>>

    @Delete
    suspend fun deleteAlert(alert: AlertEntity)

    @Query("DELETE FROM alert WHERE id = :uuid")
    suspend fun deleteAlertByUuid(uuid: UUID)
}