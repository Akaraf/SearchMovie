package com.raaf.android.searchmovie.ui.profile

import androidx.lifecycle.ViewModel
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.repository.User
import javax.inject.Inject

class AuthViewModel : ViewModel() {

    @Inject lateinit var user: User

    init {
        App.appComponent.inject(this)
    }
    fun logIn(email: String, password: String, fragment: AuthFragment) {
        user.signIn(email, password, fragment)
    }

    fun createAccount(email: String, password: String, fragment: AuthFragment) : String {
        return user.createAccount(email, password, fragment)
    }
}