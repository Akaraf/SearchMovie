package com.raaf.android.searchmovie.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.raaf.android.searchmovie.dataModel.Movie
import com.raaf.android.searchmovie.dataModel.rootJSON.PersonResponse

//использую аннотацию Transaction для Query, тк это гарантирует возврат консистентных двнных
@Dao
interface MyFilmsDao : BaseDao<Movie> {

    /*@Transaction
    @Query("SELECT * FROM films")
    fun load() : LiveData<Movie>*/

    @Transaction @Query("SELECT * FROM myfilms")
    fun loadAll() : LiveData<List<Movie>>

    @Transaction @Query("SELECT * FROM myfilms")
    fun getAll() : List<Movie>

    @Transaction @Query("DELETE FROM myfilms WHERE DBId = ( SELECT Min(DBId) FROM myfilms)")
    fun deleteForHistory()

    @Transaction @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertForWatched(movie:Movie)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(movies: List<Movie>)

    @Transaction @Query("DELETE FROM myfilms WHERE filmId = (:id)")
    fun deleteWithId(id: Long)

    @Transaction @Query("DELETE FROM myfilms WHERE DBId = (:dbId)")
    fun deleteWithDbId(dbId: Long)

    @Transaction @Query("DELETE FROM myfilms WHERE endsId = (:endsId)")
    fun deleteWithEndsId(endsId: String)

    @Transaction @Query("SELECT COUNT(filmId) FROM myfilms")
    fun getCount() : Int

    @Transaction @Query("SELECT * FROM myfilms WHERE endsId = (:endsId)")
    fun checkItemLV(endsId: String) : LiveData<Movie?>

    @Transaction @Query("SELECT * FROM myfilms WHERE endsId = (:endsId)")
    suspend fun checkItem(endsId: String) : Movie?
}