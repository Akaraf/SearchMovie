package com.raaf.android.searchmovie.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.api.FilmFetcher
import com.raaf.android.searchmovie.dataModel.rootJSON.SearchByKeyword
import javax.inject.Inject

class SearchResultViewModel : ViewModel() {

    @Inject lateinit var filmFetcher: FilmFetcher
    val resultSearchLiveData: LiveData<SearchByKeyword>
    private val mutableSearchTermKeyword = MutableLiveData<String>()
    private val mutableSearchTermPage = MutableLiveData<Int>()

    init {
        App.appComponent.inject(this)

        resultSearchLiveData = Transformations.switchMap(mutableSearchTermKeyword) { searchTerm ->
            filmFetcher.getSearchByKeyword(searchTerm, mutableSearchTermPage.value?: 1)
        }
    }

    fun fetchFilms(query: String, page: Int) {
        mutableSearchTermKeyword.value = query
        mutableSearchTermPage.value = page
    }
}