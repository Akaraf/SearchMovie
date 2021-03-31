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

private const val TAG = "CompilationFragment"

class CompilationFragment : Fragment() {

    private lateinit var compilationViewModel: CompilationViewModel
    private lateinit var compilationRecyclerView: RecyclerView
    private val nameCompilation = listOf("Comedy", "Fantasy", "Cartoon", "Drama", "Action")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        compilationViewModel = ViewModelProvider(this).get(CompilationViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root =  inflater.inflate(R.layout.fragment_compilation, container, false)
        compilationRecyclerView = root.findViewById(R.id.main_recycler_view)
        compilationRecyclerView.layoutManager = LinearLayoutManager(context)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        compilationViewModel.list.observe(
                viewLifecycleOwner,
                Observer { list ->
                    Log.e(TAG, "list=${list.size}")
                    val list1 = mutableListOf<FilmSwipeItem>()
                    val list2 = mutableListOf<FilmSwipeItem>()
                    val list3 = mutableListOf<FilmSwipeItem>()
                    val list4 = mutableListOf<FilmSwipeItem>()
                    val list5 = mutableListOf<FilmSwipeItem>()
                    list.forEach {
                        Log.e(TAG, "parent = ${it.parentId}, name = ${nameCompilation[0]}")
                        if(it.parentId == nameCompilation[0]) list1.add(it)
                        if(it.parentId == nameCompilation[1]) list2.add(it)
                        if(it.parentId == nameCompilation[2]) list3.add(it)
                        if(it.parentId == nameCompilation[3]) list4.add(it)
                        if(it.parentId == nameCompilation[4]) list5.add(it)}
                    Log.e(TAG, "list1=${list1.size},  list2=${list2.size},  list3=${list3.size},  list4=${list4.size},  list5=${list5.size}")
                    val listCategory = listOf(FilmsCategoryItem(nameCompilation[0], list1),
                            FilmsCategoryItem(nameCompilation[1], list2),
                            FilmsCategoryItem(nameCompilation[2], list3),
                            FilmsCategoryItem(nameCompilation[3], list4),
                            FilmsCategoryItem(nameCompilation[4], list5))
                    compilationRecyclerView.adapter = FilmsCategoryItemAdapter(listCategory)
                }
        )
    }
}
