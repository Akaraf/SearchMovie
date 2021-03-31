package com.raaf.android.searchmovie.ui.home.swipeFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import com.raaf.android.searchmovie.dataModel.homeItems.FilmsCategoryItem

private const val TAG = "MyFilmsFragment"

class MyFilmsFragment : Fragment() {

    private lateinit var myFilmsViewModel: MyFilmsViewModel
    private lateinit var myFilmsRecyclerView: RecyclerView
    private lateinit var listNameCategories: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myFilmsViewModel = ViewModelProvider(this).get(MyFilmsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_my_films, container, false)
        myFilmsRecyclerView = root.findViewById(R.id.my_films_recycler)
        myFilmsRecyclerView.layoutManager = LinearLayoutManager(context)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listNameCategories = listOf(getString(R.string.watch_later), getString(R.string.favorite_movies))
        myFilmsViewModel.list.observe(
                viewLifecycleOwner,
                Observer { list->
                    Log.e(TAG, "list=${list.size}")
                    val list1 = mutableListOf<FilmSwipeItem>()
                    val list2 = mutableListOf<FilmSwipeItem>()
                    list.forEach {
                        if(it.parentId == listNameCategories[0]) list1.add(it)
                        if(it.parentId == listNameCategories[1]) list2.add(it)
                    }
                    Log.e(TAG, "list1=${list1.size}, list2=${list2.size}")
                    val listCategory = listOf(FilmsCategoryItem(listNameCategories[0], list1), FilmsCategoryItem(listNameCategories[1], list2))
                    myFilmsRecyclerView.adapter = FilmsCategoryItemAdapter(listCategory)
                }
        )
    }
}