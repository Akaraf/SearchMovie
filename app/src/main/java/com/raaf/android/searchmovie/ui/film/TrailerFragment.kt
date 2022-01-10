package com.raaf.android.searchmovie.ui.film

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.Trailer
import com.raaf.android.searchmovie.ui.utils.showToolbar

private const val EXTRA_FILM_ID = "filmId"

class TrailerFragment : Fragment() {

    lateinit var trailersList: List<Trailer>
    lateinit var trailerViewModel: TrailerViewModel
    lateinit var trailersRecycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        showToolbar(requireActivity().findViewById(R.id.toolbar), getString(R.string.trailers))
        super.onCreate(savedInstanceState)
        trailerViewModel = ViewModelProvider(this).get(TrailerViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_trailer, container, false)
        trailersRecycler = view.findViewById(R.id.trailer_recycler)
        trailersRecycler.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trailerViewModel.trailersLiveData.observe(
                viewLifecycleOwner,
                Observer { trailersResponse->
                    if (trailersResponse.isNotEmpty()) {
                        trailersList = trailersResponse
                        trailersRecycler.adapter = TrailerAdapter(trailersList)
                    } else noDataMakeToast()
                }
        )
        requireArguments().getInt(EXTRA_FILM_ID).let { trailerViewModel.fetchTrailers(it) }
    }

    private fun noDataMakeToast() {
        Toast.makeText(context, getString(R.string.no_sequels_prequels), Toast.LENGTH_SHORT).show()
    }
}