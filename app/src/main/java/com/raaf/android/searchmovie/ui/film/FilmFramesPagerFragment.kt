package com.raaf.android.searchmovie.ui.film

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.Frame
import com.raaf.android.searchmovie.ui.showToolbar

private const val TAG = "FilmFramesPagerFragment"
private const val EXTRA_INITIAL_ITEM_POS = "position"
private const val EXTRA_FRAME_ITEMS = "frames"
private const val EXTRA_IMAGE = "image"

class FilmFramesPagerFragment : Fragment() {

    private lateinit var framesViewpager: ViewPager2
    private var currentItem = 0
    private lateinit var frames: ArrayList<Frame>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_film_frames_pager, container, false)
        framesViewpager = view.findViewById(R.id.frames_view_pager)
        showToolbar(requireActivity().findViewById(R.id.toolbar), "")
        currentItem = requireArguments().getInt(EXTRA_INITIAL_ITEM_POS, 0)
        frames = requireArguments().getParcelableArrayList<Frame>(EXTRA_FRAME_ITEMS)!!
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        framesViewpager.adapter = FramePagerAdapter(childFragmentManager, lifecycle, frames)
        framesViewpager.currentItem = currentItem
    }
}