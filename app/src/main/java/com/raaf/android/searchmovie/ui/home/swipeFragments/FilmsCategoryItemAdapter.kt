package com.raaf.android.searchmovie.ui.home.swipeFragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.homeItems.FilmsCategoryItem

class FilmsCategoryItemAdapter(val itemList: List<FilmsCategoryItem>/*, val hasDelete: Boolean*/) : RecyclerView.Adapter<FilmsCategoryItemAdapter.CategoryViewHolder>() {

    private val viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_main_swipe_home, parent,false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val filmCategoryItem = itemList[position]
        holder.nameText.text = filmCategoryItem.categoryName

        val layoutManager = LinearLayoutManager(
                holder.filmSwipeRecyclerView.context,
                LinearLayoutManager.HORIZONTAL,
                false
        )
        layoutManager.initialPrefetchItemCount = filmCategoryItem.filmItemList.size
        val filmSwipeItemAdapter = FilmSwipeItemAdapter(filmCategoryItem.filmItemList/*, hasDelete*/)
        holder.filmSwipeRecyclerView.layoutManager = layoutManager
        holder.filmSwipeRecyclerView.adapter = filmSwipeItemAdapter
        holder.filmSwipeRecyclerView.setRecycledViewPool(viewPool)
        holder.getCategoryName(filmCategoryItem.categoryName)
    }

    override fun getItemCount(): Int = itemList.size

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var categoryName: String = ""
        val filmSwipeRecyclerView: RecyclerView = itemView.findViewById(R.id.films_container)
        val nameText: TextView = itemView.findViewById(R.id.name_category)

        init {
            itemView.setOnClickListener(this)
        }

        fun getCategoryName(categoryName: String) {
            this.categoryName = categoryName
        }

        override fun onClick(v: View) {
            var bundle = bundleOf("categoryName" to categoryName)
            v.findNavController().navigate(R.id.action_global_detailCategoryFragment, bundle)
        }
    }



}