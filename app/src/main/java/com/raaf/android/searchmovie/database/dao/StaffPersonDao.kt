package com.raaf.android.searchmovie.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.raaf.android.searchmovie.dataModel.Movie
import com.raaf.android.searchmovie.dataModel.StaffPerson
import com.raaf.android.searchmovie.dataModel.rootJSON.PersonResponse


@Dao
interface StaffPersonDao : BaseDao<StaffPerson> {

    @Transaction
    @Query("SELECT * FROM staff_persons")
    fun loadAll() : LiveData<List<StaffPerson>>

    @Transaction
    @Query("SELECT * FROM staff_persons")
    fun getAll() : List<StaffPerson>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(movies: List<StaffPerson>)

    @Transaction @Query("DELETE FROM staff_persons WHERE staffId = (:id)")
    fun deleteWithId(id: Int)

    @Transaction @Query("SELECT COUNT(staffId) FROM staff_persons")
    fun getCount() : Int
}