package com.raaf.android.searchmovie.ui.home.swipeFragments

import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import com.squareup.picasso.Picasso

class FilmSwipeItemAdapter(val itemList: List<FilmSwipeItem>/*, val hasDelete: Boolean*/) : RecyclerView.Adapter<FilmSwipeItemAdapter.FilmViewHolder>() {

//    private val itemList: List<FilmSwipeItem> = itemList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_film_swipe_home, parent,false)
        return FilmViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val filmSwipeItem = itemList[position]
        val imageView = holder.image
        Picasso.get()
                .load(filmSwipeItem.posterUrl)
                .placeholder(R.drawable.splash_screen)
                .error(R.drawable.splash_screen)
                .fit()
                .into(imageView)
        holder.nameText.text = filmSwipeItem.nameRu
        holder.genreText.text = filmSwipeItem.genre
        holder.getFilmId(filmSwipeItem.filmId)
//        holder.getPrimaryKey(filmSwipeItem.databaseId)
    }

    override fun getItemCount(): Int = itemList.size


    inner class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var filmId = 0
        val image: ImageView = itemView.findViewById(R.id.image_film)
        val nameText: TextView = itemView.findViewById(R.id.name)
        val genreText: TextView = itemView.findViewById(R.id.genre)
//        val deleteButton: ImageButton = itemView.findViewById(R.id.button_delete)

        init {
            itemView.setOnClickListener(this)
            /*if (hasDelete) {
                deleteButton.visibility = VISIBLE
                deleteButton.setOnClickListener {

                }
            }*/
        }

//        fun getPrimaryKey(primaryKey: String) {}

        fun getFilmId(filmId: Int) {
            this.filmId = filmId
        }

        override fun onClick(v: View) {
            var bundle = bundleOf("filmId" to filmId)
            v.findNavController().navigate(R.id.action_global_filmFragment, bundle)
        }
    }
}