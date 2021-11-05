package com.raaf.android.searchmovie.ui.search.categoryForSearch

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.CategoryItem
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

private const val EXTRA_FIRST_TYPE = "firstType"
private const val EXTRA_CATEGORIES = "Categories"
private const val EXTRA_CATEGORY_ITEM_NAME = "categoryName"
private const val EXTRA_SECOND_TYPE = "secondType"

class CategoryForSearchAdapter(private var items: List<CategoryItem>) : RecyclerView.Adapter<CategoryForSearchAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        var currentItem = items[position]
        holder.itemNumberTV.text = "${position+1}."
        holder.itemNameTV.text = currentItem.itemName
        if (currentItem.frameUrl.isNullOrBlank()) currentItem.frameUrl = "htttps://fictiveurl.jpg"
        Picasso.get()
            .load(currentItem.frameUrl)
            .fit()
            .centerCrop()
            .into(holder.frameContainerIV, object:Callback {
                override fun onSuccess() {
                }

                override fun onError(e: Exception?) {
                    holder.card.visibility = GONE
                }

            })
        holder.getNames(currentItem.categoryName, currentItem.itemName)
    }

    override fun getItemCount(): Int = items.count()

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val itemNumberTV: TextView = itemView.findViewById(R.id.item_number)
        val frameContainerIV: ImageView = itemView.findViewById(R.id.frame_card_category)
        val itemNameTV: TextView = itemView.findViewById(R.id.item_name_c_c)
        val card: CardView = itemView.findViewById(R.id.card_frame_c)
        var categoryName = ""
        var categoryItemName = ""

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            var bundle = bundleOf((EXTRA_FIRST_TYPE to EXTRA_CATEGORIES), (EXTRA_SECOND_TYPE to categoryName), (EXTRA_CATEGORY_ITEM_NAME to categoryItemName))
            v.findNavController().navigate(R.id.action_global_detailCategoryFragment, bundle)
        }

        fun getNames(categoryName: String, itemName: String) {
            this.categoryName = categoryName
            this.categoryItemName = itemName
        }
    }
}