package com.raaf.android.searchmovie.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.api.FilmFetcher
import javax.inject.Inject

class HomeViewModel : ViewModel() {

    @Inject lateinit var filmFetcher: FilmFetcher

    init {
        App.appComponent.inject(this)
    }
}