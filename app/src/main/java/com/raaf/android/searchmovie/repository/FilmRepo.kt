package com.raaf.android.searchmovie.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.api.FilmApi
import com.raaf.android.searchmovie.api.FilmInterceptor
import com.raaf.android.searchmovie.api.FilmWebService
import com.raaf.android.searchmovie.dataModel.*
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import com.raaf.android.searchmovie.dataModel.rootJSON.*
import com.raaf.android.searchmovie.database.*
import com.raaf.android.searchmovie.database.cacheDB.CategoryFilmsCacheDatabase
import com.raaf.android.searchmovie.database.cacheDB.MoviesForPersonCacheDatabase
import com.raaf.android.searchmovie.database.top.ReleasesDatabase
import com.raaf.android.searchmovie.database.top.TopDatabase
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import javax.inject.Inject

private var TAG = "FilmFetcher"
private const val F_T_WATCHED = "Watched films"
private var PROFESSION_KEYS = listOf("DIRECTOR", "ACTOR", "PRODUCER")
private var YEARS_PARAMETERS = listOf(Pair(2020, 2020), Pair(2019, 2019), Pair(2018, 2018), Pair(2017, 2017), Pair(2016, 2016), Pair(2015, 2015),
                                        Pair(2010, 2014), Pair(2000, 2009), Pair(1990, 1999), Pair(1980, 1989), Pair(1970, 1979), Pair(1960, 1969),
                                        Pair(1950, 1959), Pair(1940, 1949), Pair(1930, 1939), Pair(1920, 1929), Pair(1910, 1919), Pair(1900, 1909), Pair(1890, 1899))

