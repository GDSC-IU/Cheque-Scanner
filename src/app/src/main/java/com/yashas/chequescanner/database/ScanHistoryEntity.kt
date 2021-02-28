package com.yashas.chequescanner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scanHistory")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,

    @ColumnInfo(name = "payee")
    var payeeName: String,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "amount")
    var amount: String,

    @ColumnInfo(name = "date")
    var date: String,

    @ColumnInfo(name = "accountNumber")
    var accountNumber: String
) {
    constructor(
        payeeName: String,
        name: String,
        amount: String,
        date: String,
        accountNumber: String
    ) : this(
        null,
        payeeName,
        name,
        amount,
        date,
        accountNumber
    )
}