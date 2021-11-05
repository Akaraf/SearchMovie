package com.raaf.android.searchmovie.ui.home.swipeFragments.detailCategory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.api.FilmFetcher
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import com.raaf.android.searchmovie.dataModel.rootJSON.PersonResponse
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

    @Inject lateinit var filmFetcher: FilmFetcher
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
                resultLiveData = filmFetcher.setCompilationForDetails(secondType)
            }
            (firstType == F_T_TOP) -> {
                resultLiveData = filmFetcher.setTop()
            }
            (firstType == F_T_MY_FILMS || firstType == F_T_MY_FILMS_ALL) -> {
                resultLiveData = filmFetcher.setMyFilms()
            }
            (firstType == F_T_WATCHED) -> {
                resultLiveData = filmFetcher.setWatched()
            }
            (firstType == F_T_MY_STARS) -> {
                resultLiveDataForPersonResponse = filmFetcher.setMyPerson()
            }
            (firstType == F_T_CATEGORIES) -> {
                resultLiveData = filmFetcher.setCategory()
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
        if (categoryName == favoriteMoviesCategory || categoryName == watchLaterCategory) filmFetcher.deleteMovieFromMyFilmsDb(endId)
        //if (categoryName == historyCategory) filmFetcher.deleteMovieFromHistoryDb(endId)
        if (categoryName == myPersonsCategory) filmFetcher.deletePersonFromMyPersonsDB(endId.toInt())
    }

    fun fetchCategoryFilms(categoryName: String, categoryItemName: String) {
        filmFetcher.getCategoryCache(categoryName, categoryItemName)
    }

    fun clearCategoryCacheDB() {
        filmFetcher.clearCategoryFilmsCacheDb()
    }
}