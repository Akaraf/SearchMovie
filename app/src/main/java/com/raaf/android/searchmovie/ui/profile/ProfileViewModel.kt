package com.raaf.android.searchmovie.ui.profile

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.repository.User
import javax.inject.Inject

private const val TAG = "ProfileViewModel"

class ProfileViewModel : ViewModel() {

    @Inject lateinit var user: User//remove later
    @Inject lateinit var preferences: SharedPreferences

    val firebaseUser: LiveData<FirebaseUser>
    private val mutableUserCondition = MutableLiveData<Boolean>()

    init {
        mutableUserCondition.value = false
        App.appComponent.inject(this)
        firebaseUser = user.getCurrentUser()
    }

    fun logOut() {
        user.logOut()
    }
}