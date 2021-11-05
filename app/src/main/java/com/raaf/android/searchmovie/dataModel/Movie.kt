package com.raaf.android.searchmovie.dataModel

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.raaf.android.searchmovie.repository.ListStringConverter
import kotlinx.parcelize.Parcelize


@Entity(tableName = "myfilms", indices = [Index(value = ["endsId"], unique = true)])
@TypeConverters(ListStringConverter::class)
@Parcelize
data class Movie(
        @PrimaryKey(autoGenerate = true)
        var DBId: Long = 0,
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
        var rating : String,
        //var seasons : List<String>,
        var parent: String,
        var endsId: String,
) : Parcelable {
    constructor() : this(0,0, "","","","","",0, "","","","",0,"","","","","","", listOf(),listOf(),listOf(), "","", "")
}