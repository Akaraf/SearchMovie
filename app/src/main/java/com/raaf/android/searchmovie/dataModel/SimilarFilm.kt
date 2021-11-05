package com.raaf.android.searchmovie.dataModel

data class SimilarFilm(
    val filmId: Int,
    val nameRu: String,
    val nameEn: String,
    val posterUrl: String,
    val posterUrlPreview: String
) {
    constructor() : this(0, "", "", "", "")
}
