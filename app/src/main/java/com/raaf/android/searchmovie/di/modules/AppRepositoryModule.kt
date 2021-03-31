package com.raaf.android.searchmovie.di.modules

import android.content.Context
import com.raaf.android.searchmovie.api.FilmFetcher
import com.raaf.android.searchmovie.database.CompilationDatabase
import com.raaf.android.searchmovie.database.MyFilmsDatabase
import com.raaf.android.searchmovie.database.top.TopDatabase
import com.raaf.android.searchmovie.repository.AppConverter
import com.raaf.android.searchmovie.repository.AppRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = arrayOf(DatabaseModule::class, FilmFetcherModule::class, AppModule::class))
class AppRepositoryModule {

    @Provides
    @Singleton
    fun  provideAppRepository(context: Context,
                              topDatabase: TopDatabase,
                              myFilmsDatabase: MyFilmsDatabase,
                              compilationDatabase: CompilationDatabase,
                              appConverter: AppConverter,
                              filmFetcher: FilmFetcher) : AppRepository {
        return AppRepository(context, topDatabase, myFilmsDatabase, compilationDatabase, appConverter, filmFetcher)    }
}