package com.raaf.android.searchmovie.ui.home.swipeFragments

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.homeItems.FilmsCategoryItem

private const val EXTRA_FIRST_TYPE = "firstType"
private const val EXTRA_SECOND_TYPE = "secondType"
private const val TAG = "FilmsCategoryItemA"

class FilmsCategoryItemAdapter(val itemList: List<FilmsCategoryItem>, val firstType: String, val namesOfCategory: List<String>?) : RecyclerView.Adapter<FilmsCategoryItemAdapter.CategoryViewHolder>() {

    private val viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_card_main_category, parent,false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val filmCategoryItem = itemList[position]
        if (filmCategoryItem.filmItemList.isNotEmpty()) {
            holder.nameText.text = if (namesOfCategory != null) namesOfCategory[position] else filmCategoryItem.categoryName
            val layoutManager = LinearLayoutManager(
                holder.filmSwipeRecyclerView.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            layoutManager.initialPrefetchItemCount = filmCategoryItem.filmItemList.size
            val filmSwipeItemAdapter = FilmSwipeItemAdapter(filmCategoryItem.filmItemList, null, null/*hasDelete*/)
            holder.filmSwipeRecyclerView.layoutManager = layoutManager
            holder.filmSwipeRecyclerView.adapter = filmSwipeItemAdapter
            holder.filmSwipeRecyclerView.setRecycledViewPool(viewPool)
            holder.getCategoryName(filmCategoryItem.categoryName)
        } else holder.rootLayout.visibility = GONE
    }

    override fun getItemCount(): Int = itemList.size

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var categoryName: String = ""
        val filmSwipeRecyclerView: RecyclerView = itemView.findViewById(R.id.content_container)
        val nameText: TextView = itemView.findViewById(R.id.name_category)
        val rootLayout: ConstraintLayout = itemView.findViewById(R.id.main_category_root_layout)

        init {
            itemView.setOnClickListener(this)
        }

        fun getCategoryName(categoryName: String) {
            this.categoryName = categoryName
        }

        override fun onClick(v: View) {
            Log.e(TAG, "categoryName is $categoryName")
            var bundle = bundleOf(EXTRA_SECOND_TYPE to categoryName, EXTRA_FIRST_TYPE to firstType)
            v.findNavController().navigate(R.id.action_global_detailCategoryFragment, bundle)
        }
    }



}