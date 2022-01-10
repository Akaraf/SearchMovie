package com.raaf.android.searchmovie.ui.search

import androidx.lifecycle.*
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.dataModel.StaffPerson
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import com.raaf.android.searchmovie.repository.FilmRepo
import javax.inject.Inject

private const val TAG = "SearchViewModel"

class SearchViewModel() : ViewModel() {

    @Inject lateinit var repository: FilmRepo
    var releasesLiveData: LiveData<List<FilmSwipeItem>>
    var popularPersonLiveData: LiveData<List<StaffPerson>>

    init {
        App.appComponent.inject(this)

        releasesLiveData = repository.setReleases()
        popularPersonLiveData = repository.getPopularPersons()
    }
}