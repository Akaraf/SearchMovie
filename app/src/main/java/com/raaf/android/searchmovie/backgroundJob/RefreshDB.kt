package com.raaf.android.searchmovie.backgroundJob

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.api.FilmFetcher
import com.raaf.android.searchmovie.ui.MainActivity
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

private const val TAG = "RefreshDB"
private const val CHANNEL_ID = "88811"
private const val NOTIFICATION_ID = 122

class RefreshDB @Inject constructor(val context: Context, val filmFetcher: FilmFetcher) {

    private lateinit var executors: ExecutorService
    private var isExecutorsInit = false
    //private var isFirstStartApp = true

    lateinit var nameTop: List<String>
    val typeTop = arrayOf("TOP_250_BEST_FILMS", "TOP_100_POPULAR_FILMS", "TOP_AWAIT_FILMS")
    var currentNumberMonth = 0
    var currentYear = 0
    val monthNames = mapOf(0 to "JANUARY", 1 to "FEBRUARY", 2 to "MARCH", 3 to "APRIL", 4 to "MAY", 5 to "JUNE", 6 to  "JULY", 7 to "AUGUST", 8 to "SEPTEMBER", 9 to "OCTOBER", 10 to "NOVEMBER", 11 to "DECEMBER")

    val jobHandler = Handler(Looper.getMainLooper())

    fun fillingResources() {
        nameTop = listOf(context.getString(R.string.best), context.getString(R.string.popular), context.getString(R.string.await))
        currentNumberMonth = checkNumberMonth(getDate().second)
        currentYear = getDate().first
    }

    fun startService() {
        if (!isExecutorsInit) {
            executors = Executors.newFixedThreadPool(3)
            isExecutorsInit = true
        }
        Log.e(TAG, "startFirst")
        fillingDBForService()
    }

    fun startWorker() {
        Log.e(TAG, "startWorker")
        if (!isExecutorsInit) {
            executors = Executors.newFixedThreadPool(2)
            isExecutorsInit = true
        }
        fillingResources()
        executors.execute { clearDatabases() }
            jobHandler.postDelayed({
                fillingDB()
            }, 1300)
            jobHandler.postDelayed({
                executors.execute { createNotification() }
            }, 35000)
    }

    private fun fillingDBForService() {
        executors.execute {
            filmFetcher.getCompilation()
        }
        Handler(Looper.getMainLooper()).postDelayed({
            executors.execute {
                fillingTopDb(typeTop[0], nameTop[0])
                fillingTopDb(typeTop[1], nameTop[1])
                fillingTopDb(typeTop[2], nameTop[2])
            }
        }, 2500)

        Handler(Looper.getMainLooper()).postDelayed({
            executors.execute {
                fetchReleases(currentYear, currentNumberMonth)
            }
        }, 6500)

        Handler(Looper.getMainLooper()).postDelayed({
            executors.execute {
                filmFetcher.setPopularPersonsFromMyFilms()
            }
        }, 9000)

        Handler(Looper.getMainLooper()).postDelayed({
            executors.execute {
                filmFetcher.fillingYearItemsForCategoryDb(context.resources.getStringArray(R.array.years).toList(), context.getString(R.string.years))
            }
        }, 11000)
        Handler(Looper.getMainLooper()).postDelayed({
            executors.execute {
                filmFetcher.fillingGenreItemsForCategoryDb(context.resources.getStringArray(R.array.genres).toList(), context.getString(R.string.genre))
            }
        }, 13000)
        Handler(Looper.getMainLooper()).postDelayed({
            executors.execute {
                filmFetcher.fillingTVSeriesItemsForCategoryDb(context.resources.getStringArray(R.array.tv_series).toList(), context.getString(R.string.tv_series))
            }
        }, 15000)
        Handler(Looper.getMainLooper()).postDelayed({
            executors.execute {
                filmFetcher.fillingCountriesCategory(context.resources.getStringArray(R.array.countries).toList(), context.getString(R.string.countries))
            }
        }, 17000)
    }

    private fun fillingDB() {
        Log.e(TAG, "fillingDB")
        executors.execute {
            Log.e(TAG, "1")
            filmFetcher.getCompilation()
        }
        jobHandler.postDelayed({
            executors.execute {
                Log.e(TAG, "2")
                fillingTopDb(typeTop[0], nameTop[0])
            }
        }, 2500)
        jobHandler.postDelayed({
            executors.execute {
                Log.e(TAG, "3")
                fillingTopDb(typeTop[1], nameTop[1])
            }
        }, 5200)

        jobHandler.postDelayed({
            executors.execute {
                Log.e(TAG, "4")
                fillingTopDb(typeTop[2], nameTop[2])
            }
        }, 8500)

        jobHandler.postDelayed({
            executors.execute {
                Log.e(TAG, "5")
                fetchReleases(currentYear, currentNumberMonth)
            }
        }, 12000)

        jobHandler.postDelayed({
            executors.execute {
                Log.e(TAG, "6")
                filmFetcher.setPopularPersonsFromMyFilms()
            }
        }, 15500)

        jobHandler.postDelayed({
            executors.execute {
                Log.e(TAG, "7")
                filmFetcher.fillingYearItemsForCategoryDb(context.resources.getStringArray(R.array.years).toList(), context.getString(R.string.years))
            }
        }, 19000)
        jobHandler.postDelayed({
            executors.execute {
                Log.e(TAG, "8")
                filmFetcher.fillingGenreItemsForCategoryDb(context.resources.getStringArray(R.array.genres).toList(), context.getString(R.string.genre))
            }
        }, 22000)
        jobHandler.postDelayed({
            executors.execute {
                Log.e(TAG, "9")
                filmFetcher.fillingTVSeriesItemsForCategoryDb(context.resources.getStringArray(R.array.tv_series).toList(), context.getString(R.string.tv_series))
            }
        }, 26000)
        jobHandler.postDelayed({
            executors.execute {
                Log.e(TAG, "10")
                filmFetcher.fillingCountriesCategory(context.resources.getStringArray(R.array.countries).toList(), context.getString(R.string.countries))
            }
        }, 30000)
    }

    private fun fetchReleases(year: Int, month: Int) {
        val months = listOf(monthNames[month], monthNames[month+1], monthNames[month+2], monthNames[month+3])
        filmFetcher.getReleases(year, months)
    }

    private fun fillingTopDb(type: String, name: String) {
        for (count in 1..5) {
            filmFetcher.getTop(type, count, name)
        }
    }

    fun clearDatabases() {
        Log.e(TAG, "clearDatabases")
        filmFetcher.clearTopDb()
        filmFetcher.clearCompilationDb()
        filmFetcher.clearReleasesDb()
        filmFetcher.clearPopularPersonDb()
        filmFetcher.clearCategoryDb()
    }


    private fun getDate() : Pair<Int, Int> {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        return Pair(year, month)
    }

    fun checkNumberMonth(number: Int) : Int {
        return if (number == 11) 0 //If month is 11(december) change it to 0(january) because we need months since next month
        else number
    }

    private fun createNotification() {
            Log.e(TAG, "createNotification was started")
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            var builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(context.getString(R.string.title_refresh_notification))
                .setContentText(context.getString(R.string.text_refresh_notificatioin))
                .setColor(context.getColor(R.color.ic_ref_background))
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                notify(NOTIFICATION_ID, builder.build())

        }
    }
}