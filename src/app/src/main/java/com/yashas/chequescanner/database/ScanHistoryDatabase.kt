package com.yashas.chequescanner.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ScanHistoryEntity::class], version = 1)
abstract class ScanHistoryDatabase: RoomDatabase() {
    abstract fun scanHistoryDao(): ScanHistoryDao
}