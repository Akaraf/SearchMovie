package com.raaf.android.searchmovie.ui.film

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.dataModel.Frame
import com.raaf.android.searchmovie.repository.FilmRepo
import javax.inject.Inject

private const val TAG = "FilmFramesViewModel"

class FilmFramesViewModel : ViewModel() {

    @Inject lateinit var repository: FilmRepo

    val framesLiveData: LiveData<List<Frame>>
    private val mutableId = MutableLiveData<Int>()

    init {
        App.appComponent.inject(this)

        framesLiveData = Transformations.switchMap(mutableId) { id->
            repository.fetchFrames(id)
        }
    }

    fun fetchFrames(id: Int) {
        Log.d(TAG, "id is change ($id)")
        mutableId.value = id
    }
}