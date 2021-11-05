package com.raaf.android.searchmovie.ui.film

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.Frame
import com.raaf.android.searchmovie.ui.showToolbar

//Для кадров с FilmFragment

private const val TAG = "FilmFramesFragment"
private const val EXTRA_INITIAL_ITEM_POS = "position"
private const val EXTRA_FRAME_ITEMS = "frames"
private const val EXTRA_IMAGE = "image"
private const val EXTRA_FILM_ID = "filmId"

class FilmFramesFragment : Fragment(), FramesItemClickListener {

    private lateinit var filmFramesViewModel: FilmFramesViewModel
    private lateinit var framesRecycler: RecyclerView
    private lateinit var frames: List<Frame>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filmFramesViewModel = ViewModelProvider(this).get(FilmFramesViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_film_frames, container, false)
        showToolbar(requireActivity().findViewById(R.id.toolbar), getString(R.string.frames))
        framesRecycler = view.findViewById(R.id.frames_recycler_view)
        framesRecycler.layoutManager = GridLayoutManager(context, 4)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        filmFramesViewModel.framesLiveData.observe(
                viewLifecycleOwner,
                Observer { framesResponse ->
                    frames = framesResponse
                    if (frames.isEmpty()) noDataMakeToast()
                    Log.d(TAG, "${framesResponse.size}")
                    framesRecycler.adapter = FramesAdapter(frames, this)
                    framesRecycler.setHasFixedSize(true)
                }
        )
        requireArguments().getInt(EXTRA_FILM_ID).let { filmFramesViewModel.fetchFrames(it) }
    }

    override fun onFrameItemClick(view: View, position: Int, frameItem: Frame) {
        if (frames.isNotEmpty()) {
            var bundle = bundleOf(EXTRA_INITIAL_ITEM_POS to position, EXTRA_FRAME_ITEMS to frames)
            NavHostFragment.findNavController(this@FilmFramesFragment).navigate(R.id.action_filmFramesFragment_to_filmFramesPagerFragment, bundle)
        }
    }

    private fun noDataMakeToast() {
        Toast.makeText(context, getString(R.string.no_sequels_prequels), Toast.LENGTH_SHORT).show()
    }
}