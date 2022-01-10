package com.raaf.android.searchmovie.backgroundJob.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.backgroundJob.RefreshDB
import com.raaf.android.searchmovie.ui.MainActivity
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject

private const val TAG = "RefreshDBService"

//Используется только при первом запуске приложения для заболнения баз данных приложения. После его выполнения эту функцию всегда выполняет RefreshDBWorker

class RefreshDBService : Service() {

    @Inject lateinit var refreshDB: RefreshDB

    init {
        App.appComponent.inject(this)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "Service is started")
        refreshDB.fillingResources()
        refreshDB.startService()
        Handler(Looper.getMainLooper()).postDelayed({
            this.stopSelf()
        }, 90000)
        return Service.START_STICKY// Служба не будет остановлена, пока мы не остановим ее вручную
    }
}