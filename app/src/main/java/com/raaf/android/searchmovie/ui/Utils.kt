package com.raaf.android.searchmovie.ui

import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.Toolbar

    fun showToolbar(toolbar: Toolbar, title: String) {
        toolbar.setTitle(title)
        toolbar.visibility = VISIBLE
    }

    fun showToolbar(toolbar: Toolbar) {
        toolbar.visibility = VISIBLE
    }

    fun hideToolbar(toolbar: Toolbar) {
        toolbar.visibility = GONE
    }