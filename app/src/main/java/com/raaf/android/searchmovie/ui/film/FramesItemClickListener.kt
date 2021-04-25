package com.raaf.android.searchmovie.ui.film

import android.view.View
import android.widget.ImageView
import com.raaf.android.searchmovie.dataModel.Frame

interface FramesItemClickListener {

    fun onFrameItemClick(view: View, position: Int, frameItem: Frame)
}