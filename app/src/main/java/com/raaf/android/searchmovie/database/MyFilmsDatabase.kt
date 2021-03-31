package com.raaf.android.searchmovie.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.raaf.android.searchmovie.dataModel.Movie
import com.raaf.android.searchmovie.database.dao.MyFilmsDao

@Database(entities = arrayOf(Movie::class), version = 1, exportSchema = false)
abstract class MyFilmsDatabase : RoomDatabase() {

    abstract fun myFilmsDao(): MyFilmsDao
}