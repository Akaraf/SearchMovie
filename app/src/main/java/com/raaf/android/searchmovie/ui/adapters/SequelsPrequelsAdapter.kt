package com.raaf.android.searchmovie.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.rootJSON.SequelsPrequelsResponse
import com.squareup.picasso.Picasso

private const val TAG = "SequelsPrequelsAdapter"

class SequelsPrequelsAdapter(private val items: List<SequelsPrequelsResponse>) : RecyclerView.Adapter<SequelsPrequelsAdapter.ViewHolder>() {

    init {
        Log.e(TAG, "items: " + items.size.toString())
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_sequels_prequels, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val imageView = holder.image
        Picasso.get()
            .load(item.posterUrl)
            .placeholder(R.drawable.splash_screen)
            .error(R.drawable.splash_screen)
            .fit()
            .into(imageView)
        holder.nameRu.text = item.nameRu
        holder.nameEn.text = item.nameEn
        if (position != items.count()-1) holder.divider.visibility = VISIBLE
        holder.getFilmId(item.filmId)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var filmId = 0

        var divider: View = itemView.findViewById(R.id.divider)
        val image: ImageView = itemView.findViewById(R.id.image_sequels_prequels)
        val nameRu: TextView = itemView.findViewById(R.id.nameRu)
        val nameEn: TextView = itemView.findViewById(R.id.nameEn)

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