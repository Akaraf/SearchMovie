package com.raaf.android.searchmovie.dataModel.homeItems

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithSwipe(
        var categoryItem: FilmsCategoryItem,
        var swipeItems: List<FilmSwipeItem>
)
