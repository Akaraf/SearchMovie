package com.raaf.android.searchmovie.backgroundJob.workers

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.backgroundJob.RefreshDB
import javax.inject.Inject

//Целесообразнее было бы использование JobScheduler вместо WorkManager(так как min sdk version=24).
//Но практические навыки по этой api будут для меня более ценными, да и jobIntentService помечен как deprecated.
//Используется для ежедневного заполнения баз данных приложения актуальным контентом и отправки уведомления пользователю

private const val TAG = "RefreshDBWorker"
private const val NOTIFICATION_ID = 122
private const val CHANNEL_ID = "88811"
private const val PREF_APP_IS_RUNNING = "RunningFlag"
private const val PREF_JOB_IS_DONE = "JobFlag"

class RefreshDBWorker (appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    @Inject lateinit var refreshDB: RefreshDB
    private lateinit var refreshJobPreferences: SharedPreferences
    var flag = 0
//    lateinit var backgroundComponent: BackgroundWorkComponent

    override fun doWork(): Result {
        Log.e(TAG, "$TAG is started")
        refreshJobPreferences = applicationContext.getSharedPreferences("REFRESH_JOB_PREFERENCES",0)
        App.newRefreshBackgroundComponent.inject(this)
        return try {
            flag = checkAppIsStarting()
            if (flag != 3) {
                refreshDB.startWorker()
                refreshJobPreferences.edit().putInt(PREF_JOB_IS_DONE, 2).apply()
                Log.e(TAG, "App not running")
                Result.success()
            } else {
                Log.e(TAG, "App is running")
                refreshJobPreferences.edit().putInt(PREF_JOB_IS_DONE, 1).apply()
                Result.failure()
            }
        } catch (e:Exception){
            Log.e(TAG, "failed work", e)
            refreshJobPreferences.edit().putInt(PREF_JOB_IS_DONE, 1).apply()
            Result.failure()
        }
    }

    private fun checkAppIsStarting() : Int {
        Log.e(TAG, "CAIS")
        val refreshJobPreferences = applicationContext.getSharedPreferences("REFRESH_JOB_PREFERENCES",0)
        return if (refreshJobPreferences.contains(PREF_APP_IS_RUNNING)) refreshJobPreferences.getInt(PREF_APP_IS_RUNNING, 1)
        else 2
    }
    //3- app is started, 4 - not
}