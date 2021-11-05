package com.raaf.android.searchmovie.ui.home.swipeFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import com.raaf.android.searchmovie.dataModel.homeItems.FilmsCategoryItem

private const val TAG = "MyFilmsFragment"
private const val F_T_MY_FILMS = "My films"

class MyFilmsFragment : Fragment() {

    private lateinit var myFilmsViewModel: MyFilmsViewModel
    private lateinit var myFilmsRecyclerView: RecyclerView
    private val listNameCategories = listOf("Watch later", "Favorite movies")
    private lateinit var listNameCategoriesForUI: List<String>
    private lateinit var noContentTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myFilmsViewModel = ViewModelProvider(this).get(MyFilmsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_my_films, container, false)
        myFilmsRecyclerView = root.findViewById(R.id.my_films_recycler)
        noContentTextView = root.findViewById(R.id.no_content_t_v)
        myFilmsRecyclerView.layoutManager = LinearLayoutManager(context)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listNameCategoriesForUI = listOf(getString(R.string.watch_later), getString(R.string.favorite_movies))
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
                    when {
                        list1.isEmpty() && list2.isEmpty() -> {
                            myFilmsRecyclerView.visibility = GONE
                            noContentTextView.visibility = VISIBLE
                        }
                        list1.isEmpty() && list2.isNotEmpty() -> {
                            myFilmsRecyclerView.adapter = FilmsCategoryItemAdapter(listOf(FilmsCategoryItem(listNameCategories[1], list2)), F_T_MY_FILMS, listNameCategoriesForUI)
                        }
                        list1.isNotEmpty() && list2.isEmpty() -> {
                            myFilmsRecyclerView.adapter = FilmsCategoryItemAdapter(listOf(FilmsCategoryItem(listNameCategories[0], list1)), F_T_MY_FILMS, listNameCategoriesForUI)
                        }
                        list1.isNotEmpty() && list2.isNotEmpty() -> {
                            myFilmsRecyclerView.adapter = FilmsCategoryItemAdapter(listOf(FilmsCategoryItem(listNameCategories[0], list1), FilmsCategoryItem(listNameCategories[1], list2)), F_T_MY_FILMS, listNameCategoriesForUI)
                        }
                    }
                }
        )
    }

    override fun onResume() {
        super.onResume()
        myFilmsViewModel.updateDBData()
    }
}