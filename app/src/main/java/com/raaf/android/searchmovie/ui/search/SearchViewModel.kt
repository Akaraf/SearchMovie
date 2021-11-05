package com.raaf.android.searchmovie.ui.search

import android.util.Log
import androidx.lifecycle.*
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.api.FilmFetcher
import com.raaf.android.searchmovie.dataModel.StaffPerson
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import java.util.*
import javax.inject.Inject

private const val TAG = "SearchViewModel"

class SearchViewModel() : ViewModel() {

    @Inject lateinit var filmFetcher: FilmFetcher
    var releasesLiveData: LiveData<List<FilmSwipeItem>>
    var popularPersonLiveData: LiveData<List<StaffPerson>>

    init {
        App.appComponent.inject(this)

        releasesLiveData = filmFetcher.setReleases()
        popularPersonLiveData = filmFetcher.getPopularPersons()
    }
}