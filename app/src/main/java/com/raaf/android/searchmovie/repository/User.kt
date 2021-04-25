package com.raaf.android.searchmovie.repository

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.raaf.android.searchmovie.App
import javax.inject.Inject

private const val TAG = "User"
private const val SUCCESS_FLAG = "Success"
private const val ERROR_FLAG = "Error"
private const val INCORRECT_EMAIL = "Incorrect"
private const val LOGIN_COMPLETED = "Already"
private const val MAP_EMAIL = "email"
private const val MAP_U_NAME = "userName"

class User @Inject constructor(val auth: FirebaseAuth) {

    var currentUser: FirebaseUser? = null

    init {
        App.appComponent.inject(this)
    }

    fun getUser() : LiveData<MutableMap<String,String>> {
        Log.e(TAG, "getUser is run")
        if (currentUser == null) currentUser = auth.currentUser
        val list = mutableMapOf<String, String>()
        list += MAP_EMAIL to currentUser!!.email
        list += MAP_U_NAME to currentUser!!.email.split("@")[0]
        //list += MAP_U_ID to currentUser!!.uid
        //list += MAP_U_ID to currentUser?.displayName
        //list += MAP_U_ID to currentUser?.photoUrl
        //currentUser?.isEmailVerified
        val liveData = MutableLiveData<MutableMap<String,String>>()
        liveData.value = list
        return liveData
    }

    fun signIn(email: String, password: String, fragment: Fragment) : String {
        var result = ""
        if (auth.currentUser != null) {
            //Already signed in
            //Do nothing
            result = LOGIN_COMPLETED
            currentUser = auth.currentUser
        } else {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(fragment.requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    Log.e(TAG, "logIn is success")
                                    result = SUCCESS_FLAG
                                    currentUser = auth.currentUser
                                }
                                else {
                                    Log.e(TAG, "LogIn is error")
                                    result = ERROR_FLAG
                                }
                            }
                        }
        return result
    }

    fun createAccount(email: String, password: String, fragment: Fragment) : String {
        var result = ""
        if (checkEmail(email)){
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(fragment.requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    Log.e(TAG, "Registration is success")
                                    result =  SUCCESS_FLAG
                                    currentUser = auth.currentUser
                                }
                                else {
                                    Log.e(TAG, "Registration is error")
                                    result = ERROR_FLAG
                                }
                            }
        } else result =  INCORRECT_EMAIL
        return result
    }

    fun logOut() {
        auth.signOut()
    }

    private fun checkEmail(email: String) : Boolean {
        return "@" in email && "." in email     //".gmail@com" :)
    }
}