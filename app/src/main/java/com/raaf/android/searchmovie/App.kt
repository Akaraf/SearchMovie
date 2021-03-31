package com.raaf.android.searchmovie

import android.app.Application
import com.raaf.android.searchmovie.di.components.AppComponent
import com.raaf.android.searchmovie.di.components.DaggerAppComponent
import com.raaf.android.searchmovie.di.modules.AppModule

class App : Application() {

    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }
}