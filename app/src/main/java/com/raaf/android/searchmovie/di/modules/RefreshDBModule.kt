package com.raaf.android.searchmovie.di.modules

import android.app.Application
import android.content.Context
import com.raaf.android.searchmovie.api.FilmFetcher
import com.raaf.android.searchmovie.backgroundJob.RefreshDB
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = arrayOf(FilmFetcherModule::class))
class RefreshDBModule {

    @Provides
    @Singleton
    fun provideRefreshDB(app: Application, filmFetcher: FilmFetcher) : RefreshDB {
        return RefreshDB(app.applicationContext, filmFetcher)
    }
}