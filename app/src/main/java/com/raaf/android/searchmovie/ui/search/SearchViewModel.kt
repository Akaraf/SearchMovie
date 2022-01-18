package com.raaf.android.searchmovie.ui.search

import androidx.lifecycle.*
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.dataModel.Film
import com.raaf.android.searchmovie.dataModel.StaffPerson
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import com.raaf.android.searchmovie.repository.FilmRepo
import com.raaf.android.searchmovie.repository.RandomMovieUpdater
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private const val TAG = "SearchViewModel"

class SearchViewModel @AssistedInject constructor(
    private val repository: FilmRepo,
    val randomMovieUpdater: RandomMovieUpdater,
    @Assisted private val handle: SavedStateHandle
) : ViewModel() {

    var releasesLiveData: LiveData<List<FilmSwipeItem>>
    var popularPersonLiveData: LiveData<List<StaffPerson>>

    val randomMovie: MutableSharedFlow<Film?> = MutableStateFlow(randomMovieUpdater.currentRandomMovie)

    init {
        releasesLiveData = repository.setReleases()
        popularPersonLiveData = repository.getPopularPersons()
    }

    suspend fun newRandomMovie() {
        randomMovieUpdater.userUpdateEvent()
    }

    fun makeChangeFiltersEvent() {
        randomMovieUpdater.userChangeFiltersEvent()
    }

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle) : SearchViewModel
    }

    override fun onCleared() {
        super.onCleared()
        randomMovieUpdater.destroyUpdater()
    }
}