package com.raaf.android.searchmovie.di.modules

import android.app.Application
import com.raaf.android.searchmovie.backgroundJob.RefreshDB
import com.raaf.android.searchmovie.repository.FilmRepo
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = arrayOf(FilmFetcherModule::class))
class RefreshDBModule {

    @Provides
    @Singleton
    fun provideRefreshDB(app: Application, filmRepository: FilmRepo) : RefreshDB {
        return RefreshDB(app.applicationContext, filmRepository)
    }
}