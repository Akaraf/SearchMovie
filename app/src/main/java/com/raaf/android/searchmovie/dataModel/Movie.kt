package com.raaf.android.searchmovie.dataModel

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.raaf.android.searchmovie.repository.ListStringConverter

@Entity(tableName = "films")
@TypeConverters(ListStringConverter::class)
data class Movie(
        @PrimaryKey
        var DBId: String,
        var filmId: Int,
        var nameRu : String,
        var nameEn : String,
        var webUrl : String,
        var posterUrl : String,
        var posterUrlPreview : String,
        var year : Int,
        var filmLength : String,
        var slogan : String,
        var description : String,
        var ratingMpaa : String,
        var ratingAgeLimits : Int,
        var premiereRu : String,
        var distributors : String,
        var premiereWorld : String,
        var premiereDigital : String,
        var premiereWorldCountry : String,
        var distributorRelease : String,
        var countries : List<String>,
        var genres : List<String>,
        var facts : List<String>,
        var seasons : List<String>,
        var parent: String

) {
    constructor() : this("",0, "","","","","",0, "","","","",0,"","","","","","", listOf(),listOf(),listOf(),listOf(),"")
}