package com.raaf.android.searchmovie.ui.person

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import com.raaf.android.searchmovie.dataModel.rootJSON.PersonResponse
import com.raaf.android.searchmovie.repository.FilmRepo
import javax.inject.Inject

private const val TAG = "PersonViewModel"

class PersonViewModel : ViewModel() {

    @Inject lateinit var repository: FilmRepo
    val personLiveData: LiveData<PersonResponse>
    private val personId = MutableLiveData<Int>()
    val bestWorksLiveData: LiveData<List<FilmSwipeItem>>
    private val filmsId = MutableLiveData<List<Int>>()
    var statusFloatingButton: LiveData<Boolean> = MutableLiveData()

    init {
        App.appComponent.inject(this)

        personLiveData = Transformations.switchMap(personId) { personId ->
            repository.fetchPersonById(personId, false)
        }

        bestWorksLiveData = Transformations.switchMap(filmsId) { list ->
            repository.getBestWorksForPerson()
        }

        statusFloatingButton = Transformations.switchMap(personId) { id ->
            repository.checkPersonInMyPersonDB(id)
        }
    }

    fun fetchBestWorks(list: List<Int>) {
        repository.setBestWorksForPerson(list)
        filmsId.value = list
    }

    fun fetchPersonById(id: Int) {
        personId.value = id
    }

    fun clearBestWorksCache() {
        repository.clearPersonCache()
    }

    fun addToMyPerson(personId: Int) {
        //Log.e(TAG, personResponse.toString())
        repository.fetchPersonById(personId, true)
    }

    fun deleteToMyPerson(id: Int) {
        Log.e(TAG, id.toString())
        repository.deletePersonFromMyPersonsDB(id)
    }

    fun checkSpouse(personId: Int) : Boolean {
        return repository.checkSpouse(personId)
    }
}