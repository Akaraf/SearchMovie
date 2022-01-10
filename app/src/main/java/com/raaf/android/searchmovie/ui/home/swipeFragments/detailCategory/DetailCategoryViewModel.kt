package com.raaf.android.searchmovie.ui.home.swipeFragments.detailCategory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import com.raaf.android.searchmovie.dataModel.rootJSON.PersonResponse
import com.raaf.android.searchmovie.repository.FilmRepo
import javax.inject.Inject

private const val TAG = "DetailCategoryViewModel"
private const val F_T_COMPILATION = "Compilation"
private const val F_T_TOP = "Top"
private const val F_T_MY_FILMS = "My films"
private const val F_T_WATCHED = "Watched films"
private const val F_T_MY_FILMS_ALL = "my films"
private const val F_T_MY_STARS = "my stars"
private const val F_T_CATEGORIES = "Categories"

class DetailCategoryViewModel : ViewModel() {

    @Inject lateinit var repository: FilmRepo
    var resultLiveData: LiveData<List<FilmSwipeItem>>? = null
    var resultLiveDataForPersonResponse: LiveData<MutableList<PersonResponse>>? = null
    private var favoriteMoviesCategory = ""
    private var watchLaterCategory = ""
    private var historyCategory = ""
    private var myPersonsCategory = ""

    init {
        App.appComponent.inject(this)
    }

    fun typeCategory(firstType: String, secondType: String) {
        Log.e(TAG, "ft:$firstType, st:$secondType")
        when {
            (firstType == F_T_COMPILATION) -> {
                resultLiveData = repository.setCompilationForDetails(secondType)
            }
            (firstType == F_T_TOP) -> {
                resultLiveData = repository.setTop()
            }
            (firstType == F_T_MY_FILMS || firstType == F_T_MY_FILMS_ALL) -> {
                resultLiveData = repository.setMyFilms()
            }
            (firstType == F_T_WATCHED) -> {
                resultLiveData = repository.setWatched()
            }
            (firstType == F_T_MY_STARS) -> {
                resultLiveDataForPersonResponse = repository.setMyPerson()
            }
            (firstType == F_T_CATEGORIES) -> {
                resultLiveData = repository.setCategory()
            }
        }
    }

    fun provideCategoryNames(listNames: List<String>) {
        favoriteMoviesCategory = listNames[0]
        watchLaterCategory = listNames[1]
        historyCategory = listNames[2]
        myPersonsCategory = listNames[3]
    }

    fun deleteFromDB(endId: String, categoryName: String) {
        if (categoryName == favoriteMoviesCategory || categoryName == watchLaterCategory) repository.deleteMovieFromMyFilmsDb(endId)
        //if (categoryName == historyCategory) filmFetcher.deleteMovieFromHistoryDb(endId)
        if (categoryName == myPersonsCategory) repository.deletePersonFromMyPersonsDB(endId.toInt())
    }

    fun fetchCategoryFilms(categoryName: String, categoryItemName: String) {
        repository.getCategoryCache(categoryName, categoryItemName)
    }

    fun clearCategoryCacheDB() {
        repository.clearCategoryFilmsCacheDb()
    }
}