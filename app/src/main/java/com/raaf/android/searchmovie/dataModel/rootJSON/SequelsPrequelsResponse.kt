package com.raaf.android.searchmovie.dataModel.rootJSON

data class SequelsPrequelsResponse(
    val filmId: Int,
    val nameRu: String,
    val nameEn: String,
    val posterUrl: String,
    val posterUrlPreview: String,
    var relationType: String
)