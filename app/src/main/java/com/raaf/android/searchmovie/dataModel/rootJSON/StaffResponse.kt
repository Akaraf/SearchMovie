package com.raaf.android.searchmovie.dataModel.rootJSON

import com.raaf.android.searchmovie.dataModel.StaffPerson

data class StaffResponse(
        val listPersons: List<StaffPerson>
) {
        constructor() : this(listOf())
}
