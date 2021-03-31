package com.raaf.android.searchmovie.dataModel

data class Data(
        val filmId : Int,
        val nameRu : String,
        val nameEn : String,
        val webUrl : String,
        val posterUrl : String,
        val posterUrlPreview : String,
        val year : Int,
        val filmLength : String,
        val slogan : String,
        val description : String,
        val type : String,
        val ratingMpaa : String,
        val ratingAgeLimits : Int,
        val premiereRu : String,
        val distributors : String,
        val premiereWorld : String,
        val premiereDigital : String,
        val premiereWorldCountry : String,
        val premiereDvd : String,
        val premiereBluRay : String,
        val distributorRelease : String,
        val countries : List<Countries>,
        val genres : List<Genres>,
        val facts : List<String>,
        val seasons : List<String>
)
