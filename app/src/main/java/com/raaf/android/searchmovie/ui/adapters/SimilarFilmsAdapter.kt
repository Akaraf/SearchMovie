package com.raaf.android.searchmovie.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.SimilarFilm
import com.squareup.picasso.Picasso

class SimilarFilmsAdapter(val items: List<SimilarFilm>) : RecyclerView.Adapter<SimilarFilmsAdapter.SimilarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_film_swipe_home, parent, false)
        return SimilarViewHolder(view)
    }

    override fun onBindViewHolder(holder: SimilarViewHolder, position: Int) {
        val item = items[position]
        val imageView = holder.image
        Picasso.get()
            .load(item.posterUrl)
            .placeholder(R.drawable.splash_screen)
            .error(R.drawable.splash_screen)
            .fit()
            .centerCrop()
            .into(imageView)
        holder.nameText.text = item.nameRu
        holder.nameEnText.text = item.nameEn
        holder.getFilmId(item.filmId)
    }

    override fun getItemCount(): Int = items.size

    inner class SimilarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var filmId = 0
        var image: ImageView = itemView.findViewById(R.id.image_film)
        val nameText: TextView = itemView.findViewById(R.id.name)
        val nameEnText: TextView = itemView.findViewById(R.id.genre)


        init {
            itemView.setOnClickListener(this)
            image.clipToOutline = true
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