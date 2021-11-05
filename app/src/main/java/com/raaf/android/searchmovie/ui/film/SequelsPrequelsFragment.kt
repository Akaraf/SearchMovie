package com.raaf.android.searchmovie.ui.film

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.rootJSON.SequelsPrequelsResponse
import com.raaf.android.searchmovie.ui.adapters.SequelsPrequelsAdapter
import com.raaf.android.searchmovie.ui.showToolbar

private const val TAG = "SequelsPrequelsFragment"
private const val EXTRA_FILM_ID = "filmId"

class SequelsPrequelsFragment : Fragment() {

    private lateinit var categoryNames: Map<String, String>
    private lateinit var sequelsPrequelsVM: SequelsPrequelsViewModel

    private lateinit var sequelsLayout: LinearLayout
    private lateinit var sequelsRecyclerView: RecyclerView
    private lateinit var prequelsLayout: LinearLayout
    private lateinit var prequelsRecyclerView: RecyclerView
    private lateinit var remakeLayout: LinearLayout
    private lateinit var remakeRecyclerView: RecyclerView
    private lateinit var unknownLayout: LinearLayout
    private lateinit var unknownRecyclerView: RecyclerView

    var filmId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "SPF is started!!")
        sequelsPrequelsVM = ViewModelProvider(this).get(SequelsPrequelsViewModel::class.java)
        categoryNames = mapOf("SEQUEL" to this.getString(R.string.sequel), "PREQUEL" to this.getString(R.string.prequel), "REMAKE" to this.getString(R.string.remake), "UNKNOWN" to this.getString(R.string.unknown))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sequels_prequels, container, false)
        showToolbar(requireActivity().findViewById(R.id.toolbar), getString(R.string.sequels_prequels))
        sequelsLayout = view.findViewById(R.id.sequels_layout)
        sequelsRecyclerView = view.findViewById(R.id.sequels_recycler_view)
        prequelsLayout = view.findViewById(R.id.prequels_layout)
        prequelsRecyclerView = view.findViewById(R.id.prequels_recycler_view)
        remakeLayout = view.findViewById(R.id.remake_layout)
        remakeRecyclerView = view.findViewById(R.id.remake_recycler_view)
        unknownLayout = view.findViewById(R.id.unknown_layout)
        unknownRecyclerView = view.findViewById(R.id.unknown_recycler_view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        filmId = requireArguments().getInt(EXTRA_FILM_ID) ?: 0
        Log.e(TAG, "filmId:$filmId")
        sequelsPrequelsVM.movieLiveData.observe(
            viewLifecycleOwner,
            Observer { films ->
                Log.e(TAG, "films size: " + films.size.toString())
                if (films.isNotEmpty()) sortFilmsByCategories(films)
                else noDataMakeToast()
            }
        )
        if (filmId != 0) sequelsPrequelsVM.fetchFilmId(filmId)
    }

    private fun sortFilmsByCategories(films: List<SequelsPrequelsResponse>) {
        for (count in categoryNames) {
            var categoryItems = mutableListOf<SequelsPrequelsResponse>()
            films.forEach { if (it.relationType.contains(count.key)) categoryItems.add(it) }
            if (categoryItems.isNotEmpty()) {
                categoryItems.forEach { it.relationType = count.value }
                fillUI(count.key, categoryItems)
            }
        }
    }

    private fun fillUI(category: String, listForAdapter: List<SequelsPrequelsResponse>) {
        when (category) {
            categoryNames.keys.elementAt(0) -> {
                sequelsLayout.visibility = VISIBLE
                sequelsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//                sequelsRecyclerView.visibility = VISIBLE
                sequelsRecyclerView.adapter = SequelsPrequelsAdapter(listForAdapter)
            }
            categoryNames.keys.elementAt(1) -> {
                prequelsLayout.visibility = VISIBLE
                prequelsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//                prequelsRecyclerView.visibility = VISIBLE
                prequelsRecyclerView.adapter = SequelsPrequelsAdapter(listForAdapter)
            }
            categoryNames.keys.elementAt(2) -> {
                remakeLayout.visibility = VISIBLE
                remakeRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//                remakeRecyclerView.visibility = VISIBLE
                remakeRecyclerView.adapter = SequelsPrequelsAdapter(listForAdapter)
            }
            categoryNames.keys.elementAt(3) -> {
                unknownLayout.visibility = VISIBLE
                unknownRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//                unknownRecyclerView.visibility = VISIBLE
                unknownRecyclerView.adapter = SequelsPrequelsAdapter(listForAdapter)
            }
            else -> {
                Log.e(TAG, "error list, size: " + listForAdapter.count().toString())
            }
        }
    }

    private fun noDataMakeToast() {
        Toast.makeText(context, getString(R.string.no_sequels_prequels), Toast.LENGTH_SHORT).show()
    }
}