package com.raaf.android.searchmovie.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.api.FilmApi
import com.raaf.android.searchmovie.api.FilmFetcher
import com.raaf.android.searchmovie.api.FilmInterceptor
import com.raaf.android.searchmovie.dataModel.Film
import com.raaf.android.searchmovie.dataModel.homeItems.CategoryWithSwipe
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import com.raaf.android.searchmovie.dataModel.homeItems.FilmsCategoryItem
import com.raaf.android.searchmovie.dataModel.rootJSON.SearchByFilters
import com.raaf.android.searchmovie.dataModel.rootJSON.SearchByKeyword
import com.raaf.android.searchmovie.database.CompilationDatabase
import com.raaf.android.searchmovie.database.MyFilmsDatabase
import com.raaf.android.searchmovie.database.top.TopDatabase
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import javax.inject.Inject

//Rename to Mediator later
//get - for Database. set - for UI

private const val TAG = "AppRepository"

class AppRepository @Inject constructor (val context: Context,
                                         val topDatabase: TopDatabase,
                                         val myFilmsDatabase: MyFilmsDatabase,
                                         val compilationDatabase: CompilationDatabase,
                                         val appConverter: AppConverter,
                                         val filmFetcher: FilmFetcher) {

    val typeTop = listOf("TOP_250_BEST_FILMS", "TOP_100_POPULAR_FILMS", "TOP_AWAIT_FILMS")
    val nameTop = listOf(context.getString(R.string.best), context.getString(R.string.popular), context.getString(R.string.await))
    val nameMyFilms = listOf(context.getString(R.string.favorite_movies), context.getString(R.string.watch_later))
    val genre = mapOf("Comedy" to 6, "Fantasy" to 5, "Cartoon" to 14, "Drama" to 8, "Action" to 3)

    init {
        App.appComponent.inject(this)
    }

    private val executor = Executors.newSingleThreadExecutor()

    fun getMovie() {

    }


//  Предназначен для отображения в MyFilmsFragment
//  Change to database later instead to send request result in UI
    fun getMyFilms(filmId: Int) {

    }

    fun setMyFilms() : LiveData<List<FilmSwipeItem>> {
        val dbLiveData = myFilmsDatabase.myFilmsDao().loadAll()
        val listResult = mutableListOf<FilmSwipeItem>()
        val listLiveData = MutableLiveData<List<FilmSwipeItem>>()
        dbLiveData.observeForever { list->
            Log.d(TAG, "lv size = ${list.size}")
            list.let { listResult.addAll(appConverter.parseMovies(list)) }
            Log.d(TAG, "list size = ${listResult.size}")
            listLiveData.value = listResult
        }
        return listLiveData
    }

//  getSearchByKeyword Предназначен для отображения в SearchResultFragment
//  Change to database later instead to send request result in UI
    fun getSearchByKeyword(keyword: String, page: Int) : LiveData<SearchByKeyword> {
        val request = filmFetcher.fetchSearchByKeyword(keyword, page)

        /*отправку в бд
         */

        return request
    }

//    These methods are designed to work with data for a TopFragment

//    Insert all data in TopDatabase
    fun getTop(type: String, page: Int, categoryName: String) : LiveData<Int> {
        val request = filmFetcher.fetchTopMovie(type, page, categoryName)
        return request
    }

    fun setTop() : LiveData<List<FilmSwipeItem>> {
        var dbLiveData1 = topDatabase.topDao().loadAll()
        return dbLiveData1
    }


//    These methods are designed to work with data for a CompilationFragment
    fun getCompilation() {
        for (count in genre) {
            for (it in 1..5) {
                Log.d(TAG, count.key)
                filmFetcher.fetchSearchByFilters(null, arrayOf(count.value), "RATING",
                        "FILM", 6, 10, 2010, 2020, it, true, count.key)
            }
        }
    }

    fun setCompilation() : LiveData<List<FilmSwipeItem>> {
        var dbLiveData = compilationDatabase.compilationDao().loadAll()
        return dbLiveData
    }


//    Query all data in TopDataBase
    /*fun setTop() : LiveData<List<FilmsCategoryItem>> {
        var currentLiveData = MutableLiveData<List<FilmsCategoryItem>>()
        var dbLiveData = topDatabase.topDao().loadAll()
        lateinit var listt: List<FilmSwipeItem>
        dbLiveData.value?.let {
            for (count in it) {
                listt = count.swipeItems
                currentLiveData.value = currentLiveData.value?.plus(FilmsCategoryItem(count.categoryItem.categoryName, listt))
            }
        }
        return currentLiveData
    }*/

    /*private fun getFilmsCategoryItem(nameCategory: String, typeTop: String) {

        val currentPage = MutableLiveData<Int>()
        var pageCount = 0
        val requestLiveData = Transformations.switchMap(currentPage) { page ->
            filmFetcher.fetchTopMovie(typeTop, page) }

        val films = mutableListOf<FilmSwipeItem>()

        var observer = requestLiveData.observeForever(
                Observer { searchByFilters ->
                    Log.e(TAG, "${searchByFilters.pagesCount}")
                    searchByFilters.films.forEach { film -> films.add(parseFilms(film, nameCategory)) }
                    if (pageCount <= 1) pageCount = searchByFilters.pagesCount
                })
        currentPage.value = 1
        Log.e(TAG, pageCount.toString())
        for (count in 2..pageCount){ currentPage.value = count }
        Log.e(TAG, "${films.size}")
        executor.execute { topDatabase.topDao().insert(CategoryWithSwipe(FilmsCategoryItem(nameCategory, films), films))}
        observer = clean()
        Log.e(TAG, "hasActiveObs=${requestLiveData.hasActiveObservers()}, hasAllObs=${requestLiveData.hasObservers()}")
    }

    fun clean() {
        Log.e(TAG, "its done!")
    }*/

    /*private fun getFilmsCategoryItem(nameCategory: String, typeTop: String) {
        val films = mutableListOf<FilmSwipeItem>()
        var pageCount = 0
        var request = filmFetcher.fetchTopMovie(typeTop, 1)
        Log.e(TAG, "Request is                $request")
        Log.e(TAG, "pageCount in request: ${request.pagesCount}")
        request.films.forEach { films.add(parseFilms(it, nameCategory)) }
        pageCount = request.pagesCount
        Log.e(TAG, "pageCount = $pageCount")
        for (count in 2..pageCount) {
            request = filmFetcher.fetchTopMovie(typeTop, count)
            request.films.forEach { films.add(parseFilms(it, nameCategory)) }
        }
        Log.e(TAG, "${films.size}")
        var item = FilmsCategoryItem(nameCategory, films)
        topDatabase.topDao().insert(CategoryWithSwipe(item, films))//Maybe replace item to FCI(nC, listOf())
    }*/

/*
    fun setAwaitTop() {
        val dao = awaitFilmsDatabase.topDao()
        var request = filmFetcher.fetchTopMovie(typeTop[0], 1)
        request.value?.let { dao.insert(it) }
        val pageCount = request.value!!.pagesCount
        for (count in 2..pageCount) {
            request = filmFetcher.fetchTopMovie(typeTop[0], count)
            request.value?.let { dao.insert(it) }
        }
    }*/
/*
    fun setBestTop() {
        val dao = bestFilmDatabase.topDao()
        var request = filmFetcher.fetchTopMovie(typeTop[1], 1)
        request.value?.let { dao.insert(it) }
        val pageCount = request.value!!.pagesCount
        for (count in 2..pageCount) {
            request = filmFetcher.fetchTopMovie(typeTop[1], count)
            request.value?.let { dao.insert(it) }
        }
    }

    fun setPopularTop() {
        val dao = popularFilmDatabase.topDao()
        var request = filmFetcher.fetchTopMovie(typeTop[2], 1)
        request.value?.let { dao.insert(it) }
        val pageCount = request.value!!.pagesCount
        for (count in 2..pageCount) {
            request = filmFetcher.fetchTopMovie(typeTop[2], count)
            request.value?.let { dao.insert(it) }
        }
    }*/
}