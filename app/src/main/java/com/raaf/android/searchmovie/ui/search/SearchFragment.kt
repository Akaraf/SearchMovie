package com.raaf.android.searchmovie.ui.search

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.Film
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import com.raaf.android.searchmovie.repository.RandomMovieUpdater
import com.raaf.android.searchmovie.ui.adapters.PopularPersonAdapter
import com.raaf.android.searchmovie.ui.extensions.lazyViewModel
import com.raaf.android.searchmovie.ui.home.swipeFragments.FilmSwipeItemAdapter
import com.raaf.android.searchmovie.ui.utils.fillCardFilmUI
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "Search Fragment"
private const val EXTRA_CATEGORY_NAME = "categoryName"

class SearchFragment : Fragment() {

    private val searchViewModel by lazyViewModel {
        App.appComponent.searchVM().create(it)
    }
    private lateinit var toolbar: Toolbar
    private lateinit var searchView: SearchView

    private lateinit var kinopoiskButton: Button
    private lateinit var yearsButton: Button
    private lateinit var genresButton: Button
    private lateinit var countriesButton: Button
    private lateinit var tvSeriesButton: Button
    private lateinit var awardsButton: Button

    private lateinit var digitalReleasesIncludeLayout: ConstraintLayout
    private lateinit var categoryNameDigital: TextView
    private lateinit var digitalRecyclerView: RecyclerView
    private lateinit var clickableLayoutDigital: ConstraintLayout
    private lateinit var allDigitalTV: TextView

    private lateinit var actorsIncludeLayout: ConstraintLayout
    private lateinit var categoryNameActors: TextView
    private lateinit var actorsRecyclerView: RecyclerView
    private lateinit var clickableLayoutActors: ConstraintLayout
    private lateinit var allActorsTV: TextView

    private lateinit var randomIncludeLayout: LinearLayout
    private lateinit var randomMovieLayout: ConstraintLayout
    private lateinit var imageFilm: ImageView
    private lateinit var nameRu: TextView
    private lateinit var rating: TextView
    private lateinit var nameEn: TextView
    private lateinit var country: TextView
    private lateinit var genres: TextView
    private lateinit var imageDote: ImageView
    private lateinit var changeRandomMovieButton: Button
    private lateinit var changeFiltersButton: Button

    val listReleases = mutableListOf<FilmSwipeItem>()

