package com.raaf.android.searchmovie.di.modules

import android.content.Context
import android.content.SharedPreferences
import com.raaf.android.searchmovie.Settings
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SettingsModule {

    @Provides
    @Singleton
    fun provideSettings(settingsPreference: SharedPreferences) : Settings {
        return Settings(settingsPreference)
    }
}