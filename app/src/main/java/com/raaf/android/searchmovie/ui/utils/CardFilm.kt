package com.raaf.android.searchmovie.ui.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.Film
import com.squareup.picasso.Picasso

fun fillCardFilmUI(film: Film,
                   imageFilm: ImageView,
                   nameRu: TextView,
                   nameEn: TextView,
                   rating: TextView,
                   country: TextView,
                   genres: TextView,
                   imageDote: ImageView) {

    Picasso.get()
        .load(film.posterUrl)
        .placeholder(R.drawable.splash_screen)
        .error(R.drawable.splash_screen)
        .fit()
        .into(imageFilm)

    val checkRating = film.rating.toString()
//            First line cardView
    when {
        (film.nameRu != null && film.nameRu!!.isNotBlank() && checkRating != "0.0") -> {
            rating.text = film.rating
            nameRu.text = film.nameRu
        }
        (film.nameRu != null && film.nameRu!!.isNotBlank() && checkRating == "0.0") -> {
            rating.visibility = View.GONE
            nameRu.text = film.nameRu
        }
        (film.nameRu == null || film.nameRu!!.isBlank() && checkRating != "0.0") -> {
            rating.text = film.rating
            nameRu.text = film.nameEn
            if (film.year.isNotBlank()) {
                nameEn.text = film.year
            } else {
                nameEn.visibility = View.GONE
            }
        }
        (film.nameRu == null || film.nameRu!!.isBlank() && checkRating == "0.0") -> {
            rating.visibility = View.GONE
            nameRu.text = film.nameEn
            if (film.year.isNotBlank()) {
                nameEn.text = film.year
            } else {
                nameEn.visibility = View.GONE
            }
        }
    }

//            Second line cardView
    if (film.nameRu != null && film.nameRu!!.isNotBlank()) {
        var nameEnAndYearText = ""
        var nameEnText = ""
        if (film.nameEn != null) {
            nameEnText = if (film.nameEn!!.count() > 25) "${film.nameEn!!.substring(0, 26)}.."
            else film.nameEn!!
        }
        var year = film.year
        nameEnAndYearText = if (nameEnText != "" && year != "") "$nameEnText, $year"
        else "$nameEnText$year"
        if (nameEnAndYearText != "") nameEn.text = nameEnAndYearText
        else nameEn.visibility = View.GONE
    }

//            Third line cardView
    var countries = ""
    if (film.countries.isNotEmpty()) {
        if (film.countries.size > 2) {
            countries += film.countries[0].country
            countries += ", "
            countries += film.countries[1].country
        } else countries += film.countries[0].country
    }
    if (countries != "") country.text = countries
    else {
        country.visibility = View.GONE
        imageDote.visibility = View.GONE
    }

    var genresText = ""
    if (film.genres.isNotEmpty()) {
        if (film.genres.size > 2) {
            genresText += film.genres[0].genre
            genresText += ", "
            genresText += film.genres[1].genre
        } else genresText += film.genres[0].genre
    }
    if (genresText != "") genres.text = genresText
    else {
        genres.visibility = View.GONE
        imageDote.visibility = View.GONE
    }
}