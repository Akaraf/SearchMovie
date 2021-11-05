package com.raaf.android.searchmovie

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.*
import android.util.Log
import com.raaf.android.searchmovie.di.components.RefreshBackgroundComponent
import com.raaf.android.searchmovie.backgroundJob.services.RefreshDBService
import com.raaf.android.searchmovie.di.components.DaggerRefreshBackgroundComponent
import com.raaf.android.searchmovie.di.components.DaggerAppComponent
import com.raaf.android.searchmovie.di.components.AppComponent
import com.raaf.android.searchmovie.di.modules.AppModule
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import java.util.*
import javax.inject.Inject

private const val TAG = "App"
private const val CHANNEL_ID = "88811"
private const val EXTRA_FIRST_START = "firstStart"
private const val PREF_INSTALLATION_TIME = "InstallationTime"

class App : Application() {

    @Inject lateinit var settings: Settings
//    private var executor = Executors.newSingleThreadExecutor()

    companion object {
        lateinit var appComponent: AppComponent
        lateinit var newRefreshBackgroundComponent: RefreshBackgroundComponent
        var isFirstStartApp: Boolean = true
        lateinit var refreshPreferences: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
//        Setting up DI
        daggerComponentsBuilding()
//      Setting up logo theme and notification channel
        settings.setThemeFromPreference()
        createNotificationChannel()
//        Setting up background job
        isFirstStartApp = settings.checkFirstStartValue()
        refreshPreferences = getSharedPreferences("REFRESH_JOB_PREFERENCES",0)
        if (isFirstStartApp) {
            setPrefInstallationDate(refreshPreferences)
            startRefreshDBService()
            settings.firstStartIsCompleted()
        }
//        Set fonts
        viewPumpBuilding()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        daggerComponentsBuilding()
        viewPumpBuilding()
    }


    private fun startRefreshDBService() {
        val intent = Intent(this, RefreshDBService::class.java)
        intent.putExtra(EXTRA_FIRST_START, true)
        this.startService(intent)
    }

    private fun daggerComponentsBuilding() {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
        appComponent.inject(this)
        newRefreshBackgroundComponent = DaggerRefreshBackgroundComponent.builder()
            .application(this)
            .build()
    }

    private fun viewPumpBuilding() {
        ViewPump.init(ViewPump.builder()
            .addInterceptor(CalligraphyInterceptor(
                CalligraphyConfig.Builder()
                    .setDefaultFontPath("fonts/regular.ttf")
                    .setFontAttrId(R.attr.fontPath)
                    .build()))
            .build())
    }

    private fun setPrefInstallationDate(sp: SharedPreferences) {
        sp.edit().putLong(PREF_INSTALLATION_TIME, Calendar.getInstance().timeInMillis).apply()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}