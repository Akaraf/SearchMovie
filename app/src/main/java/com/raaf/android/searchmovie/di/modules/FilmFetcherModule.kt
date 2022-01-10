package com.raaf.android.searchmovie.di.modules

import android.app.Application
import android.content.Context
import com.raaf.android.searchmovie.api.FilmWebService
import com.raaf.android.searchmovie.database.*
import com.raaf.android.searchmovie.database.cacheDB.CategoryFilmsCacheDatabase
import com.raaf.android.searchmovie.database.cacheDB.MoviesForPersonCacheDatabase
import com.raaf.android.searchmovie.database.top.ReleasesDatabase
import com.raaf.android.searchmovie.database.top.TopDatabase
import com.raaf.android.searchmovie.repository.AppConverter
import com.raaf.android.searchmovie.repository.FilmRepo
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = arrayOf(DatabaseModule::class, AppConverterModule::class, FilmWebServiceModule::class))
class FilmFetcherModule {

    @Provides
    @Singleton
    fun provideFilmRepo(webService: FilmWebService,topDatabase: TopDatabase, myFilmsDatabase: MyFilmsDatabase, compilationDatabase: CompilationDatabase, watchedDatabase: WatchedDatabase, releasesDatabase: ReleasesDatabase, popularPersonDatabase: PopularPersonDatabase, myPersonsDatabase: MyPersonsDatabase, moviesForPersonCacheDatabase: MoviesForPersonCacheDatabase, categoryDatabase: CategoryDatabase, categoryFilmsCacheDatabase: CategoryFilmsCacheDatabase, appConverter: AppConverter, app: Application) : FilmRepo {
        return FilmRepo(webService, topDatabase, myFilmsDatabase, compilationDatabase, watchedDatabase, releasesDatabase, popularPersonDatabase, myPersonsDatabase, moviesForPersonCacheDatabase, categoryDatabase, categoryFilmsCacheDatabase, appConverter, app.applicationContext)
    }
}