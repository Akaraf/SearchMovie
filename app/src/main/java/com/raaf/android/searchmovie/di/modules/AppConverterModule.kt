package com.raaf.android.searchmovie.di.modules

import com.raaf.android.searchmovie.repository.AppConverter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppConverterModule {

    @Provides
    @Singleton
    fun provideAppConverter() : AppConverter {
        return AppConverter()
    }
}