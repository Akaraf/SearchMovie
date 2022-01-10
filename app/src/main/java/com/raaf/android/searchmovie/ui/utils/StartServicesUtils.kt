package com.raaf.android.searchmovie.ui.utils

import android.content.Context
import android.content.Intent
import com.raaf.android.searchmovie.backgroundJob.services.RefreshDBService

fun startRefreshDBService(context: Context) {
    val intent = Intent(context, RefreshDBService::class.java)
    context.startService(intent)
}