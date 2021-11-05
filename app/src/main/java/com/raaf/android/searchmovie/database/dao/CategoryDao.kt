package com.raaf.android.searchmovie.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.raaf.android.searchmovie.dataModel.CategoryItem
import com.raaf.android.searchmovie.dataModel.rootJSON.PersonResponse

@Dao
interface CategoryDao : BaseDao<CategoryItem> {

    @Transaction
    @Query("SELECT * FROM categories")
    fun loadAll() : LiveData<List<CategoryItem>>

    @Query("SELECT * FROM categories WHERE categoryName = (:categoryName)")
    fun loadByCategoryName(categoryName: String) : LiveData<List<CategoryItem>>

    @Transaction
    @Query("SELECT * FROM categories")
    fun getAll() : List<CategoryItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(categories: List<CategoryItem>)

    @Transaction
    @Query("DELETE FROM categories WHERE itemName = (:itemName)")
    fun deleteWithId(itemName: String)

    @Transaction
    @Query("SELECT COUNT(categoryName) FROM categories")
    fun getCount() : Int

    @Transaction
    @Query("SELECT COUNT(categoryName) FROM categories WHERE categoryName = (:categoryName)")
    fun checkItem(categoryName: String) : Int
}