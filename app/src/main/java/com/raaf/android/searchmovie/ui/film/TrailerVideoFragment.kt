package com.raaf.android.searchmovie.ui.film

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.raaf.android.searchmovie.R

private const val TAG = "TrailerVideoFragment"
private const val EXTRA_URL = "url"

class TrailerVideoFragment : Fragment() {

    private lateinit var url: String
    private lateinit var videoWV: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_trailer_video, container, false)
        videoWV = view.findViewById(R.id.video_web_view)
        requireArguments().getString(EXTRA_URL).let {
            if (it != null) {
                Log.d(TAG, "url:$it")
                url = it
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val check = youTube(url)
        if (!check) {
            var webSetting = videoWV.settings
            webSetting.javaScriptEnabled = true
            videoWV.loadUrl(url)
        }
    }

    private fun youTube(url: String) : Boolean {
        return if (url.contains("youtube")) {
            //val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + url.split("=")[1]))
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            try {
                //context?.startActivity(appIntent)
                context?.startActivity(webIntent)
                NavHostFragment.findNavController(this@TrailerVideoFragment).popBackStack()
            } catch (ex: ActivityNotFoundException) {
                return false
                //context?.startActivity(webIntent)
            }

            true
        } else false
    }
}