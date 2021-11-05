package com.raaf.android.searchmovie.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.raaf.android.searchmovie.dataModel.rootJSON.PersonResponse
import com.raaf.android.searchmovie.database.dao.PersonDao
import com.raaf.android.searchmovie.database.dao.StaffPersonDao

@Database(entities = arrayOf(PersonResponse::class), version = 1, exportSchema = false)
abstract class MyPersonsDatabase : RoomDatabase() {

    abstract fun myPersonsDao() : PersonDao
}