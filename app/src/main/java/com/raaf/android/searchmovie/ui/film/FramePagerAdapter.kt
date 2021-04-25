package com.raaf.android.searchmovie.ui.film

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.raaf.android.searchmovie.dataModel.Frame

private const val EXTRA_FRAME_ITEMS = "frames"

class FramePagerAdapter(fragmentManager: FragmentManager,lifecycle: Lifecycle, listFrames: List<Frame>) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val frameItems: List<Frame> = listFrames

    override fun getItemCount(): Int = frameItems.size

    override fun createFragment(position: Int): Fragment {
        var bundle = bundleOf(EXTRA_FRAME_ITEMS to frameItems[position])
        var fragment = FrameDetailFragment()
        fragment.arguments = bundle
        return fragment
    }


}