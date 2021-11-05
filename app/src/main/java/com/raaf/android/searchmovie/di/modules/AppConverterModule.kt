package com.raaf.android.searchmovie.di.modules

import android.app.Application
import android.content.Context
import com.raaf.android.searchmovie.repository.AppConverter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppConverterModule {

    @Provides
    @Singleton
    fun provideAppConverter(app: Application) : AppConverter {
        return AppConverter(app.applicationContext)
    }
}