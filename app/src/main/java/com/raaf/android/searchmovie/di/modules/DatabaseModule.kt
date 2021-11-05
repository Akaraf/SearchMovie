package com.raaf.android.searchmovie.di.modules

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.raaf.android.searchmovie.database.*
import com.raaf.android.searchmovie.database.cacheDB.CategoryFilmsCacheDatabase
import com.raaf.android.searchmovie.database.cacheDB.MoviesForPersonCacheDatabase
import com.raaf.android.searchmovie.database.top.ReleasesDatabase
import com.raaf.android.searchmovie.database.top.TopDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideTopDB(app: Application) : TopDatabase {
        return Room.databaseBuilder(app.applicationContext, TopDatabase::class.java, "topDatabase")
                .build()
    }

    @Provides
    @Singleton
    fun provideMyFilmsDB(app: Application) : MyFilmsDatabase {
        return Room.databaseBuilder(app.applicationContext, MyFilmsDatabase::class.java, "myFilmsDatabase")
                .build()
    }

    @Provides
    @Singleton
    fun provideCompilationDB(app: Application) : CompilationDatabase {
        return Room.databaseBuilder(app.applicationContext, CompilationDatabase::class.java, "compilationDatabase")
                .build()
    }

    @Provides
    @Singleton
    fun provideWatchedDB(app: Application) : WatchedDatabase {
        return Room.databaseBuilder(app.applicationContext, WatchedDatabase::class.java, "watchedDatabase")
                .build()
    }

    @Provides
    @Singleton
    fun provideReleasesDB(app: Application) : ReleasesDatabase {
        return Room.databaseBuilder(app.applicationContext, ReleasesDatabase::class.java, "releasesDatabase")
                .build()
    }

    @Provides
    @Singleton
    fun providePopularPersonDB(app: Application) : PopularPersonDatabase {
        return Room.databaseBuilder(app.applicationContext, PopularPersonDatabase::class.java, "popularPersonDatabase")
                .build()
    }

    @Provides
    @Singleton
    fun provideMyPersonsDB(app: Application) : MyPersonsDatabase {
        return Room.databaseBuilder(app.applicationContext, MyPersonsDatabase::class.java, "myPersonsDatabase")
                .build()
    }

    @Provides
    @Singleton
    fun provideMoviesForPersonCacheDB(app: Application) : MoviesForPersonCacheDatabase {
        return Room.databaseBuilder(app.applicationContext, MoviesForPersonCacheDatabase::class.java, "moviesForPersonCacheDatabase")
            .build()
    }

    @Provides
    @Singleton
    fun provideCategoryDB(app: Application) : CategoryDatabase {
        return Room.databaseBuilder(app.applicationContext, CategoryDatabase::class.java, "categoryDatabase")
            .build()
    }

    @Provides
    @Singleton
    fun provideCategoryFilmsCacheDB(app: Application) : CategoryFilmsCacheDatabase {
        return Room.databaseBuilder(app.applicationContext, CategoryFilmsCacheDatabase::class.java, "CategoryFilmsCacheDatabase")
            .build()
    }
}