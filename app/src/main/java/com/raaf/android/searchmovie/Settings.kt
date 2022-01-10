package com.raaf.android.searchmovie

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import javax.inject.Inject


private const val PREF_THEME = "ThemePref"
private const val PREF_FIRST_START = "FirstStartPref"
private const val THEME_AUTO = "Auto"
private const val THEME_LIGHT = "Light"
private const val THEME_DARK = "Dark"

private const val TAG = "Settings"

class Settings @Inject constructor(val settingsPreferences: SharedPreferences) {

    private var themeValue = THEME_AUTO

    init {
//        App.appComponent.inject(this)
    }

    fun checkFirstStartValue() : Boolean {
        return if (settingsPreferences.contains(PREF_FIRST_START)) {
            settingsPreferences.getBoolean(PREF_FIRST_START, true)
        } else true
    }

    fun firstStartIsCompleted() {
        settingsPreferences.edit().putBoolean(PREF_FIRST_START, false).apply()
        Handler(Looper.getMainLooper()).postDelayed({
            App.isFirstStartApp = false
        }, 3000)
    }

    fun setThemeFromPreference() {
        if (settingsPreferences.contains(PREF_THEME)) {
            themeValue = settingsPreferences.getString(PREF_THEME, THEME_AUTO) ?: THEME_AUTO
        }

        Log.e(TAG, themeValue)
        when (themeValue) {
            THEME_AUTO -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            THEME_LIGHT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            THEME_DARK -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

    fun changeTheme(themePref: String) {
        settingsPreferences.edit().putString(PREF_THEME, themePref).apply()
        setThemeFromPreference()
    }

    fun getThemeSetting() : String {
        return if (settingsPreferences.contains(PREF_THEME)) {
            settingsPreferences.getString(PREF_THEME, THEME_AUTO) ?: THEME_AUTO
        } else THEME_AUTO
    }
}