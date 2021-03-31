package com.raaf.android.searchmovie.dataModel.rootJSON

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.raaf.android.searchmovie.dataModel.Countries
import com.raaf.android.searchmovie.dataModel.Genres

//Maybe, there is have a lists of country and genre
data class FiltersResponse(
        val genres: List<Genres>,
        val countries: List<Countries>
) {
        constructor() : this( listOf(), listOf())
}