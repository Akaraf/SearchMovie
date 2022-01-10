package com.raaf.android.searchmovie.ui.film

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.rootJSON.MovieById
import com.raaf.android.searchmovie.backgroundJob.services.FirebaseStorageService
import com.raaf.android.searchmovie.dataModel.SimilarFilm
import com.raaf.android.searchmovie.dataModel.StaffPerson
import com.raaf.android.searchmovie.ui.MainActivity
import com.raaf.android.searchmovie.ui.adapters.PopularPersonAdapter
import com.raaf.android.searchmovie.ui.adapters.SimilarFilmsAdapter
import com.raaf.android.searchmovie.ui.extensions.lazyViewModel
import com.raaf.android.searchmovie.ui.utils.showToolbar
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.coroutines.CoroutineContext

private const val TAG = "FilmFragment"
private const val EXTRA_ITEM_ID = "itemId"
private const val EXTRA_MOVIE_ID = "filmId"
private const val EXTRA_FACTS_TEXT = "facts"
private const val TYPE_COLLECTION = "type"
private const val ADD_WATCH_LATER_COLLECTION = "AWLC"
private const val ADD_FAVORITE_COLLECTION = "AFC"
private const val EXTRA_MOVIE = "movie"
private const val DELETE_WATCH_LATER_COLLECTION = "DW"
private const val DELETE_FAVORITE_COLLECTION = "DF"

class FilmFragment : Fragment(), OnBackPressedListener {

    var addVariants = listOf("Watch later", "Favorite movies")
    private lateinit var movieCurrent: MovieById
    private var isOpen = false

    private val filmViewModel by lazyViewModel {
        App.appComponent.filmVM().create(it)
    }

    private lateinit var floatingButton: FloatingActionButton
    private lateinit var mainLayout: LinearLayout
    private lateinit var checkboxLayout: LinearLayout
    private lateinit var watchLaterCheckBox: CheckBox
    private lateinit var favoriteCheckBox: CheckBox

    private lateinit var filmImage: ImageView
    private lateinit var filmRuNameText: TextView
    private lateinit var filmEnNameAndYearText: TextView
    private lateinit var genresText: TextView
    private lateinit var countryAndDurationText: TextView
    private lateinit var sloganText: TextView
    private lateinit var descriptionDivider: View
    private lateinit var descriptionText: TextView

    private lateinit var actorsIncludeLayout: ConstraintLayout
    private lateinit var actorsGrayDiv: View
    private lateinit var categoryNameActors: TextView
    private lateinit var actorsRV: RecyclerView
    //private lateinit var clickableActorsLayout: ConstraintLayout
    private lateinit var actorsAll: TextView
    private lateinit var actorsDivider: View

    private lateinit var frames: TextView
    private lateinit var video: TextView

    private lateinit var similarNonFactsGrayDiv: View
    private lateinit var similarsLayout: ConstraintLayout
    private lateinit var similarFilmsIncludeLayout: ConstraintLayout
    private lateinit var categoryNameSimilarFilms: TextView
    private lateinit var similarFilmsRV: RecyclerView
    //private lateinit var clickableSimilarFilmsLayout: ConstraintLayout
    private lateinit var similarFilmsAll: TextView

    private lateinit var openTV: TextView
    private lateinit var shareTV: TextView

    private lateinit var factsLayout: LinearLayout
    private lateinit var factsText: TextView
    //private lateinit var factsHead: TextView
    private lateinit var allFacts: TextView
    private lateinit var nonSimilarGrayDivFacts: View

