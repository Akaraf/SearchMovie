package com.raaf.android.searchmovie.di.modules

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.raaf.android.searchmovie.repository.User
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = arrayOf(FirebaseModule::class))
class UserModule {

    @Provides
    @Singleton
    fun provideUser(auth: FirebaseAuth) : User {
        return User(auth)
    }
}