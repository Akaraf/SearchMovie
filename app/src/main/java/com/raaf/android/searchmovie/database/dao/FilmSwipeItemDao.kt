package com.raaf.android.searchmovie.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.raaf.android.searchmovie.dataModel.homeItems.CategoryWithSwipe
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import com.raaf.android.searchmovie.dataModel.homeItems.FilmsCategoryItem

@Dao
interface FilmSwipeItemDao : BaseDao<FilmSwipeItem>{

    @Transaction @Query("SELECT * FROM filmstop WHERE parent_id = (:parent_id)")
    fun loadCategoryByName(parent_id: String) : LiveData<List<FilmSwipeItem>>

//    @Transaction @Query("SELECT * FROM filmstop WHERE parent_id = (:parent_id)")
//    fun getCategoryByName(parent_id: String) : List<FilmSwipeItem>

    @Transaction @Query("SELECT * FROM filmstop")
    fun loadAll() : LiveData<List<FilmSwipeItem>>

    @Query("SELECT * FROM filmstop WHERE forHome = (:booleanFlag)")
    fun loadForCompilation(booleanFlag: Boolean) : LiveData<List<FilmSwipeItem>>

    @Transaction @Query("SELECT * FROM filmstop")
    fun getAll() : List<FilmSwipeItem>

    @Transaction @Query("SELECT COUNT(filmId) FROM filmstop")
    fun getCount() : Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(filmSwipeItem: List<FilmSwipeItem>)

    /*@Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(filmSwipeItem: LiveData<List<FilmSwipeItem>>)*/

    /*@Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(filmsCategoryItem: FilmsCategoryItem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(filmSwipeItem: FilmSwipeItem)

    @Transaction
    fun insert(categoryWithSwipe: CategoryWithSwipe) {
        insert(categoryWithSwipe.categoryItem)
        for (count in categoryWithSwipe.swipeItems) {
            insert(count)
        }
    }

    @Query("SELECT * FROM searchbyfilters")
    fun getAll() : LiveData<List<SearchByFilters>>

    @Query("SELECT * FROM searchbyfilters WHERE id = :page")
    fun getByPage(page: Int) : LiveData<SearchByFilters?>*/
}