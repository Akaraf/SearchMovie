package com.raaf.android.searchmovie.ui.profile

import androidx.lifecycle.ViewModel
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.Settings
import com.raaf.android.searchmovie.repository.FilmRepo
import javax.inject.Inject

class SettingsViewModel : ViewModel() {

    @Inject lateinit var settings: Settings
    @Inject lateinit var repository: FilmRepo

    init {
        App.appComponent.inject(this)
    }

    fun changeTheme(themePref: String) {
        settings.changeTheme(themePref)
    }

    fun checkThemeSetting() : String {
        return settings.getThemeSetting()
    }

    fun clearApp() {
        repository.clearMyFilmsDB()
        repository.clearMyPersonDB()
        repository.clearHistoryDB()
    }
}