    private lateinit var sequelsPrequels: TextView
    private val IOCoroutineContext: CoroutineContext = Dispatchers.IO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity != null) {
            (activity as MainActivity).getBackPressedListener(this)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_film, container, false)
        floatingButton = view.findViewById(R.id.fab_film)
        mainLayout = view.findViewById(R.id.main_layout)
        checkboxLayout = view.findViewById(R.id.checkbox_layout)
        watchLaterCheckBox = view.findViewById(R.id.watch_later_checkBox)
        favoriteCheckBox = view.findViewById(R.id.favorite_movies_checkBox)
        filmImage = view.findViewById(R.id.film_image)
        filmImage.clipToOutline = true
        filmRuNameText = view.findViewById(R.id.film_ru_name_text)
        filmEnNameAndYearText = view.findViewById(R.id.film_en_name_text)
        genresText = view.findViewById(R.id.genres_text)
        actorsIncludeLayout = view.findViewById(R.id.actors_include_layout)
        categoryNameActors = actorsIncludeLayout.findViewById(R.id.name_category)
        actorsGrayDiv = view.findViewById(R.id.actors_gray_div)
        actorsRV = actorsIncludeLayout.findViewById(R.id.content_container)
        //clickableActorsLayout = actorsIncludeLayout.findViewById(R.id.clickable_layout)
        actorsAll = actorsIncludeLayout.findViewById(R.id.all_text_view)
        actorsDivider = view.findViewById(R.id.actors_divider)
        similarNonFactsGrayDiv = view.findViewById(R.id.similar_non_facts)
        nonSimilarGrayDivFacts = view.findViewById(R.id.non_similar_div)
        similarsLayout = view.findViewById(R.id.similar_layout)
        similarFilmsIncludeLayout = view.findViewById(R.id.similar_movies_include_layout)
        categoryNameSimilarFilms = similarFilmsIncludeLayout.findViewById(R.id.name_category)
        similarFilmsRV = similarFilmsIncludeLayout.findViewById(R.id.content_container)
        //clickableSimilarFilmsLayout = similarFilmsIncludeLayout.findViewById(R.id.clickable_layout)
        similarFilmsAll = similarFilmsIncludeLayout.findViewById(R.id.all_text_view)
        countryAndDurationText = view.findViewById(R.id.country_and_duration_text)
        sloganText = view.findViewById(R.id.slogan)
        descriptionText = view.findViewById(R.id.description)
        descriptionDivider = view.findViewById(R.id.description_divider)
        frames = view.findViewById(R.id.frames)
        video = view.findViewById(R.id.video)
        openTV = view.findViewById(R.id.open_film)
        shareTV = view.findViewById(R.id.share_film)
        factsLayout = view.findViewById(R.id.facts_layout)
        factsText = view.findViewById(R.id.facts)
        //factsHead = view.findViewById(R.id.facts_head)
        allFacts = view.findViewById(R.id.all_facts_film)
        sequelsPrequels = view.findViewById(R.id.sequels_prequels)
        actorsRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        similarFilmsRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        filmViewModel.setMovieId(requireArguments().getInt(EXTRA_MOVIE_ID))
        checkMovieId()
        categoryNameActors.text = getString(R.string.actors)
        categoryNameSimilarFilms.text = getString(R.string.similar_films)

        lifecycleScope.launch() {
            var movie = withContext(IOCoroutineContext) {
                filmViewModel.getMovieById()
            }
            setToolbarTitle(movie)
            fetchUI(movie)
        }

        lifecycleScope.launch {
            filmViewModel.actors.collect { actors ->
                if (actors.isNotEmpty()) fillActorsUI(actors)
            }
        }

        lifecycleScope.launch {
                filmViewModel.similarMovies.onEach { similarFilms ->
                    fillSimilarFilmsUI(similarFilms)
                }.collect()
        }

        lifecycleScope.launch(IOCoroutineContext) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    filmViewModel.favoriteStatus.onEach {
                        if (it) favoriteCheckBox.isChecked = true
                    }.collect()
            }
        }

        lifecycleScope.launch(IOCoroutineContext) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    filmViewModel.watchLaterStatus.onEach {
                        if (it) watchLaterCheckBox.isChecked = true
                    }.collect()
            }
        }

        favoriteCheckBox.setOnClickListener { v ->
            if (favoriteCheckBox.isChecked) {
                Toast.makeText(this.context, R.string.added_to_favorite, Toast.LENGTH_SHORT).show()
                addMovieToMyFilmsDb(1)
                favoriteCheckBox.isPressed = false
                return@setOnClickListener
            } else {
                deleteMovieFromFirebaseService(DELETE_FAVORITE_COLLECTION)
                Toast.makeText(this.context, R.string.deleted_from_favorite, Toast.LENGTH_SHORT).show()
                filmViewModel.deleteFromDB(addVariants[1])
                favoriteCheckBox.isPressed = true
                return@setOnClickListener
            }
        }

        watchLaterCheckBox.setOnClickListener { v ->
            if (watchLaterCheckBox.isChecked /*&& statusWLCB*/) {
                Toast.makeText(this.context, R.string.added_to_watch_later, Toast.LENGTH_SHORT).show()
                addMovieToMyFilmsDb(0)
                //statusWLCB = true
                watchLaterCheckBox.isPressed = false
                return@setOnClickListener
            } else {
                deleteMovieFromFirebaseService(DELETE_WATCH_LATER_COLLECTION)
                Toast.makeText(this.context, R.string.deleted_from_watch_later, Toast.LENGTH_SHORT).show()
                filmViewModel.deleteFromDB(addVariants[0])
                //statusWLCB = false
                watchLaterCheckBox.isPressed = true
                return@setOnClickListener
            }
        }

        frames.setOnClickListener { view ->
            var bundle = bundleOf(EXTRA_MOVIE_ID to movieCurrent.data.filmId)
            view.findNavController().navigate(R.id.action_filmFragment_to_filmFramesFragment, bundle)
        }

        allFacts.setOnClickListener { view ->
            if (movieCurrent.data.facts.size >= 2) {
                var bundle = bundleOf(EXTRA_FACTS_TEXT to movieCurrent.data.facts)
                view.findNavController().navigate(R.id.action_global_factsFragment, bundle)
            }
        }

        floatingButton.setOnClickListener { view ->
            isClick()
        }

        video.setOnClickListener { view ->
            var bundle = bundleOf(EXTRA_MOVIE_ID to movieCurrent.data.filmId)
            view.findNavController().navigate(R.id.action_filmFragment_to_trailerFragment, bundle)
        }

        sequelsPrequels.setOnClickListener { view ->
            var bundle = bundleOf(EXTRA_MOVIE_ID to movieCurrent.data.filmId)
            Log.e(TAG, movieCurrent.data.filmId.toString())
            view.findNavController().navigate(R.id.action_filmFragment_to_sequelsPrequelsFragment, bundle)
        }

        openTV.setOnClickListener { view ->
            val address = Uri.parse(movieCurrent.data.webUrl)
            val openIntent = Intent(Intent.ACTION_VIEW, address)
            if (context?.let { openIntent.resolveActivity(it.packageManager) } != null) {
                startActivity(openIntent)
            }
        }

        shareTV.setOnClickListener { view ->
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, makeShareText())
            if (context?.let { shareIntent.resolveActivity(it.packageManager) } != null) {
                startActivity(shareIntent)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if(activity != null) {
            (activity as MainActivity).getBackPressedListener(null)
        }
    }

    override fun onStop() {
        super.onStop()
        filmViewModel.saveMovieId()
    }

    override fun onBackPressedL() {
        if (isOpen) isClick()
    }

    override fun getFlag(): Boolean {
        return isOpen
    }

    private fun setToolbarTitle(movie: MovieById) {
        val language = Locale.getDefault().language
        if (language.contains("ru")) showToolbar(requireActivity().findViewById(R.id.toolbar), movie.data.nameRu?:"")
        if (language.contains("en")) showToolbar(requireActivity().findViewById(R.id.toolbar), movie.data.nameEn?:"")

    }

    private fun isClick() {
        if (!isOpen) {
            mainLayout.visibility = GONE
            floatingButton.setImageDrawable(ContextCompat.getDrawable(this.requireContext(), R.drawable.outline_close_24))
            checkboxLayout.visibility = VISIBLE
            isOpen = true
        } else {
            mainLayout.visibility = VISIBLE
            floatingButton.setImageDrawable(ContextCompat.getDrawable(this.requireContext(), R.drawable.outline_add_24))
            checkboxLayout.visibility = GONE
            isOpen = false
        }
    }

    private fun addMovieToMyFilmsDb(position: Int) {
        val eventDetails = Bundle()
        eventDetails.putString("message", "Adding in this category")
        if (position == 0) {
            sendDataForFirebaseService(ADD_WATCH_LATER_COLLECTION, addVariants[0])
            filmViewModel.addToDB(addVariants[0])
            filmViewModel.sendEvent("add_to_watch_later", eventDetails)
        }
        if (position == 1) {
            sendDataForFirebaseService(ADD_FAVORITE_COLLECTION, addVariants[1])
            filmViewModel.addToDB(addVariants[1])
            filmViewModel.sendEvent("add_to_favorites", eventDetails)
        }
    }

    private fun deleteMovieFromFirebaseService(type: String) {
        var intent = Intent(requireActivity(), FirebaseStorageService::class.java)
        intent.putExtra(TYPE_COLLECTION, type)
        intent.putExtra(EXTRA_ITEM_ID, filmViewModel.getMovieId().toString())
        requireActivity().startService(intent)
    }

    private fun sendDataForFirebaseService(type: String, parent: String) {
        var intent = Intent(requireActivity(), FirebaseStorageService::class.java)
        intent.putExtra(TYPE_COLLECTION, type)
        intent.putExtra(EXTRA_MOVIE, filmViewModel.parseForFirebase(movieCurrent, parent))
        requireActivity().startService(intent)
    }

    private fun fetchUI(movie: MovieById) {
        movieCurrent = movie
        if (movieCurrent.data.facts.size < 2) allFacts.visibility = GONE
        Picasso.get()
                .load(movie.data.posterUrl)
                .placeholder(R.drawable.splash_screen)
                .error(R.drawable.splash_screen)
                .fit()
                .into(filmImage)
        visibilityText(filmRuNameText, movie.data.nameRu?:"", null)
        if (movie.data.year > 1800) {
            var text = if (movie.data.nameEn != null && movie.data.nameEn!!.isNotEmpty()) "${movie.data.nameEn} (${movie.data.year})"
            else movie.data.year.toString()
            visibilityText(filmEnNameAndYearText, text, null)
        } else visibilityText(filmEnNameAndYearText, movie.data.nameEn ?:"", null)
        var genres = ""
        for (counts in movie.data.genres.indices) {
            genres += movie.data.genres[counts].genre
            if (counts != movie.data.genres.size-1) genres += ", "
        }
        visibilityText(genresText, genres, null)

        var countryAndDuration = ""
        for (counts in movie.data.countries.indices) {
            countryAndDuration += movie.data.countries[counts].country
            if (counts != movie.data.countries.size-1) countryAndDuration += ", "
        }
        if (movie.data.filmLength != "" && movie.data.filmLength != null && movie.data.filmLength.isNotEmpty()) {
            countryAndDuration += if (movie.data.filmLength[0].toString() == "0" && movie.data.filmLength.lastIndex == 4) ", ${movie.data.filmLength.subSequence(1, movie.data.filmLength.lastIndex + 1)}"
            else ", ${movie.data.filmLength}"
        }
        visibilityText(countryAndDurationText, countryAndDuration, null)
        visibilityText(sloganText, movie.data.slogan, null)

        visibilityText(descriptionText, movie.data.description, descriptionDivider)
        //Log.e(TAG, "description:"+ movie.data.description + ":")
        if (movie.data.facts.isNotEmpty()) {
            visibilityText(factsText, movie.data.facts[0], factsLayout)
            if (similarsLayout.visibility == GONE && similarFilmsRV.visibility == GONE) {
                similarNonFactsGrayDiv.visibility = GONE
                nonSimilarGrayDivFacts.visibility = GONE
            }
        }
        else {
            visibilityText(factsText, "", factsLayout)
            if (similarFilmsRV.visibility == VISIBLE && similarsLayout.visibility == VISIBLE) {
                similarNonFactsGrayDiv.visibility = VISIBLE
            }
        }
        mainLayout.visibility = VISIBLE
        floatingButton.visibility = VISIBLE
        filmViewModel.addToWatched(movieCurrent)
    }

    private fun visibilityText(textView: TextView, text: String, view: View?) {
        if (text != "" && text != " " && text != "null" && text != null) textView.text = text
        else {
            textView.visibility = GONE
            if (view != null) view.visibility = GONE
        }
    }

    private fun makeShareText() : String {
        var shareText = ""
        shareText = if (movieCurrent.data.nameEn?.isNotBlank() == true) "\"${movieCurrent.data.nameRu}\"(\"${movieCurrent.data.nameEn}\", ${movieCurrent.data.year}) #kinopoisk\n${movieCurrent.data.webUrl}"
        else "\"${movieCurrent.data.nameRu}\"(${movieCurrent.data.year}) #kinopoisk\n${movieCurrent.data.webUrl}"
        return shareText
    }

    private fun fillActorsUI(actors: List<StaffPerson>) {
        if (descriptionText.visibility == VISIBLE) actorsGrayDiv.visibility = VISIBLE
        actorsIncludeLayout.visibility = VISIBLE
        actorsDivider.visibility = VISIBLE
        actorsAll.visibility = GONE
        actorsRV.adapter = PopularPersonAdapter(actors, false)
    }

    private fun fillSimilarFilmsUI(similarFilms: List<SimilarFilm>) {
        if (similarFilms.isNotEmpty()) {
            similarsLayout.visibility = VISIBLE
            similarFilmsAll.visibility = GONE
            similarFilmsRV.adapter = SimilarFilmsAdapter(similarFilms)
            similarNonFactsGrayDiv.visibility = VISIBLE
            if (factsLayout.visibility == GONE) {
                similarNonFactsGrayDiv.visibility = VISIBLE
            }
        }
    }

    private fun checkMovieId() {
        if (!filmViewModel.isMovieIdReceived()) {
            Toast.makeText(context, getString(R.string.error), Toast.LENGTH_SHORT).show()
            NavHostFragment.findNavController(this@FilmFragment).popBackStack()
        }
    }
}