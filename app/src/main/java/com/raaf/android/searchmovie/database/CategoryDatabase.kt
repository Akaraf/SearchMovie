package com.raaf.android.searchmovie.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.raaf.android.searchmovie.dataModel.CategoryItem
import com.raaf.android.searchmovie.database.dao.CategoryDao

@Database(entities = arrayOf(CategoryItem::class), version = 1, exportSchema = false)
abstract class CategoryDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
}