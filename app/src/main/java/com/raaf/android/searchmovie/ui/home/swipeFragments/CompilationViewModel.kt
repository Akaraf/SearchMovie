package com.raaf.android.searchmovie.ui.home.swipeFragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.api.FilmFetcher
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import javax.inject.Inject

class CompilationViewModel : ViewModel() {

    @Inject lateinit var filmFetcher: FilmFetcher
    var list: LiveData<List<FilmSwipeItem>>

    init {
        App.appComponent.inject(this)

        list = filmFetcher.setCompilationForHome()
    }
}