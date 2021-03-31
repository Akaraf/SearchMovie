package com.raaf.android.searchmovie.dataModel.rootJSON

import com.raaf.android.searchmovie.dataModel.*

data class MovieById(
        val data : Data,
        val externalId : ExternalId,
        val rating : Rating,
        val budget : Budget,
        val review : Review,
        val images : Images
)
