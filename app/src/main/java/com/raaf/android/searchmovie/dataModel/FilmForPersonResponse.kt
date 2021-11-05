package com.raaf.android.searchmovie.dataModel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilmForPersonResponse(
        val filmId: Int = 0,
        val professionKey: String = ""
) : Parcelable