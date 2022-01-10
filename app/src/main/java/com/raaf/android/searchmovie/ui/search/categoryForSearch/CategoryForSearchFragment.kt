package com.raaf.android.searchmovie.ui.search.categoryForSearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.ui.utils.showToolbar

private const val EXTRA_CATEGORY_NAME = "categoryName"
private const val EXTRA_YEAR_CATEGORY = "Years"
private const val EXTRA_GENRE_CATEGORY = "Genres"
private const val EXTRA_COUNTRY_CATEGORY = "Countries"

class CategoryForSearchFragment : Fragment() {

    private var typeCategory: String = ""
    private lateinit var categoryForSearchViewModel: CategoryForSearchViewModel
    private lateinit var categoryNames: List<String>
    private lateinit var categoryNameTV: TextView
    private lateinit var categoryRV: RecyclerView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryNames = listOf(getString(R.string.years), getString(R.string.genre), getString(R.string.countries), getString(R.string.tv_series))
        categoryForSearchViewModel = ViewModelProvider(this).get(CategoryForSearchViewModel::class.java)
        typeCategory = requireArguments().getString(EXTRA_CATEGORY_NAME) ?: ""
        toolbar = requireActivity().findViewById(R.id.toolbar)
        showToolbar(toolbar, typeCategory)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_category_for_search, container, false)
        categoryNameTV = root.findViewById(R.id.category_name_c_f_s)
        categoryNameTV.text = typeCategory
        //setCategoryName(categoryNameTV)
        categoryRV = root.findViewById(R.id.category_recycler_c_f_s)
        categoryRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryForSearchViewModel.categoryItemsLiveData.observe(
            viewLifecycleOwner,
            Observer { categoryItems ->
                if (categoryItems != null && categoryItems.isNotEmpty()) {
                if (categoryItems[0].categoryName.contains(categoryNames[0]) && categoryItems.count() == 17) {
                    categoryRV.adapter = CategoryForSearchAdapter(categoryItems)
                    categoryForSearchViewModel.categoryItemsLiveData.removeObservers(viewLifecycleOwner)
                }
                if (categoryItems[0].categoryName.contains(categoryNames[1]) && categoryItems.count() == 14) {
                    categoryRV.adapter = CategoryForSearchAdapter(categoryItems)
                    categoryForSearchViewModel.categoryItemsLiveData.removeObservers(viewLifecycleOwner)
                }
                if (categoryItems[0].categoryName.contains(categoryNames[2]) && categoryItems.count() == 11) {
                    categoryRV.adapter = CategoryForSearchAdapter(categoryItems)
                    categoryForSearchViewModel.categoryItemsLiveData.removeObservers(viewLifecycleOwner)
                }
                if (categoryItems[0].categoryName.contains(categoryNames[3]) && categoryItems.count() == 4) {
                    categoryRV.adapter = CategoryForSearchAdapter(categoryItems)
                    categoryForSearchViewModel.categoryItemsLiveData.removeObservers(viewLifecycleOwner)
                }
                }
            }
        )
        categoryForSearchViewModel.setCategoryName(typeCategory)
    }

    override fun onResume() {
        super.onResume()
        showToolbar(toolbar, typeCategory)
    }

    /*private fun setCategoryName(textView: TextView) {
        when (typeCategory) {
            EXTRA_YEAR_CATEGORY -> textView.text = getString(R.string.years)
        }
    }*/
}