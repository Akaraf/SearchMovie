package com.raaf.android.searchmovie.ui.film

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.api.FilmFetcher
import com.raaf.android.searchmovie.dataModel.rootJSON.SequelsPrequelsResponse
import javax.inject.Inject


class SequelsPrequelsViewModel : ViewModel() {

    @Inject lateinit var filmFetcher: FilmFetcher

    val movieLiveData: LiveData<List<SequelsPrequelsResponse>>
    private val mutableMovieId = MutableLiveData<Int>()

    init {
        App.appComponent.inject(this)

        movieLiveData = Transformations.switchMap(mutableMovieId) { filmId ->
            filmFetcher.fetchSequelsAndPrequelsByFilmId(filmId)
        }
    }

    fun fetchFilmId(id: Int) {
        mutableMovieId.value = id
    }
}