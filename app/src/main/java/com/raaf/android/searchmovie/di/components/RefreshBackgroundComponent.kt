package com.raaf.android.searchmovie.di.components

import android.app.Application
import com.raaf.android.searchmovie.backgroundJob.jobServices.RefreshDBJobService
import com.raaf.android.searchmovie.di.modules.*
import com.raaf.android.searchmovie.backgroundJob.workers.RefreshDBWorker
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton


@Component(modules = [AndroidInjectionModule::class, FilmFetcherModule::class, DatabaseModule::class, AppConverterModule::class, RefreshDBModule::class])
@Singleton
interface RefreshBackgroundComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): RefreshBackgroundComponent
    }

    fun inject(refreshDBJS: RefreshDBJobService)
    fun inject(refreshDBWorker: RefreshDBWorker)
}