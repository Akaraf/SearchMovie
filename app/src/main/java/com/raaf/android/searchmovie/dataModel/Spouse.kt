package com.raaf.android.searchmovie.dataModel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Spouse(
    var personId: Int? = null,
    var name: String? = null,
    var divorced: Boolean? = null,
    var children: Int? = null,
) : Parcelable
