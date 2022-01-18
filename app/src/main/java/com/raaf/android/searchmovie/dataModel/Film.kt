package com.raaf.android.searchmovie.dataModel

data class Film(
        var filmId : Int = 0,
        var nameRu : String? = "",
        var nameEn : String? = "",
        var year : String = "",
        var filmLength : String? = "",
        var countries : List<Countries> = listOf(),
        var genres : List<Genres> = listOf(),
        var rating : String? = "",
        var ratingVoteCount : Int? = 0,
        var posterUrl : String = "",
        var posterUrlPreview : String = ""
) {
        constructor(data: Data) : this(
                data.filmId,
                data.nameRu,
                data.nameEn,
                data.year.toString(),
                data.filmLength,
                data.countries,
                data.genres,
                data.ratingMpaa,
                0,
                data.posterUrl,
                data.posterUrlPreview)
}
