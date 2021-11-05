package com.raaf.android.searchmovie.dataModel.rootJSON

import com.raaf.android.searchmovie.dataModel.SimilarFilm

data class RelatedFilmResponse(
    val total: Int,
    val items: List<SimilarFilm>
)
