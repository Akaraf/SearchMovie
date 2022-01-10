package com.raaf.android.searchmovie.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.ui.adapters.FactsAdapter
import com.raaf.android.searchmovie.ui.utils.showToolbar

private const val EXTRA_FACTS_TEXT = "facts"

class FactsFragment : Fragment() {

   private lateinit var factsRecycler: RecyclerView
   private lateinit var facts: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        showToolbar(requireActivity().findViewById(R.id.toolbar) as MaterialToolbar, getString(R.string.facts))
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_facts, container, false)
        factsRecycler = view.findViewById(R.id.facts_recycler_view)
        factsRecycler.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false)
        facts = requireArguments().getStringArrayList(EXTRA_FACTS_TEXT) ?: listOf()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (facts.isNotEmpty()) factsRecycler.adapter = FactsAdapter(facts)
    }
}