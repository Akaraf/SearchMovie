package com.raaf.android.searchmovie.ui.film

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.Frame
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

private const val TAG = "FilmFramesFragment"
private const val EXTRA_FRAME_ITEMS = "frames"

class FrameDetailFragment : Fragment() {

    private lateinit var frameImage: ImageView
    private lateinit var frameItem: Frame
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        //Code for lollipop
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_frame_detail, container, false)
        frameImage = view.findViewById(R.id.detail_image)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        frameItem = requireArguments().getParcelable<Frame>(EXTRA_FRAME_ITEMS)!!

        Picasso.get()
                .load(frameItem.url)
                .noFade()
                .into(frameImage, object:Callback {
                    override fun onSuccess() {
                        startPostponedEnterTransition()
                    }

                    override fun onError(e: Exception?) {
                        startPostponedEnterTransition()
                    }

                })
    }
}