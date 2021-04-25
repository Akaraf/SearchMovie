package com.raaf.android.searchmovie.ui.film

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.api.FilmFetcher
import com.raaf.android.searchmovie.dataModel.rootJSON.MovieById
import javax.inject.Inject

class FilmViewModel : ViewModel() {

    @Inject lateinit var filmFetcher: FilmFetcher
    @Inject lateinit var firebaseAnalytics: FirebaseAnalytics
    val movieLiveData: LiveData<MovieById>
    private val mutableMovieId = MutableLiveData<Int>()
    private val arr = arrayOf("BUDGET", "RATING", "REVIEW", "POSTERS")

    init {
        App.appComponent.inject(this)

        movieLiveData = Transformations.switchMap(mutableMovieId) { movieTerm ->
            filmFetcher.fetchMovie(movieTerm, arr, false, "")
        }
    }

    fun fetchFilmById(id: Int) {
        mutableMovieId.value = id
    }

    fun addToDb(id: Int, parent: String) {
        filmFetcher.fetchMovie(id, arr, true, parent)
    }

    fun sendEvent(eventText: String, bundle: Bundle) {
        firebaseAnalytics.logEvent(eventText, bundle)
    }
}