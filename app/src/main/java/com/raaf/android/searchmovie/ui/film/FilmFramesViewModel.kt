package com.raaf.android.searchmovie.ui.film

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.api.FilmFetcher
import com.raaf.android.searchmovie.dataModel.Frame
import javax.inject.Inject

private const val TAG = "FilmFramesViewModel"

class FilmFramesViewModel : ViewModel() {

    @Inject lateinit var filmFetcher: FilmFetcher

    val framesLiveData: LiveData<List<Frame>>
    private val mutableId = MutableLiveData<Int>()

    init {
        App.appComponent.inject(this)

        framesLiveData = Transformations.switchMap(mutableId) { id->
            filmFetcher.fetchFrames(id)
        }
    }

    fun fetchFrames(id: Int) {
        Log.d(TAG, "id is change ($id)")
        mutableId.value = id
    }
}