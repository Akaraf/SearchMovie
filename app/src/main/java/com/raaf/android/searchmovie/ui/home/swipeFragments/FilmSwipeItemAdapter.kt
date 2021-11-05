package com.raaf.android.searchmovie.ui.home.swipeFragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.FilmForPersonResponse
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import com.squareup.picasso.Picasso

class FilmSwipeItemAdapter(val itemList: List<FilmSwipeItem>, private val professionKeysList: List<FilmForPersonResponse>?, private val translateList: Map<String, String>?/*, val hasDelete: Boolean*/) : RecyclerView.Adapter<FilmSwipeItemAdapter.FilmViewHolder>() {

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
                .error(R.drawable.splash_screen)
                .fit()
                .centerCrop()
                .into(imageView)
        if (filmSwipeItem.nameRu.isNotBlank())holder.nameText.text = filmSwipeItem.nameRu
        else holder.nameText.text = filmSwipeItem.nameEn
        if (professionKeysList != null) {
            var professionKey: String? = findProfessionKey(filmSwipeItem.filmId)
            if (professionKey != null && professionKey.isNotEmpty()) holder.genreText.text = professionKey
            else holder.genreText.text = filmSwipeItem.genre
        } else holder.genreText.text = filmSwipeItem.genre
        holder.getFilmId(filmSwipeItem.filmId)
        holder.setIsRecyclable(false)
//        holder.getPrimaryKey(filmSwipeItem.databaseId)
    }

    private fun translateProfessionKey(key: String) : String? {
        if (translateList != null)
            for (count in translateList.keys) {
                if (count.contains(key)) return translateList[count]
        }
        return null
    }

    private fun findProfessionKey(id: Int) : String? {
        for (count in professionKeysList!!) {
            if (count.filmId == id) {
                return translateProfessionKey(count.professionKey) ?: count.professionKey
            }
        }
        return null
    }

    override fun getItemCount(): Int = itemList.size

    inner class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var filmId = 0
        var image: ImageView = itemView.findViewById(R.id.image_film)
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