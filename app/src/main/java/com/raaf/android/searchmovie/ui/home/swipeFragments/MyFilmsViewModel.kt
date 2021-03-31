package com.raaf.android.searchmovie.ui.home.swipeFragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.api.FilmFetcher
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import com.raaf.android.searchmovie.repository.AppRepository
import javax.inject.Inject

private const val TAG = "MyFilmsViewModel"

class MyFilmsViewModel : ViewModel() {

    @Inject lateinit var appRepository: AppRepository

    var list: LiveData<List<FilmSwipeItem>>

    init {
        App.appComponent.inject(this)

        list = appRepository.setMyFilms()
        Log.d(TAG, "list size = ${list.value?.size ?: 0}")
    }
}