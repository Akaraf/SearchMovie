package com.raaf.android.searchmovie.di.modules

import android.content.Context
import com.raaf.android.searchmovie.api.FilmFetcher
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideContext() : Context {
        return this.context
    }
}