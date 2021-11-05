package com.raaf.android.searchmovie.ui.home.swipeFragments.detailCategory

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.api.FilmFetcher
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import com.raaf.android.searchmovie.ui.adapters.MyPersonCategoryAdapter
import com.squareup.picasso.Picasso
import javax.inject.Inject

private const val TAG = "DetailCategoryAdapter"
private const val MY_FILMS_DB = "MyFilmsDb"
private const val HISTORY_DB = "HistoryDb"

class DetailCategoryAdapter(private var items: MutableList<FilmSwipeItem>, private var categoryName: String, private var type: String) : RecyclerView.Adapter<DetailCategoryAdapter.ViewHolder>() {

    interface OnDeleteClickListener {
        fun onDeleteClick(endId: String, categoryName: String, type: String, itemId: String)
    }

    private var deleteListener: OnDeleteClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_film, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder.setIsRecyclable(false)
        val filmItem = items[position]
        val imageView = holder.imageFilm
        Picasso.get()
                .load(filmItem.posterUrl)
                .placeholder(R.drawable.splash_screen)
                .error(R.drawable.splash_screen)
                .fit()
                .into(imageView)

        val checkRating = filmItem.rating
//            First line cardView
        when {
            (filmItem.nameRu.isNotBlank() && checkRating != "0.0") -> {
                holder.rating.text = filmItem.rating
                holder.nameRu.text = filmItem.nameRu
            }
            (filmItem.nameRu.isNotBlank() && checkRating == "0.0") -> {
                holder.rating.visibility = View.GONE
                holder.nameRu.text = filmItem.nameRu
            }
            (filmItem.nameRu.isBlank() && checkRating != "0.0") -> {
                holder.rating.text = filmItem.rating
                holder.nameRu.text = filmItem.nameEn
                if (filmItem.year.isNotBlank()) {
                    holder.nameEn.text = filmItem.year
                } else {
                    holder.nameEn.visibility = View.GONE
                }
            }
            (filmItem.nameRu.isBlank() && checkRating == "0.0") -> {
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
        if (filmItem.nameRu.isNotBlank()) {
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
            else holder.nameEn.visibility = View.GONE
        }

//            Third line cardView
        var countries = filmItem.country
        if (countries != "") holder.country.text = countries
        else {
            holder.country.visibility = View.GONE
            holder.imageDote.visibility = View.GONE
        }

        var genres = filmItem.genre
        if (genres != "") holder.genres.text = genres
        else {
            holder.genres.visibility = View.GONE
            holder.imageDote.visibility = View.GONE
        }

        if (position+1 == itemCount) {
            holder.divider.visibility = View.GONE
            holder.dividerEnd.visibility = View.VISIBLE
        }

        holder.getFilmId(filmItem.filmId)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if(payloads.isNotEmpty()) {
            if (payloads[0] == 5) {
                holder.divider.visibility = GONE
                holder.dividerEnd.visibility = VISIBLE
            }
        } else {
            super.onBindViewHolder(holder,position, payloads);
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
        //return position.toLong(
    }

    override fun getItemCount(): Int = items.size

    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        deleteListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val imageFilm: ImageView = itemView.findViewById(R.id.image)
        val nameRu: TextView = itemView.findViewById(R.id.nameRu)
        val rating: TextView = itemView.findViewById(R.id.rating)
        val nameEn: TextView = itemView.findViewById(R.id.name_en_and_year)
        val country: TextView = itemView.findViewById(R.id.country)
        val genres: TextView = itemView.findViewById(R.id.genres)
        val divider: View = itemView.findViewById(R.id.card_film_divider)
        val dividerEnd: View = itemView.findViewById(R.id.card_film_divider_end)

        val imageDote: ImageView = itemView.findViewById(R.id.imageDote)
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

    fun onItemDismiss(position: Int) {
        var currentItem = items[position]
        if (position+1 == itemCount) this.notifyItemChanged(position-1, 5)
        if (currentItem.parentId.contains(categoryName)) deleteListener?.onDeleteClick(currentItem.parentId + currentItem.filmId.toString(), categoryName, type, currentItem.filmId.toString()) ?: return
        items.remove(currentItem)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, items.size)
    }
}