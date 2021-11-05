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
import com.raaf.android.searchmovie.dataModel.StaffPerson
import com.squareup.picasso.Picasso

//maybe, make it with generic type and compare with FilmSwipeItemAdapter later
class PopularPersonAdapter(val itemList: List<StaffPerson>, val isForPP: Boolean) : RecyclerView.Adapter<PopularPersonAdapter.PersonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_film_swipe_home, parent, false)
        return PersonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val personItem = itemList[position]
        val imageView = holder.image
        Picasso.get()
                .load(personItem.posterUrl)
                .placeholder(R.drawable.splash_screen)
                .error(R.drawable.splash_screen)
                .fit()
                .centerCrop()
                .into(imageView)
        if (personItem.nameRu.isNotBlank())holder.nameText.text = personItem.nameRu
        else holder.nameText.text = personItem.nameEn
        if (isForPP) holder.professionText.text = personItem.professionText
        else holder.professionText.text = personItem.description
        holder.getStaffId(personItem.staffId)
    }

    override fun getItemCount(): Int = itemList.size

    inner class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var staffId = 0
        var image: ImageView = itemView.findViewById(R.id.image_film)
        val nameText: TextView = itemView.findViewById(R.id.name)
        val professionText: TextView = itemView.findViewById(R.id.genre)

        fun getStaffId(staffId: Int) {
            this.staffId = staffId
        }

        init {
            itemView.setOnClickListener(this)
            image.clipToOutline = true
        }

        override fun onClick(v: View) {
            var bundle = bundleOf("personId" to staffId)
            v.findNavController().navigate(R.id.action_global_personFragment, bundle)
        }
    }
}