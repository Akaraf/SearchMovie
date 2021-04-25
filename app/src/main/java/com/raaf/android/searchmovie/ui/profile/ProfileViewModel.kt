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

    val firebaseUser: LiveData<MutableMap<String,String>>
    private val mutableUserCondition = MutableLiveData<Boolean>()

    init {
        mutableUserCondition.value = false
        App.appComponent.inject(this)
        firebaseUser = Transformations.switchMap(mutableUserCondition) {
            user.getUser()
        }
    }

    fun getUser() {
        Log.e(TAG, "fun getUser is run:")
        var boolean = mutableUserCondition.value
        if (boolean == null) boolean = false
        mutableUserCondition.value != boolean
        Log.e(TAG, "boolean is $boolean,    condition is    ${mutableUserCondition.value}")
    }

    fun logOut() {
        user.logOut()
    }
}