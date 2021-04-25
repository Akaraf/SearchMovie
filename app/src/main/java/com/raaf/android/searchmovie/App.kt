package com.raaf.android.searchmovie

import android.app.Application
import com.raaf.android.searchmovie.di.components.AppComponent
import com.raaf.android.searchmovie.di.components.DaggerAppComponent
import com.raaf.android.searchmovie.di.modules.AppModule
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.calligraphy3.FontMapper
import io.github.inflationx.viewpump.ViewPump

class App : Application() {

    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
//        setTheme(R.style.Theme_SearchMovie)
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()

        ViewPump.init(ViewPump.builder()
                .addInterceptor(CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/regular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build())
    }
}