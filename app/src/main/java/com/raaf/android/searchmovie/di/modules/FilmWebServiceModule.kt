package com.raaf.android.searchmovie.di.modules

import com.raaf.android.searchmovie.api.FilmWebService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FilmWebServiceModule {

    @Provides
    @Singleton
    fun provideWebService() : FilmWebService {
        return FilmWebService.create()
    }
}