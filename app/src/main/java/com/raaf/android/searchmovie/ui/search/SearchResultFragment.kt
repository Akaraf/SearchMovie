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
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.Film
import com.squareup.picasso.Picasso

private const val TAG = "Search Result Fragment"

class SearchResultFragment : Fragment() {

    private lateinit var searchResultViewModel: SearchResultViewModel
    private lateinit var filmRecyclerView: RecyclerView
    private var pageSearch = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchResultViewModel = ViewModelProvider(this).get(SearchResultViewModel::class.java)

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
        requireArguments().getString("query")?.let { searchResultViewModel.fetchFilms(it, pageSearch) }
    }

    private class FilmHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val imageFilm: ImageView = itemView.findViewById(R.id.image)
        val nameRu: TextView = itemView.findViewById(R.id.nameRu)
        val rating: TextView = itemView.findViewById(R.id.rating)
        val nameEn: TextView = itemView.findViewById(R.id.nameEn)
        val year: TextView = itemView.findViewById(R.id.year)
        val country: TextView = itemView.findViewById(R.id.country)
        val genres: TextView = itemView.findViewById(R.id.genres)

        val textSeparator: TextView = itemView.findViewById(R.id.textSeparator)
        val imageDote: ImageView = itemView.findViewById(R.id.imageDote)

        var filmId = 0

        init {
            itemView.setOnClickListener(this)
        }

        fun getFilmId(filmId: Int) {
            this.filmId = filmId
        }

        override fun onClick(v: View) {
            var bundle = bundleOf("filmId" to filmId)
            v.findNavController().navigate(R.id.action_global_filmFragment, bundle)
        }

    }

    private class FilmAdapter(private val filmItems: List<Film>) : RecyclerView.Adapter<FilmHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_film, parent,false)
            return FilmHolder(view)
        }

        override fun getItemCount(): Int = filmItems.size

        override fun onBindViewHolder(holder: FilmHolder, position: Int) {
            holder.setIsRecyclable(false)
            val filmItem = filmItems[position]
            val imageView = holder.imageFilm
            Picasso.get()
                .load(filmItem.posterUrl)
                .placeholder(R.drawable.splash_screen)
                .error(R.drawable.splash_screen)
                .fit()
                .into(imageView)

            val checkRating = filmItem.rating.toString()
//            First line cardView
            when {
                (filmItem.nameRu!!.isNotBlank() && checkRating != "0.0") -> {
                    holder.rating.text = filmItem.rating
                    if (filmItem.nameRu!!.length > 32) holder.nameRu.text = truncate(filmItem.nameRu!!, 32)
                    else holder.nameRu.text = filmItem.nameRu
                }
                (filmItem.nameRu!!.isNotBlank() && checkRating == "0.0") -> {
                    holder.rating.visibility = GONE
                    if (filmItem.nameRu!!.length > 37) holder.nameRu.text = truncate(filmItem.nameRu!!, 37)
                    else holder.nameRu.text = filmItem.nameRu
                }
                (filmItem.nameRu!!.isBlank() && checkRating != "0.0") -> {
                    holder.rating.text = filmItem.rating
                    if (filmItem.nameEn!!.length > 32) holder.nameRu.text = truncate(filmItem.nameEn!!, 32)
                    else holder.nameRu.text = filmItem.nameEn
                    if (filmItem.year.isNotBlank()) {
                        holder.textSeparator.visibility = GONE
                        holder.year.text = filmItem.year
                    } else {
                        holder.textSeparator.visibility = GONE
                        holder.year.visibility = GONE
                        holder.nameEn.visibility = GONE
                    }
                }
                (filmItem.nameRu!!.isBlank() && checkRating == "0.0") -> {
                    holder.rating.visibility = GONE
                    if (filmItem.nameEn!!.length > 37) holder.nameRu.text = truncate(filmItem.nameEn!!, 37)
                    else holder.nameRu.text = filmItem.nameEn
                    if (filmItem.year.isNotBlank()) {
                        holder.nameEn.visibility = GONE
                        holder.year.text = filmItem.year
                    } else {
                        holder.year.visibility = GONE
                        holder.nameEn.visibility = GONE
                    }
                }
            }

//            Second line cardView
            when {
                (filmItem.nameEn!!.isNotBlank() && filmItem.year.isNotBlank() && filmItem.nameRu!!.isNotBlank()) -> {
                    holder.nameEn.text = filmItem.nameEn
                    holder.year.text = filmItem.year
                }
                (filmItem.nameEn!!.isNotBlank() && filmItem.year.isBlank() && filmItem.nameRu!!.isNotBlank()) -> {
                    holder.nameEn.text = filmItem.nameEn
                    holder.textSeparator.visibility = GONE
                }
                (filmItem.nameEn!!.isBlank() && filmItem.year.isBlank()) -> {
                    holder.textSeparator.visibility = GONE
                    holder.nameEn.visibility = GONE
                    holder.year.visibility = GONE
                }
                (filmItem.nameEn!!.isBlank() && filmItem.year.isNotBlank()) -> {
                    holder.textSeparator.visibility = GONE
                    holder.nameEn.text = filmItem.year
                    holder.year.visibility = GONE
                }
            }

//            Third line cardView
            var countries = ""
            for (counts in filmItem.countries.indices) {
                countries += filmItem.countries[counts].country
                if (counts != filmItem.countries.size-1) countries += ", "
            }
            if (countries != "") holder.country.text = countries
            else {
                holder.country.visibility = GONE
                holder.imageDote.visibility = GONE
            }

            var genres = ""
            for (counts in filmItem.genres.indices) {
                genres += filmItem.genres[counts].genre
                if (counts != filmItem.genres.size-1) genres += ", "
            }
            if (genres != "") holder.genres.text = genres
            else {
                holder.genres.visibility = GONE
                holder.imageDote.visibility = GONE
            }

            Log.d(TAG, "card #$position")
            holder.getFilmId(filmItem.filmId)
        }

        fun truncate(text: String, numberOfSymbols: Int): String {
            var newText = text.substring(0, numberOfSymbols)
            newText = newText.trimEnd()
            newText = "$newText..."
            return newText
        }
    }
}