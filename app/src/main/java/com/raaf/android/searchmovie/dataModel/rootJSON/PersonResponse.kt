package com.raaf.android.searchmovie.dataModel.rootJSON

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.raaf.android.searchmovie.dataModel.FilmForPersonResponse
import com.raaf.android.searchmovie.dataModel.Spouse
import com.raaf.android.searchmovie.repository.FilmIdIntConverter
import com.raaf.android.searchmovie.repository.ListStringConverter
import kotlinx.parcelize.Parcelize

@Entity(tableName = "persons")
@TypeConverters(ListStringConverter::class, FilmIdIntConverter::class)
@Parcelize
data class PersonResponse(
        @PrimaryKey
        var personId: Int,
        var webUrl: String,
        var nameRu: String,
        var nameEn: String,
        var sex: String, //[ MALE, FEMALE ]
        var posterUrl: String,
        var growth: String,
        var birthday: String?,
        var death: String?,
        var age: Int?,
        var birthplace: String?,
        var profession: String,
        var facts: List<String>,
        @Ignore
        var spouses: List<Spouse>,
        var films: List<FilmForPersonResponse>
) : Parcelable {
    constructor() : this(0, "", "", "","", "","","", "",0, "", "", listOf(), listOf<Spouse>(), listOf<FilmForPersonResponse>())
}