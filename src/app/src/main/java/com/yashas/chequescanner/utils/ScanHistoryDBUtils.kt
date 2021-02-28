package com.yashas.chequescanner.utils

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room
import com.yashas.chequescanner.database.ScanHistoryDatabase
import com.yashas.chequescanner.database.ScanHistoryEntity

class ScanHistoryDBUtils(val context: Context, val mode: Int, val data: ScanHistoryEntity? = null): AsyncTask<Void, Void, Any>() {
    override fun doInBackground(vararg p0: Void?): Any {
        /*
            1-> ADD to DB
            2-> DELETE from DB
            3-> CLEAR DB
            4-> GET ALL from DB
         */
        val db = Room.databaseBuilder(context, ScanHistoryDatabase::class.java, "scanHistory").build()
        when(mode){
            1->{
                return db.scanHistoryDao().addData(data!!)
            }

            2->{
                return  db.scanHistoryDao().deleteData(data!!)
            }

            3->{
                return db.scanHistoryDao().clearDatabase()
            }

            4->{
                return db.scanHistoryDao().getAll()
            }

            else -> return ""
        }
    }
}