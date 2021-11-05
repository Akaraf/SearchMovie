package com.raaf.android.searchmovie.dataModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "staff_persons")
data class StaffPerson(
        @PrimaryKey
        val staffId: Int,
        val nameRu: String,
        val nameEn: String,
        val description: String?,
        val posterUrl: String,
        val professionText: String,
        val professionKey: String //[ WRITER, OPERATOR, EDITOR, COMPOSER, PRODUCER_USSR, TRANSLATOR, DIRECTOR, DESIGN, PRODUCER, ACTOR, VOICE_DIRECTOR, UNKNOWN ]
) {
    constructor() : this(0, "", "", "", "", "", "")
}