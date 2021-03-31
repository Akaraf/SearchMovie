package com.raaf.android.searchmovie.dataModel.rootJSON

import com.raaf.android.searchmovie.dataModel.Film

data class SearchByKeyword (
    val keyword: String,
    val pagesCount: Int,
    val searchFilmsCountResult: Int,
    val films: List<Film>
) {
    constructor() : this("", 0, 0, listOf())
}