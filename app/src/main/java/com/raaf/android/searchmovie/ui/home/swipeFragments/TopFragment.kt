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

private const val TAG = "TopFragment"

class TopFragment : Fragment() {

    private lateinit var topViewModel: TopViewModel
    private lateinit var topRecyclerView: RecyclerView
    lateinit var nameTop: List<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        topViewModel = ViewModelProvider(this).get(TopViewModel::class.java)
        nameTop = listOf(getString(R.string.best), getString(R.string.popular), getString(R.string.await))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_top, container, false)
        topRecyclerView = root.findViewById(R.id.top_recycler)
        topRecyclerView.layoutManager = LinearLayoutManager(context)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topViewModel.list.observe(
                viewLifecycleOwner,
                Observer { list ->
                    Log.e(TAG, "list=${list.size}")
                    val list1 = mutableListOf<FilmSwipeItem>()
                    val list2 = mutableListOf<FilmSwipeItem>()
                    val list3 = mutableListOf<FilmSwipeItem>()
                    list.forEach { if(it.parentId == nameTop[0]) list1.add(it)
                        if(it.parentId == nameTop[1]) list2.add(it)
                        if(it.parentId == nameTop[2]) list3.add(it)}
                    Log.e(TAG, "list1=${list1.size},  list2=${list2.size},  list3=${list3.size}")
                    val listCategory = listOf(FilmsCategoryItem(nameTop[0], list1), FilmsCategoryItem(nameTop[1], list2), FilmsCategoryItem(nameTop[2], list3))
                    topRecyclerView.adapter = FilmsCategoryItemAdapter(listCategory)
                }
        )
//        requireArguments().getString("name")?.let {  }
    }
}