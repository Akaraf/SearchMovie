package com.raaf.android.searchmovie.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.raaf.android.searchmovie.dataModel.Movie
import com.raaf.android.searchmovie.dataModel.rootJSON.PersonResponse

@Dao
interface PersonDao : BaseDao<PersonResponse> {

    @Transaction
    @Query("SELECT * FROM persons")
    fun loadAll() : LiveData<List<PersonResponse>>

    @Transaction
    @Query("SELECT * FROM persons")
    fun getAll() : List<PersonResponse>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(persons: List<PersonResponse>)

    @Transaction
    @Query("DELETE FROM persons WHERE personId = (:id)")
    fun deleteWithId(id: Int)

    @Transaction
    @Query("SELECT COUNT(personId) FROM persons")
    fun getCount() : Int

    @Transaction @Query("SELECT * FROM persons WHERE personId = (:personId)")
    fun checkItem(personId: Int) : LiveData<PersonResponse?>
}