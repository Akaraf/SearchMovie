package com.raaf.android.searchmovie.ui.film

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.Trailer

private const val EXTRA_URL = "url"

class TrailerAdapter(items: List<Trailer>) : RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder>() {

    private var trailerItems: List<Trailer> = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_trailer, parent, false)
        return TrailerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrailerViewHolder, position: Int) {
        val trailerItem = trailerItems[position]
        holder.nameTrailerTV.text = trailerItem.name
        holder.websiteTV.text = trailerItem.site
        holder.getUrl(trailerItem.url)
    }

    override fun getItemCount(): Int = trailerItems.size

    inner class TrailerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var watchButton: Button = itemView.findViewById(R.id.watch_button)
        var nameTrailerTV: TextView = itemView.findViewById(R.id.name_video)
        var websiteTV: TextView = itemView.findViewById(R.id.name_website_source)
        var currentUrl = ""

        init {
            itemView.setOnClickListener(this)
            watchButton.setOnClickListener(this)
        }

        fun getUrl(url: String) {
            this.currentUrl = url
        }

        override fun onClick(v: View) {
            var bundle = bundleOf(EXTRA_URL to currentUrl)
            v.findNavController().navigate(R.id.action_trailerFragment_to_trailerVideoFragment, bundle)
        }
    }
}