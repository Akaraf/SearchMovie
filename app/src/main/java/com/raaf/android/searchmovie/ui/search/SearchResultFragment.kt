package com.raaf.android.searchmovie.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.ui.adapters.FilmAdapter
import com.raaf.android.searchmovie.ui.utils.showToolbar

private const val TAG = "Search Result Fragment"

class SearchResultFragment : Fragment() {

    private lateinit var searchResultViewModel: SearchResultViewModel
    private lateinit var filmRecyclerView: RecyclerView
    private var pageSearch = 1
    private var query = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchResultViewModel = ViewModelProvider(this).get(SearchResultViewModel::class.java)
        query = requireArguments().getString("query") ?: ""
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

        showToolbar(requireActivity().findViewById(R.id.toolbar),
            "${getString(R.string.title_search)}: $query")

        searchResultViewModel.resultSearchLiveData.observe(
                viewLifecycleOwner,
                Observer { filmItems ->
                    filmRecyclerView.adapter = FilmAdapter(filmItems.films)
                })
        searchResultViewModel.fetchFilms(query, pageSearch)
    }
}