package com.raaf.android.searchmovie.repository

import android.util.Log
import com.raaf.android.searchmovie.dataModel.Film
import com.raaf.android.searchmovie.dataModel.Movie
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import com.raaf.android.searchmovie.dataModel.rootJSON.MovieById

private const val TAG = "AppConverter"

class AppConverter {

    fun parseMovieById(movieById: MovieById, parentId: String) : Movie {
        var item = Movie()
        item.DBId = movieById.data.filmId.toString() + parentId
        item.filmId = movieById.data.filmId
        item.nameRu = movieById.data.nameRu
        item.nameEn = movieById.data.nameEn
        item.webUrl = movieById.data.webUrl
        item.posterUrl = movieById.data.posterUrl
        item.posterUrlPreview = movieById.data.posterUrlPreview
        item.year = movieById.data.year
        item.filmLength = movieById.data.filmLength ?: ""
        item.slogan = movieById.data.slogan ?: ""
        item.description = movieById.data.description ?: ""
        item.ratingMpaa = movieById.data.ratingMpaa ?: ""
        item.ratingAgeLimits = movieById.data.ratingAgeLimits
        item.premiereRu = movieById.data.premiereRu ?: ""
        item.distributors = movieById.data.distributors ?: ""
        item.premiereWorld = movieById.data.premiereWorld ?: ""
        item.premiereDigital = movieById.data.premiereDigital ?: ""
        item.premiereWorldCountry = movieById.data.premiereWorldCountry ?: ""
        item.distributorRelease = movieById.data.distributorRelease ?: ""
//        item.countries = movieById.data.countries.forEach { += it.toString() }
//        item.genres = movieById.data.genres.forEach { += it.toString() }
        item.facts = movieById.data.facts
        item.seasons = movieById.data.seasons
        item.parent = parentId
        return item
    }

    fun parseMovies(movies: List<Movie>) : List<FilmSwipeItem> {
        var list = mutableListOf<FilmSwipeItem>()
        for (count in movies) {
            var item = FilmSwipeItem()
            item.databaseId = count.DBId
            item.filmId = count.filmId
            item.nameRu = count.nameRu ?: ""
            item.nameEn = count.nameEn ?: ""
            item.genre = count.genres[0].toString()
            item.posterUrl = count.posterUrlPreview
            item.parentId = count.parent
            item.totalPage = 0
            list.add(item)
        }
        Log.e(TAG, "parseMovies : ${list.size}")
        return list
    }

    fun parseFilms(films: List<Film>, parentId: String, page: Int) : MutableList<FilmSwipeItem> {
        var list = mutableListOf<FilmSwipeItem>()
        for (count in films) {
            var item = FilmSwipeItem()
            item.databaseId = count.filmId.toString() + parentId
            item.filmId = count.filmId
            item.nameRu = count.nameRu ?: ""
            item.nameEn = count.nameEn ?: ""
            item.genre = count.genres[0].toString()
            item.posterUrl = count.posterUrlPreview
            item.parentId = parentId
            item.totalPage = page
            list.add(item)
        }
        Log.e(TAG, "parseFilms : ${list.size}")
        return list
    }
}