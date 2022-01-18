package com.raaf.android.searchmovie.ui.film

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.raaf.android.searchmovie.dataModel.Movie
import com.raaf.android.searchmovie.dataModel.SimilarFilm
import com.raaf.android.searchmovie.dataModel.StaffPerson
import com.raaf.android.searchmovie.dataModel.rootJSON.MovieById
import com.raaf.android.searchmovie.repository.AppConverter
import com.raaf.android.searchmovie.repository.FilmRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*

private const val EXTRA_MOVIE_ID = "filmId"

class FilmViewModel @AssistedInject constructor(
    private val repository: FilmRepo,
    private val firebaseAnalytics: FirebaseAnalytics,
    private val appConverter: AppConverter,
    @Assisted private val handle: SavedStateHandle
) : ViewModel() {

    private val arr = arrayOf("BUDGET", "RATING", "REVIEW", "POSTERS")
    private var movieId: Int = 0

    val actors: Flow<List<StaffPerson>> = flow {
        emit(repository.requestStaffByMovieId(movieId))
    }
    val similarMovies: Flow<List<SimilarFilm>> = flow {
        emit(repository.requestSimilarFilmsByMovieId(movieId))
    }

    val favoriteStatus: SharedFlow<Boolean> = flow {
        emit(repository.checkMovieInMyFilmsDB(1, movieId))
    }.shareIn(viewModelScope, SharingStarted.Lazily, 1)

    val watchLaterStatus: SharedFlow<Boolean> = flow {
        emit(repository.checkMovieInMyFilmsDB(0, movieId))
    }.shareIn(viewModelScope, SharingStarted.Lazily, 1)

    fun isMovieIdReceived() : Boolean {
        return movieId != 0
    }

    fun setMovieId(bundleMovieId: Int?) {
        movieId = bundleMovieId ?: handle.get<Int?>(EXTRA_MOVIE_ID) ?: 0
    }

    fun saveMovieId() {
        handle.set(EXTRA_MOVIE_ID, movieId)
    }

    fun getMovieId() : Int = movieId

    suspend fun getMovieById() : MovieById? {
        return repository.requestMovieForUI(movieId, arr)
    }

    fun addToDB(parent: String) {
        repository.fetchMovie(movieId, arr, true, parent)
    }

    fun deleteFromDB(addVariant: String) {
        repository.deleteMovieFromMyFilmsDb(addVariant + movieId.toString())
    }

    fun addToWatched(movieById: MovieById) {
        repository.addToWatchedDb(movieById)
    }

    fun sendEvent(eventText: String, bundle: Bundle) {
        firebaseAnalytics.logEvent(eventText, bundle)
    }

    fun parseForFirebase(item: MovieById, parent: String) : Movie {
        return  appConverter.parseMovieByIdToMovie(item, parent)
    }

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle) : FilmViewModel
    }
}