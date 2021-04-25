package com.raaf.android.searchmovie.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonElement
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.dataModel.Frame
import com.raaf.android.searchmovie.dataModel.Trailer
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import com.raaf.android.searchmovie.dataModel.rootJSON.*
import com.raaf.android.searchmovie.database.CompilationDatabase
import com.raaf.android.searchmovie.database.MyFilmsDatabase
import com.raaf.android.searchmovie.database.top.TopDatabase
import com.raaf.android.searchmovie.repository.AppConverter
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import javax.inject.Inject

private var TAG = "FilmFetcher"

class FilmFetcher @Inject constructor(val topDatabase: TopDatabase,
                                      val myFilmsDatabase: MyFilmsDatabase,
                                      val compilationDatabase: CompilationDatabase,
                                      val appConverter: AppConverter,) {

    private val filmApi: FilmApi

    init {
        App.appComponent.inject(this)

        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(FilmInterceptor())
                .build()

        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://kinopoiskapiunofficial.tech/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

        filmApi = retrofit.create(FilmApi::class.java)
    }

    private val executor = Executors.newSingleThreadExecutor()
    val topDao = topDatabase.topDao()
    val myFilmsDao = myFilmsDatabase.myFilmsDao()
    val compilationDao = compilationDatabase.compilationDao()

    //  Search Movie By Id(1)
    fun fetchMovie(id: Int, appendToResponse: Array<String>, isAddFromDB: Boolean, parent: String): LiveData<MovieById> {
        val responseLiveData: MutableLiveData<MovieById> = MutableLiveData()
        val searchesRequest: Call<MovieById> = filmApi.fetchMovie(id, appendToResponse)

        searchesRequest.enqueue(object : Callback<MovieById> {
            override fun onFailure(call: Call<MovieById>, t: Throwable) {
                Log.e(TAG, "Failed response", t)
            }

            override fun onResponse(call: Call<MovieById>, response: Response<MovieById>) {
                val filmResponse: MovieById? = response.body()
                responseLiveData.value = filmResponse
                Log.d(TAG, "${response.body().toString()}")
                if (isAddFromDB && filmResponse != null) {
                    executor.execute { myFilmsDao.insert(appConverter.parseMovieByIdToMovie(filmResponse, parent)) }
                }
            }
        })
        return responseLiveData
    }

    fun setMyFilms() : LiveData<List<FilmSwipeItem>> {
        var dbLiveData = myFilmsDao.loadAll()
        var list = mutableListOf<FilmSwipeItem>()
        Log.d(TAG, "dbLD value size = ${dbLiveData.value?.size ?:0}")
        dbLiveData.value?.let { list.addAll(appConverter.parseMoviesToFilmSwipeItem(it)) }
        Log.d(TAG, "list size = ${list.size}")
        var listLiveData = MutableLiveData<List<FilmSwipeItem>>()
        listLiveData.value = list
        return listLiveData
    }

    fun deleteMyFilms() {
        executor.execute { myFilmsDao.delete() }
    }

    // Frames by Film ID(2)
    fun fetchFrames(id: Int): LiveData<List<Frame>> {
        val responseLiveData: MutableLiveData<List<Frame>> = MutableLiveData()
        val searchesRequest: Call<FramesByFilmId> = filmApi.fetchFrames(id)

        searchesRequest.enqueue(object : Callback<FramesByFilmId> {
            override fun onFailure(call: Call<FramesByFilmId>, t: Throwable) {
                Log.e(TAG, "Failed response", t)
            }

            override fun onResponse(call: Call<FramesByFilmId>, response: Response<FramesByFilmId>) {
                val filmResponse: FramesByFilmId? = response.body()
                responseLiveData.value = appConverter.parseFramesToFrame(filmResponse!!.frames)
                Log.d(TAG, "${response.body().toString()}")
                if (responseLiveData.value != null) {
                    Log.d(TAG, "${responseLiveData.value!!.size}")
                }
            }
        })
        return responseLiveData
    }

    //    Трейлеры и тизеры по фильму(3)
    fun fetchTrailers(id: Int): LiveData<List<Trailer>> {
        val responseLiveData: MutableLiveData<List<Trailer>> = MutableLiveData()
        val searchesRequest: Call<TrailerResponse> = filmApi.fetchVideos(id)

        searchesRequest.enqueue(object : Callback<TrailerResponse> {
            override fun onFailure(call: Call<TrailerResponse>, t: Throwable) {
                Log.e(TAG, "Failed response", t)
            }

            override fun onResponse(call: Call<TrailerResponse>, response: Response<TrailerResponse>) {
                val filmResponse: TrailerResponse? = response.body()
                if (filmResponse != null) {
                    responseLiveData.value = filmResponse.trailers
                    Log.d(TAG, "${responseLiveData.value!!.size}")
                }
            }
        })
        return responseLiveData
    }

    //  Search By Keyword(6)
    fun fetchSearchByKeyword(keyword: String, page: Int) : LiveData<SearchByKeyword> {
        var responseResult: MutableLiveData<SearchByKeyword> = MutableLiveData()
        val searchesRequest: Call<SearchByKeyword> = filmApi.fetchSearchByKeyword(keyword, page)

        searchesRequest.enqueue(object : Callback<SearchByKeyword> {
            override fun onFailure(call: Call<SearchByKeyword>, t: Throwable) {
                Log.e(TAG, "Failed response", t)
            }

            override fun onResponse(call: Call<SearchByKeyword>, response: Response<SearchByKeyword>) {
                val searchResponse: SearchByKeyword? = response.body()
                    responseResult.value = searchResponse
                Log.d(TAG, "${response.body().toString()}")
            }
        })
        return responseResult
    }

    //    Filters(7)
    fun fetchFilters(): LiveData<FiltersResponse> {
        val responseLiveData: MutableLiveData<FiltersResponse> = MutableLiveData()
        val filtersRequest: Call<FiltersResponse> = filmApi.fetchFilters()

        filtersRequest.enqueue(object : Callback<FiltersResponse> {
            override fun onFailure(call: Call<FiltersResponse>, t: Throwable) {
                Log.e(TAG, "Failed response", t)
            }

            override fun onResponse(call: Call<FiltersResponse>, response: Response<FiltersResponse>) {
                Log.d(TAG, "${response.body()}")
//                val s = response.toString()
            }
        })
        return responseLiveData
    }

//    Example: https://kinopoiskapiunofficial.tech/api/v2.1/films/search-by-filters?genre=6&order=RATING&type=FILM&ratingFrom=6&ratingTo=10&yearFrom=2010&yearTo=2020&page=1
    //  Search By Filters(8)
    fun fetchSearchByFilters(country: Array<Int>?,
                             genre: Array<Int>?,
                             order: String,
                             type: String,
                             ratingFrom: Int,
                             ratingTo: Int,
                             yearFrom: Int,
                             yearTo: Int,
                             page: Int,
                             isAddFromDB: Boolean,
                             parent: String): LiveData<SearchByFilters> {
        val responseLiveData: MutableLiveData<SearchByFilters> = MutableLiveData()
        val searchByFiltersRequest: Call<SearchByFilters> = filmApi.fetchSearchByFilters(country, genre, order, type, ratingFrom, ratingTo, yearFrom, yearTo, page)

        searchByFiltersRequest.enqueue(object : Callback<SearchByFilters> {
            override fun onFailure(call: Call<SearchByFilters>, t: Throwable) {
                Log.e(TAG, "Failed response", t)
            }

            override fun onResponse(call: Call<SearchByFilters>, response: Response<SearchByFilters>) {
                val filmResponse = response.body()
                responseLiveData.value = filmResponse
                Log.d(TAG, "${response.body().toString()}")
                if (isAddFromDB && filmResponse != null) {
                    executor.execute { compilationDao.insert(appConverter.parseFilmsToFilmSwipeItem(filmResponse.films, parent, filmResponse.pagesCount)) }
                }
            }
        })
        return responseLiveData
    }

    //      Top Films of Kinopoisk(9)
    fun fetchTopMovie(type: String, page: Int, categoryName: String) : LiveData<Int> {
        lateinit var responseLiveData: List<FilmSwipeItem>
        val searchesRequest: Call<SearchByFilters> = filmApi.fetchTop(type, page)
        val pagesCount: MutableLiveData<Int> = MutableLiveData()

        searchesRequest.enqueue(object : Callback<SearchByFilters> {
            override fun onFailure(call: Call<SearchByFilters>, t: Throwable) {
                Log.e(TAG, "Failed response", t)
            }

            override fun onResponse(call: Call<SearchByFilters>, response: Response<SearchByFilters>) {
                val filmResponse: SearchByFilters? = response.body()
                if (filmResponse != null) {
                    pagesCount.value = filmResponse.pagesCount
                    responseLiveData = appConverter.parseFilmsToFilmSwipeItem(filmResponse.films, categoryName, filmResponse.pagesCount)
                }
                Log.d(TAG, "ResponseLiveDataResult                          $responseLiveData")
                Log.d(TAG, "${response.body()}  page=${response.body()?.pagesCount}")
                executor.execute { topDao.insert(responseLiveData) }
            }
        })
        return pagesCount
    }

    //  Digital Releases(10)
    fun fetchReleases(year: Int, month: String, page: Int): LiveData<DigitalReleases> {
        val responseLiveData: MutableLiveData<DigitalReleases> = MutableLiveData()
        val releasesRequest: Call<DigitalReleases> = filmApi.fetchReleases(year, month, page)

        releasesRequest.enqueue(object : Callback<DigitalReleases> {
            override fun onFailure(call: Call<DigitalReleases>, t: Throwable) {
                Log.e(TAG, "Failed response", t)
            }

            override fun onResponse(call: Call<DigitalReleases>, response: Response<DigitalReleases>) {
                val filmResponse = response.body()
                if (filmResponse != null) {

                }
                Log.e(TAG, "${response.body().toString()}")
            }
        })
        return responseLiveData
    }

    //    Staff By Id(13)
    fun fetchStaffById(filmId: Int): LiveData<List<String>> {
        val responseLiveData: MutableLiveData<List<String>> = MutableLiveData()
        val releasesRequest: Call<JsonElement> = filmApi.fetchStaffByPersonId(filmId)

        releasesRequest.enqueue(object : Callback<JsonElement> {
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.e(TAG, "Failed response", t)
            }

            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.e(TAG, "${response.body().toString()}")
            }
        })
        return responseLiveData
    }
}