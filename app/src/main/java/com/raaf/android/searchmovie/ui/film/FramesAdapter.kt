package com.raaf.android.searchmovie.ui.film

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.Frame
import com.squareup.picasso.Picasso

class FramesAdapter(items: List<Frame>, clickListener: FramesItemClickListener) : RecyclerView.Adapter<FramesAdapter.ImageViewHolder>() {

    private var framesItemClickListener: FramesItemClickListener = clickListener
    private var frameItems: List<Frame> = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_frames_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val frameItem = frameItems[position]

        Picasso.get()
                .load(frameItem.url)
                .fit()
                .centerCrop()
                .into(holder.frameImage)
        holder.itemView.setOnClickListener {
            framesItemClickListener.onFrameItemClick(it, holder.adapterPosition, frameItem)
        }
//        ViewCompat.setTransitionName(holder.frameImage, frameItem.id.toString())

    }

    override fun getItemCount(): Int {
        return frameItems.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var frameImage: ImageView = itemView.findViewById(R.id.frame_image)

    }
}