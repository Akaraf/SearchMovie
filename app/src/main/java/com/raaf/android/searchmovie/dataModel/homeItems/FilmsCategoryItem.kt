package com.raaf.android.searchmovie.dataModel.homeItems


import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


data class FilmsCategoryItem(
        var categoryName: String,
        var filmItemList: List<FilmSwipeItem>
) {
    constructor() : this("", listOf())
}
