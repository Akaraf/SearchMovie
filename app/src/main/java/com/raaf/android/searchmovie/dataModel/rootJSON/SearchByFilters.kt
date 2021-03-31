package com.raaf.android.searchmovie.dataModel.rootJSON

import com.raaf.android.searchmovie.dataModel.Film

//  For 8 and 9 requests
data class SearchByFilters(
        var pagesCount: Int,
        var films: List<Film>
)
