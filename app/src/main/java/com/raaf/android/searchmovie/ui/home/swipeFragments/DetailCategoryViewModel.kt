package com.raaf.android.searchmovie.ui.home.swipeFragments

import androidx.lifecycle.ViewModel
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.repository.AppRepository
import javax.inject.Inject

class DetailCategoryViewModel : ViewModel() {

    @Inject lateinit var appRepository: AppRepository

    init {
        App.appComponent.inject(this)
    }
}