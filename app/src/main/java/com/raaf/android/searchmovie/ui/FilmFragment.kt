package com.raaf.android.searchmovie.ui

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.*
import androidx.fragment.app.Fragment
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.Movie
import com.raaf.android.searchmovie.dataModel.rootJSON.MovieById
import com.squareup.picasso.Picasso

class FilmFragment : Fragment() {

    lateinit var addVariants: List<String>
    private lateinit var filmViewModel: FilmViewModel
    private lateinit var movieCurrent: MovieById
    private var isOpen = false

    private lateinit var floatingButton: FloatingActionButton
    private lateinit var mainLayout: LinearLayout
    private lateinit var checkboxLayout: LinearLayout
    private lateinit var watchLaterCheckBox: CheckBox
    private lateinit var favoriteCheckBox: CheckBox

    private lateinit var filmImage: ImageView
    private lateinit var filmRuNameText: TextView
    private lateinit var filmEnNameText: TextView
    private lateinit var genresText: TextView
    private lateinit var countryAndDurationText: TextView
    private lateinit var sloganText: TextView
    private lateinit var descriptionText: TextView

    private lateinit var frames: TextView
    private lateinit var video: TextView

    private lateinit var factsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filmViewModel = ViewModelProvider(this).get(FilmViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_film, container, false)
        floatingButton = view.findViewById(R.id.fab)
        mainLayout = view.findViewById(R.id.main_layout)
        checkboxLayout = view.findViewById(R.id.checkbox_layout)
        watchLaterCheckBox = view.findViewById(R.id.watch_later_checkBox)
        favoriteCheckBox = view.findViewById(R.id.favorite_movies_checkBox)
        filmImage = view.findViewById(R.id.film_image)
        filmRuNameText = view.findViewById(R.id.film_ru_name_text)
        filmEnNameText = view.findViewById(R.id.film_en_name_text)
        genresText = view.findViewById(R.id.genres_text)
        countryAndDurationText = view.findViewById(R.id.country_and_duration_text)
        sloganText = view.findViewById(R.id.slogan)
        descriptionText = view.findViewById(R.id.description)

        frames = view.findViewById(R.id.frames)
        video = view.findViewById(R.id.video)

        factsText = view.findViewById(R.id.facts)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addVariants = listOf(getString(R.string.watch_later), getString(R.string.favorite_movies))
        filmViewModel.movieLiveData.observe(
                viewLifecycleOwner,
                Observer { movieResponse ->
                    fetchContentView(movieResponse)
                })
        requireArguments().getInt("filmId").let { filmViewModel.fetchFilmById(it) }

        frames.setOnClickListener { view ->
            var bundle = bundleOf("filmId" to movieCurrent.data.filmId)
            view.findNavController().navigate(R.id.action_filmFragment_to_filmFramesFragment, bundle)
        }

        floatingButton.setOnClickListener { view ->
            isClick()
        }

        watchLaterCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) addMovieToMyFilmsDb(0)
        }

        favoriteCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) addMovieToMyFilmsDb(1)
        }

        /*video.setOnClickListener { view ->
            var bundle = bundleOf("filmId" to movieCurrent.data.filmId)
            view.findNavController().navigate(R.id., bundle)
        }*/
    }

    private fun isClick() {
        if (!isOpen) {
            mainLayout.visibility = GONE
            checkboxLayout.visibility = VISIBLE
            //Add changes icon
            isOpen = true
        } else {
            mainLayout.visibility = VISIBLE
            checkboxLayout.visibility = GONE
            //Add changes icon
            isOpen = false
        }
    }

    private fun addMovieToMyFilmsDb(position: Int) {
        if (position == 0) filmViewModel.addToDb(movieCurrent.data.filmId, addVariants[0])
        if (position == 1) filmViewModel.addToDb(movieCurrent.data.filmId, addVariants[1])
    }

    private fun fetchContentView(movie: MovieById) {
        movieCurrent = movie
        Picasso.get()
                .load(movie.data.posterUrl)
                .placeholder(R.drawable.splash_screen)
                .error(R.drawable.splash_screen)
                .fit()
                .into(filmImage)
        visibilityText(filmRuNameText, movie.data.nameRu)
        visibilityText(filmEnNameText, movie.data.nameEn)

        var genres = ""
        for (counts in movie.data.genres.indices) {
            genres += movie.data.genres[counts].genre
            if (counts != movie.data.genres.size-1) genres += ", "
        }
        visibilityText(genresText, genres)

        var countryAndDuration = ""
        for (counts in movie.data.countries.indices) {
            countryAndDuration += movie.data.countries[counts].country
            if (counts != movie.data.countries.size-1) countryAndDuration += ", "
        }
        if (movie.data.filmLength != "") countryAndDuration += ", ${movie.data.filmLength}"
        visibilityText(countryAndDurationText, countryAndDuration)
        visibilityText(sloganText, movie.data.slogan)

        visibilityText(descriptionText, movie.data.description)
        var facts = movie.data.facts.toString()
        facts = facts.substring(1, facts.lastIndex)
        visibilityText(factsText, facts)
        factsText.movementMethod = ScrollingMovementMethod()

    }

    private fun visibilityText(textView: TextView, text: String) {
        if (text != "") textView.text = text
        else textView.visibility = GONE
    }
}