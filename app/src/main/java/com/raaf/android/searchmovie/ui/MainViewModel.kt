package com.raaf.android.searchmovie.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.Settings
import com.raaf.android.searchmovie.api.FilmFetcher
import java.util.*
import javax.inject.Inject

private const val TAG = "MainViewModel"
private const val PREF_APP_IS_RUNNING = "RunningFlag"

class MainViewModel : ViewModel() {

    var refreshJobPreferences: SharedPreferences? = null

    fun setValueForRefreshJob(flag: Int, context: Context) {
        if (refreshJobPreferences == null) refreshJobPreferences = context.getSharedPreferences("REFRESH_JOB_PREFERENCES",0)
        refreshJobPreferences!!.edit().putInt(PREF_APP_IS_RUNNING, flag).apply()
    }
}