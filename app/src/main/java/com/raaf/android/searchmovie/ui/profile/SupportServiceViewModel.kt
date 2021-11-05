package com.raaf.android.searchmovie.ui.profile

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.raaf.android.searchmovie.App
import javax.inject.Inject

class SupportServiceViewModel : ViewModel() {

    @Inject lateinit var firebaseAnalytics: FirebaseAnalytics

    init {
        App.appComponent.inject(this)
    }

    fun sendMessage(bundle: Bundle) {
        var eventName = "message_for_support_service"
        firebaseAnalytics.logEvent(eventName, bundle)
        //bundle.putString(firebaseAnalytics.Param.ITEM_NAME, "")CONTENT_TYPE
    }
}