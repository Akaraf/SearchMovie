package com.raaf.android.searchmovie.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.raaf.android.searchmovie.dataModel.Movie

@Dao
interface MyFilmsDao : BaseDao<Movie> {

    /*@Transaction
    @Query("SELECT * FROM films")
    fun load() : LiveData<Movie>*/

    @Transaction @Query("SELECT * FROM films")
    fun loadAll() : LiveData<List<Movie>>

    @Transaction @Query("SELECT * FROM films")
    fun getAll() : List<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(movies: List<Movie>)
}