package com.raaf.android.searchmovie.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.raaf.android.searchmovie.dataModel.FilmForPersonResponse

private const val TAG = "FilmIdIntConverter"

class FilmIdIntConverter {


    @TypeConverter
    fun fromFilmId(list: List<FilmForPersonResponse>) : String {
        var result = list[0].filmId.toString() + "  " + list[0].professionKey
        list.subList(1, list.lastIndex).forEach { result = result + " ,   " + it.filmId.toString() + "  " + it.professionKey}
        return result
    }

    @TypeConverter
    fun toFilmId(string: String) : List<FilmForPersonResponse> {
        val list = mutableListOf<FilmForPersonResponse>()
        if (string != null && string.isNotEmpty()) string.split(" ,   ").forEach { list.add(FilmForPersonResponse(it.split("  ")[0].toInt(), it.split("  ")[1])) }
        Log.e(TAG, "list:       :" + list.size.toString())
        return list
    }
}