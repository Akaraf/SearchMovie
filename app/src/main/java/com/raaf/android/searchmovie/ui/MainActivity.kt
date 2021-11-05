package com.raaf.android.searchmovie.ui

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.backgroundJob.jobServices.RefreshDBJobService
import com.raaf.android.searchmovie.backgroundJob.services.RefreshDBService
import com.raaf.android.searchmovie.backgroundJob.workers.RefreshDBWorker
import com.raaf.android.searchmovie.ui.film.OnBackPressedListener
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import java.util.*
import java.util.concurrent.TimeUnit

private const val TAG = "MainActivity"
private const val TAG_WORKER = "RefreshDBWorker"
private const val EXTRA_FIRST_START = "firstStart"
private const val PREF_INSTALLATION_TIME = "InstallationTime"
private const val PREF_JOB_IS_DONE = "JobFlag"

class MainActivity : AppCompatActivity() {
    lateinit var mainViewModel: MainViewModel
    lateinit var toolbar: Toolbar
    var onBackPressedListener: OnBackPressedListener? = null
    lateinit var mainNavController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SearchMovie)
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mainViewModel.setValueForRefreshJob(3, this)
        if (!hasConnection(this)) Toast.makeText(baseContext, R.string.problem_connection, Toast.LENGTH_LONG).show()
        else if (!App.isFirstStartApp) {
                Log.e(TAG, "NFS")
                val refreshIsDoneFlag = App.refreshPreferences.getInt(PREF_JOB_IS_DONE, 0)
                Log.e(TAG, "RIDF=$refreshIsDoneFlag")
                if (refreshIsDoneFlag == 0) {
                    Log.e(TAG, "back job is not done yet")
                    Log.e(TAG, "start Service for update ui")
                    startRefreshDBService()
                    if (getPrefInstallationDate(App.refreshPreferences) < Calendar.getInstance().timeInMillis){
                        Log.e(TAG, "china shit")
                        Toast.makeText(this, this.getString(R.string.allow_autorun), Toast.LENGTH_LONG).show()
                    }
            }
        }
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        mainNavController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home, R.id.navigation_search, R.id.navigation_profile))
        setupActionBarWithNavController(mainNavController, appBarConfiguration)
        navView.setupWithNavController(mainNavController)
        toolbar.setNavigationOnClickListener {
            if (onBackPressedListener != null && onBackPressedListener!!.getFlag()) onBackPressedListener!!.onBackPressedL()
            else mainNavController.popBackStack()
        }
        startRefreshDBWorker()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onBackPressed() {
        if (onBackPressedListener != null && onBackPressedListener!!.getFlag()) {
            onBackPressedListener!!.onBackPressedL()
        } else super.onBackPressed()
    }

    override fun onStop() {
        Log.e(TAG, "onStop")
        mainViewModel.setValueForRefreshJob(4, this)
        super.onStop()
    }

    override fun onStart() {
        Log.e(TAG, "onStart")
        mainViewModel.setValueForRefreshJob(3, this)
        super.onStart()
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        mainViewModel.setValueForRefreshJob(4, this)
        super.onDestroy()
    }

    private fun startRefreshDBService() {
        val intent = Intent(this, RefreshDBService::class.java)
        intent.putExtra(EXTRA_FIRST_START, true)
        this.startService(intent)
    }

    private fun getPrefInstallationDate(sp: SharedPreferences) : Long {
        var prefTime = sp.getLong(PREF_INSTALLATION_TIME, 0L)
        if (prefTime != 0L) {
            prefTime += 48 * 60 * 60 * 1000
        }
        return prefTime
    }

    fun startRefreshDBJobService() {
        Log.e(TAG, "startRefreshDBJobService")
        val js = ComponentName(this, RefreshDBJobService::class.java)
        val exerciseJobBuilder = JobInfo.Builder(84, js)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NOT_ROAMING)
            .setPeriodic(TimeUnit.MINUTES.toMillis(16))
        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val job = jobScheduler.schedule(exerciseJobBuilder.build())
        if (job == JobScheduler.RESULT_SUCCESS) Log.e(TAG, "Success")
        if (job == JobScheduler.RESULT_FAILURE) Log.e(TAG, "Failed")
    }

    private fun startRefreshDBWorker() {
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
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(TAG_WORKER, ExistingPeriodicWorkPolicy.REPLACE, workRequest)
    }

    fun getBackPressedListener(listener: OnBackPressedListener?) {
        if (listener == null) Log.e(TAG, "MA:  listener == null")
        else Log.e(TAG, "MA:  listener != null")
        onBackPressedListener = listener
    }

    //Checking internet connection
    fun hasConnection(context: Context) : Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = cm.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }
}