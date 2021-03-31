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
        var nameEn: String = "",
        var posterUrl: String = "",
        var genre: String = "",
        @ColumnInfo(name = "parent_id", index = true) var parentId: String = "",
        var totalPage: Int = 0
) {
    constructor() : this("",0, "", "", "", "", "",0)
}
