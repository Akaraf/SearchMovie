package com.raaf.android.searchmovie.ui.utils

import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.Toolbar

    fun showToolbar(toolbar: Toolbar, title: String) {
        toolbar.title = title
        toolbar.visibility = VISIBLE
    }

    fun showToolbar(toolbar: Toolbar) {
        toolbar.visibility = VISIBLE
    }

    fun hideToolbar(toolbar: Toolbar) {
        toolbar.visibility = GONE
    }