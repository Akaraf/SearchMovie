package com.raaf.android.searchmovie.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.ui.home.swipeFragments.CompilationFragment
import com.raaf.android.searchmovie.ui.home.swipeFragments.MyFilmsFragment
import com.raaf.android.searchmovie.ui.home.swipeFragments.TopFragment

private const val TAG = "Search Result Fragment"
//You need to check 544 page in heeead, before you will going to next step

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        viewPager = view.findViewById(R.id.view_pager)
        tabLayout = view.findViewById(R.id.tab_layout)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabLayoutTittles = arrayOf(getString(R.string.compilations), getString(R.string.top), getString(R.string.my_films))
        viewPager.adapter = HomePagerAdapter(childFragmentManager, lifecycle)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabLayoutTittles[position]
            viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }

    private inner class HomePagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> CompilationFragment()
            1 -> TopFragment()
            else -> MyFilmsFragment()
        }
    }
}