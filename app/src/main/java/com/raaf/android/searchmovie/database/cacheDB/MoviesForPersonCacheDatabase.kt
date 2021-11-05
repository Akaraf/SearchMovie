package com.raaf.android.searchmovie.database.cacheDB

import androidx.room.Database
import androidx.room.RoomDatabase
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import com.raaf.android.searchmovie.database.dao.FilmSwipeItemDao

@Database(entities = arrayOf(FilmSwipeItem::class), version = 1, exportSchema = false)
abstract class MoviesForPersonCacheDatabase : RoomDatabase() {

    abstract fun moviesForPersonCacheDao(): FilmSwipeItemDao
}