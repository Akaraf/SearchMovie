package com.raaf.android.searchmovie.ui.search.categoryForSearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.dataModel.CategoryItem
import com.raaf.android.searchmovie.repository.FilmRepo
import javax.inject.Inject

class CategoryForSearchViewModel : ViewModel() {

    @Inject lateinit var repository: FilmRepo
    var categoryNameLV: MutableLiveData<String> = MutableLiveData()
    var categoryItemsLiveData: LiveData<List<CategoryItem>>

    init {
        App.appComponent.inject(this)
        categoryItemsLiveData = Transformations.switchMap(categoryNameLV) {
            repository.getCategoryItems(it)
        }
    }

    fun setCategoryName(name: String) {
        categoryNameLV.value = name
    }
}