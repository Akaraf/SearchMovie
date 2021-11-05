package com.raaf.android.searchmovie.backgroundJob.jobServices

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.raaf.android.searchmovie.backgroundJob.RefreshDB
import com.raaf.android.searchmovie.di.components.DaggerRefreshBackgroundComponent
import javax.inject.Inject

private const val TAG = "refreshDBJS"
private const val NOTIFICATION_ID = 122
private const val CHANNEL_ID = "88811"
private const val PREF_APP_IS_RUNNING = "RunningFlag"
private const val PREF_JOB_IS_DONE = "JobFlag"

class RefreshDBJobService : JobService() {

    @Inject lateinit var refreshDB: RefreshDB
    private lateinit var refreshJobPreferences: SharedPreferences
    var flag = 0

    override fun onStartJob(p0: JobParameters?): Boolean {
            Log.e(TAG, "onStartJob is started")
            refreshJobPreferences = application.applicationContext.getSharedPreferences("REFRESH_JOB_PREFERENCES",0)
            DaggerRefreshBackgroundComponent.builder().application(application).build().inject(this)
            flag = checkAppIsStarting()
            if (flag != 3) {
                refreshDB.startWorker()
                refreshJobPreferences.edit().putInt(PREF_JOB_IS_DONE, 2).apply()
                Log.e(TAG, "App not running")
            } else {
                Log.e(TAG, "App is running")
                refreshJobPreferences.edit().putInt(PREF_JOB_IS_DONE, 1).apply()
            }
            Handler(Looper.getMainLooper()).postDelayed({
                Log.e(TAG, "job start finished")
                this.jobFinished(p0, false)
            }, 50000)
        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return false
    }

    private fun checkAppIsStarting() : Int {
        Log.e(TAG, "CAIS")
        return if (refreshJobPreferences.contains(PREF_APP_IS_RUNNING)) refreshJobPreferences.getInt(PREF_APP_IS_RUNNING, 1)
        else 2
    }
    //3- app is started, 4 - not
}