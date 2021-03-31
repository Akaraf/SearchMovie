package com.raaf.android.searchmovie.dataModel.rootJSON

import com.raaf.android.searchmovie.dataModel.Releases

data class DigitalReleases(
        val releases : List<Releases>,
        val page : Int,
        val total : Int
)
