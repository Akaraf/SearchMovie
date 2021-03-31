package com.raaf.android.searchmovie.ui.search

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.raaf.android.searchmovie.R

private const val TAG = "Search Fragment"

class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        searchViewModel =
                ViewModelProvider(this).get(SearchViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_search, container, false)
        toolbar = requireActivity().findViewById(R.id.toolbar)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_search, menu)
        toolbar.title = ""
        toolbar.inflateMenu(R.menu.fragment_search)

        val searchItem: MenuItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setIconifiedByDefault(false)
        searchView.queryHint = getText(R.string.search_tooltip_text)
        searchView.apply {

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//                Вызывается, когда пользователь отправляет запрос
                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.d(TAG, "QueryTextSubmit: $query")
                    var bundle = bundleOf("query" to query)
                    NavHostFragment.findNavController(this@SearchFragment).navigate(R.id.action_navigation_search_to_searchResultFragment, bundle)
                    return true
                }
//Вызывается каждый раз, когда меняется один символ
                override fun onQueryTextChange(newText: String?): Boolean {
                    Log.d(TAG, "QueryTextChange: $newText")
                    return false
                }
            })
        }
    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()

    }
}