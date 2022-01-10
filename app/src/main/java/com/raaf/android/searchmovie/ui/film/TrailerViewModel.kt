package com.raaf.android.searchmovie.ui.film

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.dataModel.Trailer
import com.raaf.android.searchmovie.repository.FilmRepo
import javax.inject.Inject

class TrailerViewModel : ViewModel() {

    @Inject lateinit var repository: FilmRepo

    val trailersLiveData: LiveData<List<Trailer>>
    private val mutableId = MutableLiveData<Int>()

    init {
        App.appComponent.inject(this)

        trailersLiveData = Transformations.switchMap(mutableId) { id->
            repository.fetchTrailers(id)
        }
    }

    fun fetchTrailers(id: Int) {
        mutableId.value = id
    }
}