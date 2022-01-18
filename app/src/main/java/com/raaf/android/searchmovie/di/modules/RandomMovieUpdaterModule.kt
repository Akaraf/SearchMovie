package com.raaf.android.searchmovie.di.modules

import com.raaf.android.searchmovie.repository.FilmRepo
import com.raaf.android.searchmovie.repository.RandomMovieUpdater
import dagger.Module
import dagger.Provides

@Module(includes = arrayOf(FilmFetcherModule::class))
class RandomMovieUpdaterModule {

    @Provides
    fun provideRandomMovieUpdater(filmRepo: FilmRepo) : RandomMovieUpdater {
        return RandomMovieUpdater(filmRepo)
    }
}