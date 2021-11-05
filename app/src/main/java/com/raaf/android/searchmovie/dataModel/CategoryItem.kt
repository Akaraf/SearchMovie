package com.raaf.android.searchmovie.dataModel

import androidx.room.Entity
import androidx.room.PrimaryKey

//Мб добавить праметры для запроса еще

@Entity(tableName = "categories")
data class CategoryItem(
    @PrimaryKey
    var itemName: String,
    var categoryName: String,
    var frameUrl: String?
) {
    constructor() : this("", "", "")
}
