package com.raaf.android.searchmovie.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.Film
import com.squareup.picasso.Picasso

private const val TAG = "FilmAdapter"

class FilmAdapter(private val filmItems: List<Film>) : RecyclerView.Adapter<FilmAdapter.FilmHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_film, parent,false)
        return FilmHolder(view)
    }

    override fun getItemCount(): Int = filmItems.size

    override fun onBindViewHolder(holder: FilmHolder, position: Int) {
        holder.setIsRecyclable(false)
        val filmItem = filmItems[position]
        val imageView = holder.imageFilm
        Picasso.get()
                .load(filmItem.posterUrl)
                .placeholder(R.drawable.splash_screen)
                .error(R.drawable.splash_screen)
                .fit()
                .into(imageView)

        val checkRating = filmItem.rating.toString()
//            First line cardView
        when {
            (filmItem.nameRu!!.isNotBlank() && checkRating != "0.0") -> {
                holder.rating.text = filmItem.rating
                holder.nameRu.text = filmItem.nameRu
            }
            (filmItem.nameRu!!.isNotBlank() && checkRating == "0.0") -> {
                holder.rating.visibility = View.GONE
                holder.nameRu.text = filmItem.nameRu
            }
            (filmItem.nameRu!!.isBlank() && checkRating != "0.0") -> {
                holder.rating.text = filmItem.rating
                holder.nameRu.text = filmItem.nameEn
                if (filmItem.year.isNotBlank()) {
                    holder.nameEn.text = filmItem.year
                } else {
                    holder.nameEn.visibility = View.GONE
                }
            }
            (filmItem.nameRu!!.isBlank() && checkRating == "0.0") -> {
                holder.rating.visibility = View.GONE
                holder.nameRu.text = filmItem.nameEn
                if (filmItem.year.isNotBlank()) {
                    holder.nameEn.text = filmItem.year
                } else {
                    holder.nameEn.visibility = View.GONE
                }
            }
        }

//            Second line cardView
        if (filmItem.nameRu != null && filmItem.nameRu!!.isNotBlank()) {
            var nameEnAndYearText = ""
            var nameEn = ""
            if (filmItem.nameEn != null) {
                nameEn = if (filmItem.nameEn!!.count() > 25) "${filmItem.nameEn!!.substring(0, 26)}.."
                else filmItem.nameEn!!
            }
            var year = filmItem.year
            nameEnAndYearText = if (nameEn != "" && year != "") "$nameEn, $year"
            else "$nameEn$year"
            if (nameEnAndYearText != "") holder.nameEn.text = nameEnAndYearText
            else holder.nameEn.visibility = GONE
        }

//            Third line cardView
        var countries = ""
        if (filmItem.countries.isNotEmpty()) {
            if (filmItem.countries.size > 2) {
                countries += filmItem.countries[0].country
                countries += ", "
                countries += filmItem.countries[1].country
            } else countries += filmItem.countries[0].country
        }
        if (countries != "") holder.country.text = countries
        else {
            holder.country.visibility = View.GONE
            holder.imageDote.visibility = View.GONE
        }

        var genres = ""
        if (filmItem.genres.isNotEmpty()) {
            if (filmItem.genres.size > 2) {
                genres += filmItem.genres[0].genre
                genres += ", "
                genres += filmItem.genres[1].genre
            } else genres += filmItem.genres[0].genre
        }
        if (genres != "") holder.genres.text = genres
        else {
            holder.genres.visibility = View.GONE
            holder.imageDote.visibility = View.GONE
        }

        if (position+1 == itemCount) {
            holder.divider.visibility = GONE
            holder.dividerEnd.visibility = View.VISIBLE
        }
        holder.getFilmId(filmItem.filmId)
    }

    inner class FilmHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val imageFilm: ImageView = itemView.findViewById(R.id.image)
        val nameRu: TextView = itemView.findViewById(R.id.nameRu)
        val rating: TextView = itemView.findViewById(R.id.rating)
        val nameEn: TextView = itemView.findViewById(R.id.name_en_and_year)
        val country: TextView = itemView.findViewById(R.id.country)
        val genres: TextView = itemView.findViewById(R.id.genres)

        val imageDote: ImageView = itemView.findViewById(R.id.imageDote)
        val divider: View = itemView.findViewById(R.id.card_film_divider)
        val dividerEnd: View = itemView.findViewById(R.id.card_film_divider_end)

        var filmId = 0

        init {
            itemView.setOnClickListener(this)
        }

        fun getFilmId(filmId: Int) {
            this.filmId = filmId
        }

        override fun onClick(v: View) {
            var bundle = bundleOf("filmId" to filmId)
            v.findNavController().navigate(R.id.action_global_filmFragment, bundle)
        }

    }
}