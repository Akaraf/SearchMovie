package com.raaf.android.searchmovie.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.util.stream.Collectors

class ListStringConverter {

    @RequiresApi(Build.VERSION_CODES.N)
    @TypeConverter
    fun fromList(list: List<String>) : String {
        return list.stream().collect(Collectors.joining(" ,  "))
    }

    @TypeConverter
    fun toList(string: String) : List<String> {
        return string.split(" ,  ")
    }
}