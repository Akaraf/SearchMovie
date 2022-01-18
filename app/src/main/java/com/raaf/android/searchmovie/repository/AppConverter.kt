package com.raaf.android.searchmovie.repository

import android.content.Context
import android.util.Log
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.*
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import com.raaf.android.searchmovie.dataModel.rootJSON.MovieById

private const val TAG = "AppConverter"

class AppConverter(val context: Context) {
    val nameCompilation = listOf(context.getString(R.string.comedy), context.getString(R.string.fantasy), context.getString(
        R.string.cartoon), context.getString(R.string.drama), context.getString(R.string.action))

    fun parseMovieByIdToMovie(movieById: MovieById, parentName: String) : Movie {
        var item = Movie()
        item.filmId = movieById.data.filmId
        item.nameRu = movieById.data.nameRu ?: ""
        item.nameEn = movieById.data.nameEn ?: ""
        item.webUrl = movieById.data.webUrl
        item.posterUrl = movieById.data.posterUrl
        item.posterUrlPreview = movieById.data.posterUrlPreview
        item.year = movieById.data.year
        item.filmLength = movieById.data.filmLength ?: ""
        item.slogan = movieById.data.slogan ?: ""
        item.description = movieById.data.description ?: ""
        item.ratingMpaa = movieById.data.ratingMpaa ?: ""
        item.rating = movieById.rating.rating.toString()
        item.ratingAgeLimits = movieById.data.ratingAgeLimits
        item.premiereRu = movieById.data.premiereRu ?: ""
        item.distributors = movieById.data.distributors ?: ""
        item.premiereWorld = movieById.data.premiereWorld ?: ""
        item.premiereDigital = movieById.data.premiereDigital ?: ""
        item.premiereWorldCountry = movieById.data.premiereWorldCountry ?: ""
        item.distributorRelease = movieById.data.distributorRelease ?: ""
        item.facts = movieById.data.facts
        item.parent = parentName
        item.endsId = parentName + movieById.data.filmId
        return item
    }

    fun parseMoviesToFilmSwipeItem(movies: List<Movie>, parentName: String) : List<FilmSwipeItem> {
        var list = mutableListOf<FilmSwipeItem>()
        for (count in movies) {
            var item = FilmSwipeItem()
            if (parentName != "") item.databaseId = count.filmId.toString() + parentName
            item.filmId = count.filmId
            item.nameRu = count.nameRu ?: ""
            item.nameEn = count.nameEn ?: ""
            if (count.genres.isNotEmpty()) item.genre = count.genres[0]
            if (count.countries.isNotEmpty()) item.country = count.countries[0]
            item.year = count.year.toString()
            item.rating = count.rating
            item.posterUrl = count.posterUrlPreview
            item.parentId = count.parent
            item.totalPage = 0
            list.add(item)
        }
        return list
    }

    fun parseFilmsToFilmSwipeItem(films: List<Film>, parentName: String, page: Int, isForHome: Boolean) : MutableList<FilmSwipeItem> {
        var list = mutableListOf<FilmSwipeItem>()
        for (count in films) {
            var item = FilmSwipeItem()
            if (nameCompilation.toString().contains(parentName)) item.databaseId = count.filmId.toString()
            else item.databaseId = count.filmId.toString() + parentName
            item.filmId = count.filmId
            item.nameRu = count.nameRu ?: ""
            item.nameEn = count.nameEn ?: ""
            var genre = ""
            genre += count.genres[0].genre
            item.genre = genre
            if (count.rating != null ) item.rating = count.rating!!
            else item.rating = ""
            if (item.country.isNotBlank()) item.country = count.countries[0].country
            item.year = count.year ?:""
            item.posterUrl = count.posterUrlPreview
            item.parentId = parentName
            item.forHome = isForHome
            item.totalPage = page
            list.add(item)
        }
        return list
    }

    fun parseFramesToFrame(frames: List<Frames>) : List<Frame> {
        var list = mutableListOf<Frame>()
        for ((countId, count) in frames.withIndex()) {
            var item = Frame()
            item.url = count.image
            item.id = countId
            list.add(item)
        }
        return list
    }

    fun parseReleasesToFilmSwipeItem(releases: List<Releases>, total: Int) : MutableList<FilmSwipeItem>{
        var list = mutableListOf<FilmSwipeItem>()
        for (count in releases) {
            var item = FilmSwipeItem()
            item.databaseId = "DigitalReleases${count.filmId}"
            item.filmId = count.filmId
            item.nameRu = count.nameRu ?:""
            item.nameEn = count.nameEn ?:""
            item.posterUrl = count.posterUrlPreview
            if (item.genre.isNotEmpty()) item.genre = count.genres[0].genre
            else item.genre = ""
            item.year = count.releaseDate
            if(count.countries.isNotEmpty()) item.country = count.countries[0].country
            else item.country = ""
            item.rating = count.rating.toString()
            item.totalPage = total/10 + 1
            list.add(item)
        }
        return list
    }
}