package com.raaf.android.searchmovie.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.*
import com.raaf.android.searchmovie.backgroundJob.workers.RefreshDBWorker
import java.util.*
import java.util.concurrent.TimeUnit

private const val TAG = "WorkManagerStartR"
private const val TAG_WORKER = "RefreshDBWorker"

class WorkManagerStartReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p0 != null) {
            try {
                startRefreshDBWorker(p0.applicationContext)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to plain work", e)
            }
        }
    }

    private fun startRefreshDBWorker(context: Context) {
        val calendar = Calendar.getInstance()
//      Указываем нужное время для запуска worker
        calendar.set(Calendar.HOUR_OF_DAY, 21)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
//      Если указанное раннее время уже прошло не прошло в момент выполнения кода, то прибавляем к указанному выше времени 24 часа
        if (calendar.before(Calendar.getInstance())) {
            Log.e(TAG, "day was added")
            calendar.add(Calendar.HOUR_OF_DAY, 24)
        } else Log.e(TAG, "day wasnt added")
//      Находим разницу разницу между нужным и текущим времени
        val timeDifference = calendar.timeInMillis - Calendar.getInstance().timeInMillis
        Log.e(TAG, "diffTime::$timeDifference")
//      Создаем критерии запуска
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_ROAMING)
            .build()
        val workRequest = PeriodicWorkRequestBuilder<RefreshDBWorker>(24, TimeUnit.HOURS, 21, TimeUnit.HOURS)
            .setInitialDelay(timeDifference, TimeUnit.MILLISECONDS)//Добавляем отложенное время для ввыполнения
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(TAG_WORKER, ExistingPeriodicWorkPolicy.REPLACE, workRequest)
    }
}