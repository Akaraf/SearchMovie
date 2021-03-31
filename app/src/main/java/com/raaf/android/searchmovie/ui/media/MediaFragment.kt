package com.raaf.android.searchmovie.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.raaf.android.searchmovie.R

class MediaFragment : Fragment() {

    private lateinit var mediaViewModel: MediaViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mediaViewModel =
                ViewModelProvider(this).get(MediaViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_media, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        mediaViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}