    var currentRandomMovieId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_search, container, false)
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.visibility = GONE
        searchView = root.findViewById(R.id.s_search_view)
        kinopoiskButton = root.findViewById(R.id.kinopoisk_button)
        yearsButton = root.findViewById(R.id.years_button)
        genresButton = root.findViewById(R.id.genres_button)
        countriesButton = root.findViewById(R.id.countries_button)
        tvSeriesButton = root.findViewById(R.id.tv_series_button)
        awardsButton = root.findViewById(R.id.awards_button)



        digitalReleasesIncludeLayout = root.findViewById(R.id.digital_releases_include_layout)
        allDigitalTV = digitalReleasesIncludeLayout.findViewById(R.id.all_text_view)
        allDigitalTV.visibility = GONE
        categoryNameDigital = digitalReleasesIncludeLayout.findViewById(R.id.name_category)
        digitalRecyclerView = digitalReleasesIncludeLayout.findViewById(R.id.content_container)
        clickableLayoutDigital = digitalReleasesIncludeLayout.findViewById(R.id.clickable_layout)

        actorsIncludeLayout = root.findViewById(R.id.actors_include_layout)
        allActorsTV = actorsIncludeLayout.findViewById(R.id.all_text_view)
        allActorsTV.visibility = GONE
        categoryNameActors = actorsIncludeLayout.findViewById(R.id.name_category)
        actorsRecyclerView = actorsIncludeLayout.findViewById(R.id.content_container)
        clickableLayoutActors = actorsIncludeLayout.findViewById(R.id.clickable_layout)
        digitalRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        actorsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        randomIncludeLayout = root.findViewById(R.id.random_include_layout)
        randomMovieLayout = randomIncludeLayout.findViewById(R.id.random_film_include_layout)
        imageFilm = randomMovieLayout.findViewById(R.id.image)
        nameRu = randomMovieLayout.findViewById(R.id.nameRu)
        rating = randomMovieLayout.findViewById(R.id.rating)
        nameEn = randomMovieLayout.findViewById(R.id.name_en_and_year)
        country = randomMovieLayout.findViewById(R.id.country)
        genres = randomMovieLayout.findViewById(R.id.genres)
        imageDote = randomMovieLayout.findViewById(R.id.imageDote)
        changeRandomMovieButton = randomIncludeLayout.findViewById(R.id.random_movie_button)
        changeFiltersButton = randomIncludeLayout.findViewById(R.id.filter_button)

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchViewModel.releasesLiveData.observe(
                viewLifecycleOwner,
                Observer {
                    Log.e(TAG, "rf::"+it.count().toString())
                    digitalRecyclerView.adapter = FilmSwipeItemAdapter(it, null, null)
                }
        )

        searchViewModel.popularPersonLiveData.observe(
                viewLifecycleOwner,
                Observer {
                    Log.e(TAG, "pp::"+it.count().toString())
                    if (it.count()>50) searchViewModel.popularPersonLiveData.removeObservers(viewLifecycleOwner)
                    actorsRecyclerView.adapter = PopularPersonAdapter(it, true)
                }
        )

        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                //????????????????????, ?????????? ???????????????????????? ???????????????????? ????????????
                override fun onQueryTextSubmit(query: String): Boolean {
                    var bundle = bundleOf("query" to query)
                    NavHostFragment.findNavController(this@SearchFragment).navigate(
                        R.id.action_navigation_search_to_searchResultFragment, bundle)
                    setQuery("", false)
                    clearFocus()
                    return false
                }
                //???????????????????? ???????????? ??????, ?????????? ???????????????? ???????? ????????????
                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })

            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    searchViewModel.randomMovieUpdater.movieFlow.onEach {
                        Log.e(TAG, "random ovie was received")
                        if (it != null) {
                            randomMovieLayout.visibility = GONE
                            fillCardFilmUI(
                                it, imageFilm, nameRu, nameEn, rating, country, genres, imageDote
                            )
                            currentRandomMovieId = it.filmId
                            randomMovieLayout.visibility = VISIBLE
                        } else Log.e(TAG, "null")
                    }.collect()
                }
            }
        }
        
        categoryNameDigital.text = getString(R.string.digital_releases)
        categoryNameActors.text = getString(R.string.popular_actors)

        setListenerForCategoryButton(kinopoiskButton, getString(R.string.kinopoisk))
        setListenerForCategoryButton(yearsButton, getString(R.string.years))
        setListenerForCategoryButton(genresButton, getString(R.string.genre))
        setListenerForCategoryButton(countriesButton, getString(R.string.countries))
        setListenerForCategoryButton(tvSeriesButton, getString(R.string.tv_series))
        setListenerForCategoryButton(awardsButton, getString(R.string.awards))

        changeRandomMovieButton.setOnClickListener {
            lifecycleScope.launch { searchViewModel.newRandomMovie() }
        }

        changeFiltersButton.setOnClickListener {
            searchViewModel.makeChangeFiltersEvent()
            //make filter changing later
        }

        randomMovieLayout.setOnClickListener {
            if (currentRandomMovieId != null) {
                var bundle = bundleOf("filmId" to currentRandomMovieId)
                it.findNavController().navigate(R.id.action_global_filmFragment, bundle)
            }
        }
    }

    private fun setListenerForCategoryButton(button: Button, categoryName: String) {
        button.setOnClickListener {
            NavHostFragment.findNavController(this@SearchFragment).navigate(R.id.action_navigation_search_to_categoryForSearchFragment, bundleOf(EXTRA_CATEGORY_NAME to categoryName))
        }
    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "listReleases for RV: ${listReleases.size}")
        //digitalRecyclerView.adapter = FilmSwipeItemAdapter(listReleases)
    }
}