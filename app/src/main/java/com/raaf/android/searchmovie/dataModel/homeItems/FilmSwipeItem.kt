package com.raaf.android.searchmovie.dataModel.homeItems

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "filmstop")
data class FilmSwipeItem(
        @PrimaryKey
        var databaseId: String = "",
        var filmId: Int = 0,
        var nameRu: String = "",
        var nameEn: String? = "" ?: "",
        var posterUrl: String = "",
        var genre: String = "",
        var year: String = "",
        var country: String = "",
        var rating: String = "",
        @ColumnInfo(index = true) var forHome: Boolean = false,
        @ColumnInfo(name = "parent_id", index = true) var parentId: String = "",
        var totalPage: Int = 0,//rename to current page
) {
    constructor() : this("",0, "", "", "", "", "", "", "", false, "",0)
}
