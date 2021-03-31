package com.raaf.android.searchmovie.di.modules

import com.raaf.android.searchmovie.api.FilmFetcher
import com.raaf.android.searchmovie.database.CompilationDatabase
import com.raaf.android.searchmovie.database.MyFilmsDatabase
import com.raaf.android.searchmovie.database.top.TopDatabase
import com.raaf.android.searchmovie.repository.AppConverter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = arrayOf(DatabaseModule::class, AppConverterModule::class))
class FilmFetcherModule {

    @Provides
    @Singleton
    fun provideFilmFetcher(topDatabase: TopDatabase, myFilmsDatabase: MyFilmsDatabase, compilationDatabase: CompilationDatabase, appConverter: AppConverter) : FilmFetcher {
        return FilmFetcher(topDatabase, myFilmsDatabase, compilationDatabase, appConverter)
    }

    /*@Provides
    @Singleton
    fun provideFilmApi() : FilmApi {

    }*/
}