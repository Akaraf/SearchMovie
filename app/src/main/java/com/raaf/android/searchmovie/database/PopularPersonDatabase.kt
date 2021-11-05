package com.raaf.android.searchmovie.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.raaf.android.searchmovie.dataModel.StaffPerson
import com.raaf.android.searchmovie.database.dao.StaffPersonDao

@Database(entities = arrayOf(StaffPerson::class), version = 1, exportSchema = false)
abstract class PopularPersonDatabase : RoomDatabase() {

    abstract fun popularPersonDao() : StaffPersonDao
}