class FilmRepo @Inject constructor(val filmWebService: FilmWebService,
                                   val topDatabase: TopDatabase,
                                   val myFilmsDatabase: MyFilmsDatabase,
                                   val compilationDatabase: CompilationDatabase,
                                   val watchedDatabase: WatchedDatabase,
                                   val releasesDatabase: ReleasesDatabase,
                                   val popularPersonDatabase: PopularPersonDatabase,
                                   val myPersonsDatabase: MyPersonsDatabase,
                                   val moviesForPersonCacheDatabase: MoviesForPersonCacheDatabase,
                                   val categoryDatabase: CategoryDatabase,
                                   val categoryFilmsCacheDatabase: CategoryFilmsCacheDatabase,
                                   val appConverter: AppConverter,
                                   val context: Context) {

    private val filmApi: FilmApi
    private var bestWorks: String
    private var myFilms: List<String>
    private var categoryNames: List<String>
    private var nameCompilation: List<String>
    private lateinit var yearsItemsNames: List<String>
    private lateinit var tvSeriesItemsNames: List<String>
    private lateinit var genreItemNames: List<String>
    private val countries = mapOf("Russia" to 2, "USSR" to 13, "Japan" to 9, "China" to 31, "South Korea" to 26)
    private val countriesForCategory = mapOf(context.getString(R.string.great_britain) to 11, context.getString(R.string.india) to 29, context.getString(R.string.spain) to 15, context.getString(R.string.canada) to 6, context.getString(R.string.norway) to 33, context.getString(R.string.russia) to 2, context.getString(R.string.usa) to 1, context.getString(R.string.turkey) to 68, context.getString(R.string.ukraine) to 62, context.getString(R.string.france) to 8, context.getString(R.string.sweden) to 5)
    private val genres: Map<String, Int> by lazy {
            mapOf(genreItemNames[0] to 1750, genreItemNames[1] to 22, genreItemNames[2] to 17, genreItemNames[3] to 12, genreItemNames[4] to 15,
                genreItemNames[5] to 16, genreItemNames[6] to 7, genreItemNames[7] to 9, genreItemNames[8] to 10, genreItemNames[9] to 25,
                genreItemNames[10] to 26, genreItemNames[11] to 4, genreItemNames[12] to 1, genreItemNames[13] to 2)
        }

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

            bestWorks = context.getString(R.string.best_works)
            myFilms = listOf("Watch later", "Favorite movies")
            categoryNames = listOf(context.getString(R.string.years), context.getString(R.string.tv_series), context.getString(R.string.genre), context.getString(R.string.countries))
            nameCompilation = listOf("comedy", "fantasy", "cartoon", "drama", "action")
            getItemNamesForCategory()
        }

        private val executor = Executors.newFixedThreadPool(2)
        val topDao = topDatabase.topDao()
        val myFilmsDao = myFilmsDatabase.myFilmsDao()
        val compilationDao = compilationDatabase.compilationDao()
        val watchedDao = watchedDatabase.watchedDao()
        val releasesDao = releasesDatabase.releasesDao()
        val popularPersonDao = popularPersonDatabase.popularPersonDao()
        val myPersonsDao = myPersonsDatabase.myPersonsDao()
        val personCacheDao = moviesForPersonCacheDatabase.moviesForPersonCacheDao()
        val categoryDao = categoryDatabase.categoryDao()
        val categoryFilmsCacheDao = categoryFilmsCacheDatabase.categoryFilmsCacheDao()

        fun getWatchParentName() : String {
            return myFilms[0]
        }

    suspend fun requestMovieForUI(id: Int, appendToResponse: Array<String>) : MovieById {
        return filmWebService.fetchMovie(id, appendToResponse)
    }

        //  Search Movie By Id(1)
        fun fetchMovie(id: Int, appendToResponse: Array<String>, isAddFromDB: Boolean, parent: String): LiveData<MovieById> {
            val responseLiveData: MutableLiveData<MovieById> = MutableLiveData()
            val searchesRequest: Call<MovieById> = filmApi.fetchMovie(id, appendToResponse)

            searchesRequest.enqueue(object : Callback<MovieById> {
                override fun onFailure(call: Call<MovieById>, t: Throwable) {
                    Log.e(TAG, "Failed response1", t)
                }

                override fun onResponse(call: Call<MovieById>, response: Response<MovieById>) {
                    val filmResponse: MovieById? = response.body()
                    if (filmResponse != null) {
                        responseLiveData.value = filmResponse
                        if (isAddFromDB) {
                            if (filmResponse.data.nameEn == "null" || filmResponse.data.nameEn == null) filmResponse.data.nameEn = ""
                            if (parent == bestWorks) {
                                executor.execute {
                                    personCacheDao.insert(appConverter.parseMoviesToFilmSwipeItem(listOf(appConverter.parseMovieByIdToMovie(filmResponse, parent)), parent)) }//Вставка проходит только на один фильм
                            }
                            else executor.execute { myFilmsDao.insert(appConverter.parseMovieByIdToMovie(filmResponse, parent)) }
                        }
                    }
                }
            })
            return responseLiveData
        }

        // Frames by Film ID(2)
        fun fetchFrames(id: Int): LiveData<List<Frame>> {
            val responseLiveData: MutableLiveData<List<Frame>> = MutableLiveData()
            val searchesRequest: Call<FramesByFilmId> = filmApi.fetchFrames(id)

            searchesRequest.enqueue(object : Callback<FramesByFilmId> {
                override fun onFailure(call: Call<FramesByFilmId>, t: Throwable) {
                    Log.e(TAG, "Failed response2", t)
                }

                override fun onResponse(call: Call<FramesByFilmId>, response: Response<FramesByFilmId>) {
                    val filmResponse: FramesByFilmId? = response.body()
                    if (filmResponse != null) {
                        responseLiveData.value = appConverter.parseFramesToFrame(filmResponse.frames)
                    }
                }
            })
            return responseLiveData
        }


        fun fetchFrame(id: Int) : CompletableFuture<String> {
            var futureFrameUrl = CompletableFuture<String>()
            val searchesRequest: Call<FramesByFilmId> = filmApi.fetchFrames(id)

            searchesRequest.enqueue(object : Callback<FramesByFilmId> {
                override fun onFailure(call: Call<FramesByFilmId>, t: Throwable) {
                    Log.e(TAG, "Failed response3", t)
                }

                override fun onResponse(call: Call<FramesByFilmId>, response: Response<FramesByFilmId>) {
                    val framesResponse: FramesByFilmId? = response.body()
                    if (framesResponse != null && framesResponse.frames.isNotEmpty()) {
                        //Log.e(TAG, "{\"frameUrl\":\"${framesResponse.frames[0].preview}\"}")
                        futureFrameUrl.complete(framesResponse.frames[0].preview)
                    } else futureFrameUrl.complete("null")
                }
            })
            return futureFrameUrl
        }

        //    Трейлеры и тизеры по фильму(3)
        fun fetchTrailers(id: Int): LiveData<List<Trailer>> {
            val responseLiveData: MutableLiveData<List<Trailer>> = MutableLiveData()
            val searchesRequest: Call<TrailerResponse> = filmApi.fetchVideos(id)

            searchesRequest.enqueue(object : Callback<TrailerResponse> {
                override fun onFailure(call: Call<TrailerResponse>, t: Throwable) {
                    Log.e(TAG, "Failed response4", t)
                }

                override fun onResponse(call: Call<TrailerResponse>, response: Response<TrailerResponse>) {
                    val filmResponse: TrailerResponse? = response.body()
                    if (filmResponse != null) {
                        responseLiveData.value = filmResponse.trailers
                    } else responseLiveData.value = listOf<Trailer>()
                }
            })
            return responseLiveData
        }

        fun fetchSequelsAndPrequelsByFilmId(id: Int) : LiveData<List<SequelsPrequelsResponse>> {
            val responseLiveData = MutableLiveData<List<SequelsPrequelsResponse>>()
            val requestCall: Call<List<SequelsPrequelsResponse>> = filmApi.fetchSequelsAndPrequels(id)

            requestCall.enqueue(object : Callback<List<SequelsPrequelsResponse>> {
                override fun onFailure(call: Call<List<SequelsPrequelsResponse>>, t: Throwable) {
                    Log.e(TAG, "Failed response5", t)
                }

                override fun onResponse(call: Call<List<SequelsPrequelsResponse>>, response: Response<List<SequelsPrequelsResponse>>) {
                    val filmResponse: List<SequelsPrequelsResponse>? = response.body()
                    if (filmResponse != null) {
//                        Log.e(TAG, "fSAPBFI: response list size: " + filmResponse.size.toString())
                        responseLiveData.value = filmResponse!!
                    } else responseLiveData.value = listOf<SequelsPrequelsResponse>()
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
                    Log.e(TAG, "Failed response6", t)
                }

                override fun onResponse(call: Call<SearchByKeyword>, response: Response<SearchByKeyword>) {
                    val searchResponse: SearchByKeyword? = response.body()
                    responseResult.value = searchResponse
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
                    Log.e(TAG, "Failed response7", t)
                }

                override fun onResponse(call: Call<FiltersResponse>, response: Response<FiltersResponse>) {
                val responseUrl = response
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
                                 isAddToDB: Boolean,
                                 parent: String,
                                 currentPage: Int,
                                 isAddFromCache: Boolean,
                                 forHome: Boolean = false): LiveData<SearchByFilters> {
            val responseLiveData: MutableLiveData<SearchByFilters> = MutableLiveData()
            val searchByFiltersRequest: Call<SearchByFilters> = filmApi.fetchSearchByFilters(country, genre, order, type, ratingFrom, ratingTo, yearFrom, yearTo, page)

            searchByFiltersRequest.enqueue(object : Callback<SearchByFilters> {
                override fun onFailure(call: Call<SearchByFilters>, t: Throwable) {
                    Log.e(TAG, "Failed response8: ${call.request().url()}")
                }

                override fun onResponse(call: Call<SearchByFilters>, response: Response<SearchByFilters>) {
                    val filmResponse = response.body()
                    if (filmResponse != null) {
                        responseLiveData.value = filmResponse
                        if (isAddToDB) {
                            if (parent.contains(nameCompilation[2]) || parent.contains(nameCompilation[4])) {
                                if (page <= 2)executor.execute { compilationDao.insert(appConverter.parseFilmsToFilmSwipeItem(filmResponse.films, parent, currentPage, true)) }
                                else executor.execute { compilationDao.insert(appConverter.parseFilmsToFilmSwipeItem(filmResponse.films, parent, currentPage, forHome)) }
                            }
                            else executor.execute { compilationDao.insert(appConverter.parseFilmsToFilmSwipeItem(filmResponse.films, parent, currentPage, forHome)) }
                        }
                        if (isAddFromCache) executor.execute { categoryFilmsCacheDao.insert(appConverter.parseFilmsToFilmSwipeItem(filmResponse.films, parent, currentPage, forHome)) }
                    }
                }
            })
            return responseLiveData
        }


        fun fetchSearchByFiltersForFrames(country: Array<Int>?,
                                          genre: Array<Int>?,
                                          order: String,
                                          type: String,
                                          ratingFrom: Int,
                                          ratingTo: Int,
                                          yearFrom: Int,
                                          yearTo: Int,
                                          page: Int) : CompletableFuture<String> {

            var futureFrameUrl = CompletableFuture<String>()
            val searchByFiltersRequest: Call<SearchByFilters> = filmApi.fetchSearchByFilters(country, genre, order, type, ratingFrom, ratingTo, yearFrom, yearTo, page)

            searchByFiltersRequest.enqueue(object : Callback<SearchByFilters> {
                override fun onFailure(call: Call<SearchByFilters>, t: Throwable) {
                    Log.e(TAG, "Failed response9", t)
                }

                override fun onResponse(call: Call<SearchByFilters>, response: Response<SearchByFilters>) {
                    val filmResponse = response.body()
                    if (filmResponse != null) {
                        var completeString = ""
                        for (count in 0 until filmResponse.films.count()) {
                            if (count != 4) completeString += "${filmResponse.films[0].filmId},"
                            else break
                        }
                        futureFrameUrl.complete(completeString)
                    } else futureFrameUrl.complete("null")
                }
            })
            return futureFrameUrl
        }

        //      Top Films of Kinopoisk(9)
        fun fetchTopMovie(type: String, page: Int, categoryName: String) : LiveData<Int> {
            lateinit var responseResult: List<FilmSwipeItem>
            val searchesRequest: Call<SearchByFilters> = filmApi.fetchTop(type, page)
            val pagesCount: MutableLiveData<Int> = MutableLiveData()

            searchesRequest.enqueue(object : Callback<SearchByFilters> {
                override fun onFailure(call: Call<SearchByFilters>, t: Throwable) {
                    Log.e(TAG, "Failed response11", t)
                }

                override fun onResponse(call: Call<SearchByFilters>, response: Response<SearchByFilters>) {
                    val filmResponse: SearchByFilters? = response.body()
                    if (filmResponse != null) {
                        filmResponse.films.forEach { if (it.nameEn == null) it.nameEn = "" }
                        pagesCount.value = filmResponse.pagesCount
                        responseResult = appConverter.parseFilmsToFilmSwipeItem(filmResponse.films, categoryName, page, false)
                    }
                    executor.execute { topDao.insert(responseResult) }
                }
            })
            return pagesCount
        }

    suspend fun requestSimilarFilmsByMovieId(id: Int) : List<SimilarFilm> {
        return filmWebService.fetchSimilarsMovieByFilmId(id).items
    }

        fun fetchSimilarFilmsByFilmId(id: Int) : LiveData<List<SimilarFilm>> {
            val responseLiveData = MutableLiveData<List<SimilarFilm>>()
            val similarRequest: Call<RelatedFilmResponse> = filmApi.fetchSimilarsMovieByFilmId(id)

            similarRequest.enqueue(object : Callback<RelatedFilmResponse> {
                override fun onFailure(call: Call<RelatedFilmResponse>, t: Throwable) {
                    Log.e(TAG, "Failed response12", t)
                }

                override fun onResponse(call: Call<RelatedFilmResponse>, response: Response<RelatedFilmResponse>) {
                    val filmResponse: RelatedFilmResponse? = response.body()
                    if (filmResponse != null) {
                        responseLiveData.value = filmResponse.items
                    }
                }
            })
            return responseLiveData
        }

        //  Digital Releases(11)
        private fun fetchReleases(year: Int, month: String, page: Int) {
            val releasesRequest: Call<DigitalReleases> = filmApi.fetchReleases(year, month, page)
            var resultList = mutableListOf<FilmSwipeItem>()

            releasesRequest.enqueue(object : Callback<DigitalReleases> {
                override fun onFailure(call: Call<DigitalReleases>, t: Throwable) {
                    Log.e(TAG, "Failed response13", t)
                }

                override fun onResponse(call: Call<DigitalReleases>, response: Response<DigitalReleases>) {
                    val filmResponse = response.body()
                    if (filmResponse != null) {
                        resultList.addAll(appConverter.parseReleasesToFilmSwipeItem(filmResponse.releases, filmResponse.total))
                        executor.execute { releasesDao.insert(resultList) }
                    }
                }
            })
        }

        //    Staff By Id(13)
        fun fetchStaffByIdForPopularPersons(filmId: Int) {
            val releasesRequest: Call<List<StaffPerson>> = filmApi.fetchStaffByMovieId(filmId)
            var countAddingInDb = 0
            releasesRequest.enqueue(object : Callback<List<StaffPerson>> {
                override fun onFailure(call: Call<List<StaffPerson>>, t: Throwable) {
                    Log.e(TAG, "Failed response14", t)
                }

                override fun onResponse(call: Call<List<StaffPerson>>, response: Response<List<StaffPerson>>) {
                    val personsResponse = response.body()
                    if (personsResponse != null) {
                        for (count in personsResponse) {
                            var person = count
                            for (count in PROFESSION_KEYS) {
                                if (person.professionKey.contains(count)) {
                                    executor.execute { popularPersonDao.insert(person) }
                                    ++countAddingInDb
                                }
                                if (countAddingInDb >= 5) break
                            }
                        }
                    }
                }
            })
        }

    suspend fun requestStaffByMovieId(filmId: Int) : List<StaffPerson> {
        Log.e(TAG, "rS: $filmId")
        return filmWebService.fetchStaffByMovieId(filmId)
    }

        fun fetchStaffByIdForUI(filmId: Int): LiveData<List<StaffPerson>> {
            val releasesRequest: Call<List<StaffPerson>> = filmApi.fetchStaffByMovieId(filmId)
            var staffLiveData = MutableLiveData<List<StaffPerson>>()

            releasesRequest.enqueue(object : Callback<List<StaffPerson>> {
                override fun onFailure(call: Call<List<StaffPerson>>, t: Throwable) {
                    Log.e(TAG, "Failed response15", t)
                }

                override fun onResponse(call: Call<List<StaffPerson>>, response: Response<List<StaffPerson>>) {
                    val personsResponse = response.body()
                    if (personsResponse != null) staffLiveData.value = personsResponse!!
                }
            })
            return staffLiveData
        }

        fun fetchPersonById(personId: Int, isAddFromDB: Boolean) : LiveData<PersonResponse> {
            val personRequest = filmApi.fetchStaffByPersonId(personId)
            val personLiveData = MutableLiveData<PersonResponse>()

            personRequest.enqueue(object : Callback<PersonResponse> {
                override fun onFailure(call: Call<PersonResponse>, t: Throwable) {
                    Log.e(TAG, "Failed response16", t)
                }

                override fun onResponse(call: Call<PersonResponse>, response: Response<PersonResponse>) {
                    var personResponse = response.body()
                    if (personResponse != null) {
                        if (personResponse.films.size > 100) personResponse.films = personResponse.films.subList(0, 50)
                        personLiveData.value = personResponse!!
                        if (isAddFromDB) executor.execute { myPersonsDao.insert(personResponse) }
                    } else personLiveData.value = PersonResponse()
                }
            })
            return personLiveData
        }

        fun checkSpouse(personId: Int) : Boolean {
            val personRequest: Call<PersonResponse> = filmApi.fetchStaffByPersonId(personId)

            var future = CompletableFuture.supplyAsync {
                var response = personRequest.execute().body()
                response != null
            }
            return future.get()
        }

        fun clearTopDb() {
            executor.execute { topDatabase.clearAllTables() }
        }

        fun clearCompilationDb() {
            executor.execute { compilationDatabase.clearAllTables() }
        }

        fun clearReleasesDb() {
            executor.execute { releasesDatabase.clearAllTables() }
        }

        fun deleteMovieFromMyFilmsDb(endsId: String) {
            executor.execute { myFilmsDao.deleteWithEndsId(endsId) }
        }

//        fun deleteMovieFromHistoryDb(endsId: String) {
//            executor.execute { watchedDao.deleteWithEndsId(endsId) }
//        }

    suspend fun checkMovieInMyFilmsDB(parent: Int, movieId: Int) : Boolean {
        return myFilmsDao.checkItem(myFilms[parent] + movieId) != null
    }

        fun checkMovieInMyFilmsDB2(parentName: Int, filmId: Int) : MutableLiveData<Boolean> {
            val dbLiveData = myFilmsDao.checkItemLV(myFilms[parentName] + filmId.toString())
            val resultLiveData = MutableLiveData<Boolean>()
            dbLiveData.observeForever {
                resultLiveData.value = it != null
            }
            return resultLiveData
        }



        //использую observeForever, так как этот класс является синглтоном
        fun setWatched() : LiveData<List<FilmSwipeItem>> {
            val dbLiveData = watchedDao.loadAll()
            val listResult = mutableListOf<FilmSwipeItem>()
            val resultLiveData = MutableLiveData<List<FilmSwipeItem>>()
            dbLiveData.observeForever { list->
                val sortedList = list.sortedByDescending { it.DBId }
                sortedList.let { listResult.addAll(appConverter.parseMoviesToFilmSwipeItem(sortedList, "")) }
                resultLiveData.value = listResult
            }
            return resultLiveData
        }


        private fun fixCountDataInWatchedBd() {
            var futureCountWatched = CompletableFuture<Long>()
            executor.execute {
                futureCountWatched.complete(watchedDao.getCount().toLong())
                var countWatched = futureCountWatched.get()
                if (countWatched > 29) {
                    for (count in 1..countWatched - 29) {
                        executor.execute { watchedDao.deleteForHistory() }
                    }
                }
            }
        }


        fun addToWatchedDb(movie: MovieById) {
            fixCountDataInWatchedBd()
            executor.execute {
                try {
                    watchedDao.insertForWatched(appConverter.parseMovieByIdToMovie(movie, F_T_WATCHED))
                } catch (e:Exception) {
                    Log.e(TAG, e.localizedMessage)
                }
            }
        }


        fun setBestWorksForPerson(filmId:List<Int>) {
            filmId.forEach { fetchMovie(it, arrayOf("RATING"), true, bestWorks) }
        }

        fun getBestWorksForPerson() : LiveData<List<FilmSwipeItem>> {
            return personCacheDao.loadAll()
        }

        fun clearPersonCache() {
            executor.execute { moviesForPersonCacheDatabase.clearAllTables() }
        }

        fun setReleases() : LiveData<List<FilmSwipeItem>> {
            return releasesDao.loadAll()
        }

        fun getReleases(year: Int, months: List<String?>) {
            for (count in months) {
                var month = count
                for (count in 1..4) {
                    if (month != null) {
                        fetchReleases(year,month,count)
                    }
                }
            }
        }




        //Use it in MainActivity
        fun getPopularPersons() : LiveData<List<StaffPerson>>{
            return popularPersonDao.loadAll()
        }

        fun setPopularPersonsFromMyFilms() {
            val listFilms = myFilmsDao.getAll()
            listFilms.forEach { fetchStaffByIdForPopularPersons(it.filmId) }
            if (listFilms.count() < 10) setPopularPersonsFromDigitalReleases()
        }

        fun setPopularPersonsFromDigitalReleases() {
            val movieList = releasesDao.getAll()
            if (movieList.count() > 11) movieList.subList(0, 10).forEach { fetchStaffByIdForPopularPersons(it.filmId) }
            else movieList.forEach { fetchStaffByIdForPopularPersons(it.filmId) }
        }

        fun clearPopularPersonDb() {
            executor.execute { popularPersonDatabase.clearAllTables() }
        }

        fun clearCategoryDb() {
            executor.execute { categoryDatabase.clearAllTables() }
        }

        fun setMyPerson() : LiveData<MutableList<PersonResponse>> {
            val dbLiveData = myPersonsDao.loadAll()
            val listResult = mutableListOf<PersonResponse>()
            val resultLiveData = MutableLiveData<MutableList<PersonResponse>>()
            dbLiveData.observeForever { list->
                list.let { listResult.addAll(list) }
                resultLiveData.value = listResult
            }
            return resultLiveData
        }

        fun getItemNamesForCategory() {
            yearsItemsNames = context.resources.getStringArray(R.array.years).toList()
            genreItemNames = context.resources.getStringArray(R.array.genres).toList()
            tvSeriesItemsNames = context.resources.getStringArray(R.array.tv_series).toList()
        }

        fun findNumberOfItem(listItems:List<String>, itemName: String) : Int {
            for (count in 0 until listItems.count()) {
                if (itemName == listItems[count]) return count
            }
            return -1
        }

        fun getCategoryCache(categoryName: String, categoryItemName: String) {
            when (categoryName) {
                //Years
                categoryNames[0] -> {
                    var countItem = findNumberOfItem(yearsItemsNames, categoryItemName)
                    for (count in 0 until yearsItemsNames.count()) {
                        if (categoryItemName == yearsItemsNames[count]){
                            countItem = count
                            break
                        }
                    }
                    for (count in 1..3){
                        fetchSearchByFilters(null, null, "RATING", "FILM", 0, 0, YEARS_PARAMETERS[countItem].first, YEARS_PARAMETERS[countItem].second, count, false, categoryItemName, count, true)
                    }
                }
                //TV Series
                categoryNames[1] -> {
                    when(categoryItemName) {
                        tvSeriesItemsNames[0] -> {
                            for (count in 1..5) {
                                fetchSearchByFilters(null, null, "NUM_VOTE", "TV_SHOW", 6, 0, 1960, 2020, count, false, categoryItemName, count, true)//Best
                            }
                        }
                        tvSeriesItemsNames[1] -> {
                            for (count in 1..3) {
                                fetchSearchByFilters(null, null, "NUM_VOTE", "TV_SHOW", 0, 0, 2020, 2020, count, false, categoryItemName, count, true)//2020
                            }
                        }
                        tvSeriesItemsNames[2] -> {
                            for (count in 1..3) {
                                fetchSearchByFilters(arrayOf(countries["Russia"]!!), null, "NUM_VOTE", "TV_SHOW", 6, 0, 1960, 2020, count, false, categoryItemName, count, true)//rus
                            }
                        }
                        tvSeriesItemsNames[3] -> {
                            var country = arrayOf<Int>()
                            for (count in 1..4) {
                                if (count <= 2) country = arrayOf(countries["Japan"]!!)
                                if (count == 3) country = arrayOf(countries["China"]!!)
                                if (count == 4) country = arrayOf(countries["South Korea"]!!)
                                fetchSearchByFilters(country, null, "NUM_VOTE", "TV_SHOW", 6, 0, 1960, 2020, count, false, categoryItemName, count, true)//asian
                            }
                        }
                    }
                }
                //Genres
                categoryNames[2] -> {
                    var genre = 0
                    for (count in genres) {
                        if (categoryItemName == count.key){
                            genre = count.value
                            break
                        }
                    }
                    for (count in 1..3){
                        fetchSearchByFilters(null, arrayOf(genre), "NUM_VOTE", "ALL", 6, 10, 1950, 2020, count, false, categoryItemName, count, true)
                    }
                }
                //Countries
                categoryNames[3] -> {
                    var country = 0
                    for (count in countriesForCategory) {
                        if (categoryItemName == count.key) {
                            country = count.value
                            break
                        }
                    }
                    for (count in 1..3) {
                        fetchSearchByFilters(arrayOf(country), null, "NUM_VOTE", "ALL", 6, 10, 1950, 2020, count, false, categoryItemName, count, true)
                    }
                }
            }
        }

        fun setCategory() : LiveData<List<FilmSwipeItem>> {
            return categoryFilmsCacheDao.loadAll()
        }

        fun checkPersonInMyPersonDB(personId: Int) : MutableLiveData<Boolean> {
            val dbLiveData = myPersonsDao.checkItem(personId)
            val resultLiveData = MutableLiveData<Boolean>()
            dbLiveData.observeForever {
                resultLiveData.value = it != null
            }
            return resultLiveData
        }

        fun deletePersonFromMyPersonsDB(id: Int) {
            executor.execute { myPersonsDao.deleteWithId(id) }
        }

        fun clearMyFilmsDB() {
            executor.execute { myFilmsDatabase.clearAllTables() }
        }

        fun clearMyPersonDB() {
            executor.execute { myPersonsDatabase.clearAllTables() }
        }

        fun clearHistoryDB() {
            executor.execute { myPersonsDatabase.clearAllTables() }
        }

        fun clearCategoryFilmsCacheDb() {
            executor.execute { categoryFilmsCacheDatabase.clearAllTables() }
        }


        //val yearPair = MutableLiveData<Pair<Int, Int>>()
//    val frameLiveData: LiveData<SearchByFilters> = Transformations.switchMap(yearPair) {
//        fetchSearchByFilters(null, null, "RATING", "FILM", 0, 0, it.first, it.second, 1, false, "", 0, currentPosition)
//    }


        fun fillingTVSeriesItemsForCategoryDb(categoryItemsNames: List<String>, categoryName: String) {
            //tvSeriesItemsNames = categoryItemsNames
            if (!checkCategoryItemsInCategoryDb(categoryName).get()) {
                executor.execute {
                    for (count in 0 until categoryItemsNames.count()) {
                        var item = CategoryItem()
                        var frameUrl: String? = ""
                        var filmIdList = when(count) {
                            0 -> fetchSearchByFiltersForFrames(null, null, "NUM_VOTE", "TV_SHOW", 6, 10, 1960, 2020, 1).get().split(",")//Best
                            1 -> fetchSearchByFiltersForFrames(null, null, "NUM_VOTE", "TV_SHOW", 0, 10, 2020, 2020, 1).get().split(",")//2020
                            2 -> fetchSearchByFiltersForFrames(arrayOf(countries["Russia"]!!), null, "NUM_VOTE", "TV_SHOW", 0, 10, 1960, 2020, 1).get().split(",")//rus
                            3 -> fetchSearchByFiltersForFrames(arrayOf(countries["Japan"]!!), null, "NUM_VOTE", "TV_SHOW", 6, 10, 1960, 2020, 1).get().split(",")//asian
                            else -> listOf()
                        }
                        if (filmIdList.isNotEmpty()) {
                            for (count in filmIdList) {
//                        Log.e(TAG, "count::$count")
                                frameUrl = fetchFrame(count.toInt()).get()
                                if (frameUrl != "null") break
                            }
                            if (frameUrl == "null") frameUrl = null
                        }
                        item.itemName = categoryItemsNames[count]
                        item.categoryName = categoryName
                        item.frameUrl = frameUrl
                        categoryDao.insert(item)
                    }
                }
            }
        }


        fun fillingGenreItemsForCategoryDb(categoryItemsNames: List<String>, categoryName: String) {
            //genreItemNames = categoryItemsNames
            if (!checkCategoryItemsInCategoryDb(categoryName).get()) {
                executor.execute {
                    for (count in 0 until categoryItemsNames.count()) {
                        var item = CategoryItem()
                        var frameUrl: String? = ""
                        var filmIdList = fetchSearchByFiltersForFrames(null, arrayOf(genres[categoryItemsNames[count]]!!), "NUM_VOTE", "ALL", 6, 10,
                            1950, 2020, 1).get().split(",")
                        if (filmIdList.isNotEmpty()) {
                            for (count in filmIdList) {
//                        Log.e(TAG, "count::$count")
                                if (count.isNotBlank()) frameUrl = fetchFrame(count.toInt()).get()
                                if (frameUrl != "null") break
                            }
                            if (frameUrl == "null") frameUrl = null
                        }
                        item.itemName = categoryItemsNames[count]
                        item.categoryName = categoryName
                        item.frameUrl = frameUrl
                        categoryDao.insert(item)
                    }
                }
            }
        }


        fun fillingCountriesCategory(categoryItemsNames: List<String>, categoryName: String) {
            Log.e(TAG, "FCC")
            if (!checkCategoryItemsInCategoryDb(categoryName).get()) {
                    Log.e(TAG, "FCC executing")
                    for (count in 0 until categoryItemsNames.count()) {
                        Log.e(TAG, "FCC:$count          CFC:${countriesForCategory[categoryItemsNames[count]]}")
                        var item = CategoryItem()
                        var frameUrl: String? = ""
                        var filmIdList = fetchSearchByFiltersForFrames(arrayOf(countriesForCategory[categoryItemsNames[count]]!!), null, "NUM_VOTE", "ALL", 6, 10,
                            1950, 2020, 1).get().split(",") ?: listOf()
                        Log.e(TAG, "filmIdList:${filmIdList.toString()}")
                        if (filmIdList.isNotEmpty()) {
                            for (countId in filmIdList) {
                                Log.e(TAG, "FCC:$count       FIL:$countId")
//                        Log.e(TAG, "count::$count")
                                if (countId.isNotBlank()) frameUrl = fetchFrame(countId.toInt()).get()
                                if (frameUrl != "null") break
                            }
                            if (frameUrl == "null") frameUrl = null
                        }
                        Log.e(TAG, "FCC:finish")
                        item.itemName = categoryItemsNames[count]
                        item.categoryName = categoryName
                        item.frameUrl = frameUrl
                        Log.e(TAG, "item:${item.toString()}")
                        categoryDao.insert(item)
                    }
            }
        }


        fun fillingYearItemsForCategoryDb(categoryItemsNames: List<String>, categoryName: String) {
            //yearsItemsNames = categoryItemsNames
            Log.e(TAG, "fillingYearItemsForCategoryDb")
            Log.e(TAG, categoryName)
            Log.e(TAG, categoryItemsNames.toString())
            if (!checkCategoryItemsInCategoryDb(categoryName).get()) {
                executor.execute {
                    for (count in 0 until categoryItemsNames.count()) {
                        var item = CategoryItem()
                        var frameUrl: String? = ""
//                        Log.e(TAG,"CAI" + categoryItemsNames[count])
//                        Log.e(TAG, "param" + YEARS_PARAMETERS[count].first.toString() + YEARS_PARAMETERS[count].second.toString())
                        var filmIdList = fetchSearchByFiltersForFrames(null, null, "NUM_VOTE", "FILM",
                            5, 10, YEARS_PARAMETERS[count].first,
                            YEARS_PARAMETERS[count].second, 1).get().split(",")
                        if (filmIdList.isNotEmpty()) {
                            for (count in filmIdList) {
//                        Log.e(TAG, "count::$count")
                                if (count.isNotBlank()) frameUrl = fetchFrame(count.toInt()).get() ?: ""
                                if (frameUrl != "null" && frameUrl!!.isNotBlank()) break
                            }
                            if (frameUrl == "null") frameUrl = null
                        }
                        item.itemName = categoryItemsNames[count]
                        item.categoryName = categoryName
                        item.frameUrl = frameUrl
                        categoryDao.insert(item)
                        //Log.e(TAG, item.toString())
                    }
                }
            } //else Log.e(TAG, "Категории ест")
        }


        private fun checkCategoryItemsInCategoryDb(categoryName: String) : CompletableFuture<Boolean> {
            var futureBoolean = CompletableFuture<Boolean>()
            executor.execute {
                var count = categoryDao.checkItem(categoryName)
                //Log.e(TAG, "count::$count")
                if (count > 16) futureBoolean.complete(true)
                else futureBoolean.complete(false)
            }
            return futureBoolean
        }

        fun getCategoryItems(categoryName: String) : LiveData<List<CategoryItem>> {
            return categoryDao.loadByCategoryName(categoryName)
        }

    fun setCompilationForDetails(parent: String) : LiveData<List<FilmSwipeItem>> {
        var dbLiveData = compilationDao.loadCategoryByName(parent)
        return dbLiveData
    }

    fun setCompilationForHome() : LiveData<List<FilmSwipeItem>> {
        var dbLiveData = compilationDao.loadForCompilation(true)
        return dbLiveData
    }

    //использую observeForever, так как этот класс является синглтоном
    fun setMyFilms() : LiveData<List<FilmSwipeItem>> {
        val listLiveData = MutableLiveData<List<FilmSwipeItem>>()
        executor.execute { listLiveData.postValue(appConverter.parseMoviesToFilmSwipeItem(myFilmsDao.getAll(), "")) }
        return listLiveData
    }

    //  getSearchByKeyword Предназначен для отображения в SearchResultFragment
//  Change to database later instead to send request result in UI
    fun getSearchByKeyword(keyword: String, page: Int) : LiveData<SearchByKeyword> {
        val request = fetchSearchByKeyword(keyword, page)
        return request
    }

//    These methods are designed to work with data for a TopFragment

    //    Insert all data in TopDatabase
    fun getTop(type: String, page: Int, categoryName: String) : LiveData<Int> {
        val request = fetchTopMovie(type, page, categoryName)
        return request
    }

    fun setTop() : LiveData<List<FilmSwipeItem>> {
        var dbLiveData1 = topDao.loadAll()
        return dbLiveData1
    }

    //    These methods are designed to work with data for a CompilationFragment
    fun getCompilation() {
        val genres = mapOf("Comedy" to 6, "Fantasy" to 5, "Cartoon" to 14, "Drama" to 8, "Action" to 3)
        executor.execute {
            var countInt = 0
            for (count in genres) {
                for (it in 1..5) {
                    if (it == 1) fetchSearchByFilters(null, arrayOf(count.value), "RATING",
                        "FILM", 5, 10, 2010, 2020, it, true, nameCompilation[countInt], it, false, true)
                    else  fetchSearchByFilters(null, arrayOf(count.value), "RATING",
                        "FILM", 6, 10, 2010, 2020, it, true, nameCompilation[countInt], it, false)
                }
                countInt += 1
            }
        }
    }

    suspend fun checkDBContent() : Boolean {
        val databasesState = listOf(isCategoryDBEmpty(),
                isCompilationDBEmpty(),
                isPopularPersonDBEmpty(),
                isReleasesDBEmpty(),
                isTopDBEmpty())
        databasesState.forEach { if (it) return it }
        return false
    }

    private fun isCompilationDBEmpty() : Boolean {
        return compilationDao.getCount() == 0
    }

    private fun isReleasesDBEmpty() : Boolean {
        return releasesDao.getCount() == 0
    }

    private fun isTopDBEmpty() : Boolean {
        return topDao.getCount() == 0
    }

    private fun isPopularPersonDBEmpty() : Boolean {
        return popularPersonDao.getCount() == 0
    }

    private fun isCategoryDBEmpty() : Boolean {
        return categoryDao.getCount() == 0
    }
}