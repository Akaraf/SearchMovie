package com.raaf.android.searchmovie.dataModel

data class Rating(
        val rating : Double,
        val ratingVoteCount : Int,
        val ratingImdb : String,
        val ratingImdbVoteCount : Int,
        val ratingFilmCritics : String,
        val ratingFilmCriticsVoteCount : Int,
        val ratingAwait : String,
        val ratingAwaitCount : Int,
        val ratingRfCritics : String,
        val ratingRfCriticsVoteCount : Int
)
