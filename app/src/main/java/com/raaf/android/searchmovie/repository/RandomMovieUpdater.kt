package com.raaf.android.searchmovie.repository

import android.util.Log
import com.raaf.android.searchmovie.dataModel.Film
import com.raaf.android.searchmovie.dataModel.Filters
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

private const val MAX_ID = 1439000
private const val TAG = "RandomMovieUpdater"

class RandomMovieUpdater @Inject constructor(val repository: FilmRepo) {

    var currentRandomMovie: Film? = null
        set(value) {
            movieFlow.value = value
            field = value
        }
    var movieFlow: MutableStateFlow<Film?> = MutableStateFlow(currentRandomMovie)
    private var nextRandomMovie: Film? = null
    private var filters: Filters? = null
    private var userUpdateEvent = false
    private var userChangeFiltersEvent = false
    private var isNeedToStopUpdate = false
    private val IOCoroutineContext: CoroutineContext = Dispatchers.IO
    private val coroutineExceptionHandler =  CoroutineExceptionHandler { coroutineContext, throwable ->
        when (throwable) {
            is HttpException -> Log.e(TAG, "HTTP 404")
            is SocketTimeoutException -> Log.e(TAG, "call was timeout")
        }
    }
    private val coroutineContext = Executors.newFixedThreadPool(1).asCoroutineDispatcher() + coroutineExceptionHandler
    private val coroutineScope = CoroutineScope(SupervisorJob())

    init {
        runUpdate()
    }

    private fun runUpdate() {
        coroutineScope.launch(coroutineContext) {
            firstUpdate()
            while (!isNeedToStopUpdate) {
                waitingEndUserActions()
                update()
                delay(5000)
            }
        }
    }

    private suspend fun waitingEndUserActions() {
        while (userUpdateEvent || userChangeFiltersEvent) {
            if (userUpdateEvent) {
                userUpdateEvent = false
                delay(10000)
            }
            if (userChangeFiltersEvent) {
                userChangeFiltersEvent = false
                delay(30000)
            }
        }
    }

    suspend fun userUpdateEvent() {
        update()
        userUpdateEvent = true
    }

    fun userChangeFiltersEvent() {
        userChangeFiltersEvent = true
    }

    private suspend fun firstUpdate() {
        while (currentRandomMovie == null) {
            currentRandomMovie = getFilm()
        }
        while (nextRandomMovie == null) {
            nextRandomMovie = getFilm()
        }
    }

    private suspend fun update() {
        if (filters == null) updateWithoutFilters()
        else updateWithFilters()
    }

    private fun updateWithFilters() {
        TODO()
    }

    private suspend fun updateWithoutFilters() {
        var oldId = nextRandomMovie?.filmId
        currentRandomMovie = nextRandomMovie
        while  (nextRandomMovie == null || oldId == nextRandomMovie?.filmId) {
            nextRandomMovie = getFilm()
        }
    }

    private suspend fun getFilm() : Film? {
        var film: Film? = null
        withContext(IOCoroutineContext) {
        val movieById = repository.requestMovieForUI(calculateRandomId(), arrayOf())
        if (movieById != null) film = Film(movieById.data)
        }
        return film
    }

    fun destroyUpdater() {
        isNeedToStopUpdate = true
        coroutineScope.cancel()
    }

    private fun calculateRandomId() : Int {
        return (1..MAX_ID).random()
    }

    private fun calculateRandomTotalPosition(total: Int) : Int {
        return (1..total).random()
    }
}