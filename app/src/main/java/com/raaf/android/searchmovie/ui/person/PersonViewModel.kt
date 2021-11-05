package com.raaf.android.searchmovie.ui.person

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.api.FilmFetcher
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import com.raaf.android.searchmovie.dataModel.rootJSON.PersonResponse
import javax.inject.Inject

private const val TAG = "PersonViewModel"

class PersonViewModel : ViewModel() {

    @Inject lateinit var filmFetcher: FilmFetcher
    val personLiveData: LiveData<PersonResponse>
    private val personId = MutableLiveData<Int>()
    val bestWorksLiveData: LiveData<List<FilmSwipeItem>>
    private val filmsId = MutableLiveData<List<Int>>()
    var statusFloatingButton: LiveData<Boolean> = MutableLiveData()

    init {
        App.appComponent.inject(this)

        personLiveData = Transformations.switchMap(personId) { personId ->
            filmFetcher.fetchPersonById(personId, false)
        }

        bestWorksLiveData = Transformations.switchMap(filmsId) { list ->
            filmFetcher.getBestWorksForPerson()
        }

        statusFloatingButton = Transformations.switchMap(personId) { id ->
            filmFetcher.checkPersonInMyPersonDB(id)
        }
    }

    fun fetchBestWorks(list: List<Int>) {
        filmFetcher.setBestWorksForPerson(list)
        filmsId.value = list
    }

    fun fetchPersonById(id: Int) {
        personId.value = id
    }

    fun clearBestWorksCache() {
        filmFetcher.clearPersonCache()
    }

    fun addToMyPerson(personId: Int) {
        //Log.e(TAG, personResponse.toString())
        filmFetcher.fetchPersonById(personId, true)
    }

    fun deleteToMyPerson(id: Int) {
        Log.e(TAG, id.toString())
        filmFetcher.deletePersonFromMyPersonsDB(id)
    }

    fun checkSpouse(personId: Int) : Boolean {
        return filmFetcher.checkSpouse(personId)
    }
}