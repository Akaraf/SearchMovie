package com.raaf.android.searchmovie.dataModel

data class Releases(
        val filmId : Int,
        val nameRu : String,
        val nameEn : String,
        val year : Int,
        val posterUrl : String,
        val posterUrlPreview : String,
        val countries : List<Countries>,
        val genres : List<Genres>,
        val rating : Double,
        val ratingVoteCount : Int,
        val expectationsRating : String,
        val expectationsRatingVoteCount : Int,
        val duration : Int,
        val releaseDate : String
)
