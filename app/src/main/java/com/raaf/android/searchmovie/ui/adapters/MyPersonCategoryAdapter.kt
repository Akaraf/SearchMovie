package com.raaf.android.searchmovie.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.rootJSON.PersonResponse
import com.squareup.picasso.Picasso
import javax.inject.Inject

private const val TAG = "MyPersonCategoryAdapter"
private const val DELETE_MY_PERSON_COLLECTION = "DMP"

class MyPersonCategoryAdapter(private val items: MutableSet<PersonResponse>, private val categoryName: String) : RecyclerView.Adapter<MyPersonCategoryAdapter.ViewHolder>() {

    interface OnDeleteClickListener {
        fun onDeleteClick(endId: String, categoryName: String, type: String, itemId: String)
    }

    private var deleteListener: OnDeleteClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_person, parent, false)
        return ViewHolder(view)
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val personItem = items.elementAt(position)
        val imageView = holder.imagePerson
        Picasso.get()
            .load(personItem.posterUrl)
            .placeholder(R.drawable.splash_screen)
            .error(R.drawable.splash_screen)
            .fit()
            .into(imageView)
        holder.nameRu.text = personItem.nameRu
        holder.nameEn.text = personItem.nameEn
        holder.profession.text = personItem.profession
        if (position+1 == itemCount) {
            holder.divider.visibility = GONE
            holder.dividerEnd.visibility = VISIBLE
        }

        holder.getPersonId(personItem.personId)
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

    fun onItemDismiss(position: Int) {
        var currentItem = items.elementAt(position)
        var newItems = listOf<PersonResponse>()
        Log.e(TAG, "size1:::${items.size}")
        if (position+1 == itemCount) this.notifyItemChanged(position-1, 5)
        deleteListener?.onDeleteClick(currentItem.personId.toString(), categoryName, DELETE_MY_PERSON_COLLECTION, currentItem.personId.toString()) ?: return
        //notifyItemRemoved(position)
        items.remove(currentItem)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, items.size)
        Log.e(TAG, "size2:::${items.size}")
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val imagePerson: ImageView = itemView.findViewById(R.id.image_person)
        val nameRu: TextView = itemView.findViewById(R.id.name_ru_person)
        val nameEn: TextView = itemView.findViewById(R.id.name_en_person)
        val profession: TextView = itemView.findViewById(R.id.profession_person)
        var divider: View = itemView.findViewById(R.id.card_person_divider)
        val dividerEnd: View = itemView.findViewById(R.id.card_person_divider_end)
        var personId = 0

        init {
            itemView.setOnClickListener(this)
        }

        fun getPersonId(personId: Int) {
            this.personId = personId
        }

        override fun onClick(v: View) {
            var bundle = bundleOf("personId" to personId)
            v.findNavController().navigate(R.id.action_global_personFragment, bundle)
        }
    }
}