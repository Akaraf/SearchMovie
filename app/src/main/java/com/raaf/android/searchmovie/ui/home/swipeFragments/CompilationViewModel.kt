package com.raaf.android.searchmovie.ui.home.swipeFragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import com.raaf.android.searchmovie.repository.AppRepository
import javax.inject.Inject

class CompilationViewModel : ViewModel() {

    @Inject lateinit var appRepository: AppRepository
    var list: LiveData<List<FilmSwipeItem>>

    init {
        App.appComponent.inject(this)

        list = appRepository.setCompilation()
    }
}