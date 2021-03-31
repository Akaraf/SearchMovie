package com.raaf.android.searchmovie.ui

import android.os.Bundle
import android.view.View.GONE
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.repository.AppRepository
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    val dbTopName = "topDatabase"
    @Inject lateinit var appRepository: AppRepository
    val typeTop = arrayOf("TOP_250_BEST_FILMS", "TOP_100_POPULAR_FILMS", "TOP_AWAIT_FILMS")

    lateinit var nameTop: List<String>
    lateinit var toolbar: Toolbar

    init {
        App.appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SearchMovie)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        nameTop = listOf(getString(R.string.best), getString(R.string.popular), getString(R.string.await))

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home, R.id.navigation_media, R.id.navigation_search, R.id.navigation_profile))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

//        startDB(dbTopName, appRepository)
        fillingTopDb(typeTop[0], nameTop[0])
        fillingTopDb(typeTop[1], nameTop[1])
        fillingTopDb(typeTop[2], nameTop[2])

        appRepository.getCompilation()
    }

    private fun fillingTopDb(type: String, name: String) {
        var page = appRepository.getTop(type, 1, name).value ?: 2
        for (count in 2..page) {
            appRepository.getTop(type, count, name)
        }
    }


    private fun doesDBExist(dbName: String) : Boolean {
        return applicationContext.getDatabasePath(dbName).exists()
    }
}