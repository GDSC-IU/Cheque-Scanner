package com.yashas.chequescanner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScanHistoryDao {
    @Insert
    fun addData(scanHistoryEntity: ScanHistoryEntity)

    @Delete
    fun deleteData(scanHistoryEntity: ScanHistoryEntity)

    @Query("DELETE FROM scanHistory")
    fun clearDatabase()

    @Query("SELECT * FROM scanHistory")
    fun getAll(): List<ScanHistoryEntity>
}