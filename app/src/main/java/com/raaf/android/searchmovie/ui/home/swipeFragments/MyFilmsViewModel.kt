package com.raaf.android.searchmovie.ui.home.swipeFragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import com.raaf.android.searchmovie.repository.FilmRepo
import javax.inject.Inject

private const val TAG = "MyFilmsViewModel"

class MyFilmsViewModel : ViewModel() {

    @Inject lateinit var repository: FilmRepo

    val list: LiveData<List<FilmSwipeItem>>
    private val mutableFlag = MutableLiveData<Boolean>()
    private var flag = false

    init {
        App.appComponent.inject(this)

        list = Transformations.switchMap(mutableFlag) {
            repository.setMyFilms()
        }
        Log.d(TAG, "list size = ${list.value?.size ?: 0}")
    }

    fun updateDBData() {
        if (flag) {
            flag = true
            mutableFlag.value = true
        } else {
            flag = true
            mutableFlag.value = false
        }
    }
}