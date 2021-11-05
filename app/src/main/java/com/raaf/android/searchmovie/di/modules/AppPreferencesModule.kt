package com.raaf.android.searchmovie.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppPreferencesModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(app: Application) : SharedPreferences {
        return app.applicationContext.getSharedPreferences("APP_PREFERENCES", 0/*context.MODE_PRIVATE value*/)
    }
}