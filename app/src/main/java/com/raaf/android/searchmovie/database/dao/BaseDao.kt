package com.raaf.android.searchmovie.database.dao

import androidx.room.*

@Dao
interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(obj: T)

    @Delete
    fun delete(obj: T)

    @Delete
    fun delete(vararg obj: T)
}