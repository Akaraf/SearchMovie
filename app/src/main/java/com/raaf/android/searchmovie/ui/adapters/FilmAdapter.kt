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
import com.raaf.android.searchmovie.ui.utils.fillCardFilmUI
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
        fillCardFilmUI(
            filmItem, holder.imageFilm, holder.nameRu, holder.nameEn, holder.rating, holder.country,
            holder.genres, holder.imageDote)
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