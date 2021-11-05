package com.raaf.android.searchmovie.ui.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.Film
import com.raaf.android.searchmovie.ui.adapters.FilmAdapter
import com.raaf.android.searchmovie.ui.showToolbar
import com.squareup.picasso.Picasso

private const val TAG = "Search Result Fragment"

class SearchResultFragment : Fragment() {

    private lateinit var searchResultViewModel: SearchResultViewModel
    private lateinit var filmRecyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private var pageSearch = 1
    private var query = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchResultViewModel = ViewModelProvider(this).get(SearchResultViewModel::class.java)
        query = requireArguments().getString("query") ?: ""
        toolbar = requireActivity().findViewById(R.id.toolbar)
        showToolbar(toolbar, "${getString(R.string.title_search)}: $query")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_search_result, container, false)
        filmRecyclerView = root.findViewById(R.id.result_search_recycler)
        filmRecyclerView.layoutManager = LinearLayoutManager(context)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchResultViewModel.resultSearchLiveData.observe(
                viewLifecycleOwner,
                Observer { filmItems ->
                    filmRecyclerView.adapter = FilmAdapter(filmItems.films)
                })
        searchResultViewModel.fetchFilms(query, pageSearch)
    }

    override fun onResume() {
        super.onResume()
        showToolbar(toolbar, "${getString(R.string.title_search)}: $query")
    }
}