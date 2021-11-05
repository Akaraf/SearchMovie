package com.raaf.android.searchmovie.ui.film

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.api.FilmFetcher
import com.raaf.android.searchmovie.dataModel.Movie
import com.raaf.android.searchmovie.dataModel.SimilarFilm
import com.raaf.android.searchmovie.dataModel.StaffPerson
import com.raaf.android.searchmovie.dataModel.rootJSON.MovieById
import com.raaf.android.searchmovie.repository.AppConverter
import javax.inject.Inject

class FilmViewModel : ViewModel() {

    @Inject lateinit var filmFetcher: FilmFetcher
    @Inject lateinit var firebaseAnalytics: FirebaseAnalytics
    @Inject lateinit var appConverter: AppConverter
    val movieLiveData: LiveData<MovieById>
    val actorsLiveData: LiveData<List<StaffPerson>>
    val similarLiveData: LiveData<List<SimilarFilm>>
    var statusWatchLaterCB: LiveData<Boolean> = MutableLiveData()
    var statusFavoriteCB: LiveData<Boolean> = MutableLiveData()
    private val mutableMovieId = MutableLiveData<Int>()
    private val arr = arrayOf("BUDGET", "RATING", "REVIEW", "POSTERS")

    init {
        App.appComponent.inject(this)

        movieLiveData = Transformations.switchMap(mutableMovieId) { movieTerm ->
            filmFetcher.fetchMovie(movieTerm, arr, false, "")
        }

        actorsLiveData = Transformations.switchMap(mutableMovieId) { id ->
            filmFetcher.fetchStaffByIdForUI(id)
        }

        similarLiveData = Transformations.switchMap(mutableMovieId) { id ->
            filmFetcher.fetchSimilarFilmsByFilmId(id)
        }

        statusFavoriteCB = Transformations.switchMap(mutableMovieId) { id ->
            filmFetcher.checkMovieInMyFilmsDB(1, id)
        }

        statusWatchLaterCB = Transformations.switchMap(mutableMovieId) { id ->
            filmFetcher.checkMovieInMyFilmsDB(0, id)
        }
    }

    fun fetchFilmById(id: Int) {
        mutableMovieId.value = id
    }

    fun addToDB(id: Int, parent: String) {
        filmFetcher.fetchMovie(id, arr, true, parent)
    }

    fun deleteFromDB(endsId: String) {
        filmFetcher.deleteMovieFromMyFilmsDb(endsId)
    }

    fun addToWatched(movieById: MovieById) {
        filmFetcher.addToWatchedDb(movieById)
    }

    fun sendEvent(eventText: String, bundle: Bundle) {
        firebaseAnalytics.logEvent(eventText, bundle)
    }

    fun parseForFirebase(item: MovieById, parent: String) : Movie {
        return  appConverter.parseMovieByIdToMovie(item, parent)
    }
}