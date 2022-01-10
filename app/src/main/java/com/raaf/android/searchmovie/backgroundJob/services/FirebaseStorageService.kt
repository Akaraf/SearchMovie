package com.raaf.android.searchmovie.backgroundJob.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.raaf.android.searchmovie.App
import com.raaf.android.searchmovie.dataModel.Movie
import com.raaf.android.searchmovie.dataModel.rootJSON.PersonResponse
import com.raaf.android.searchmovie.repository.FilmRepo
import java.util.concurrent.Executors
import javax.inject.Inject

//Данная служба будет запускаться из application в начале работы приложения. Предназначена для отправки и получения данных Firebase storage

private const val TAG = "FirebaseStorageService"
private const val TYPE_COLLECTION = "type"
private const val WATCH_LATER_COLLECTION = "watch"
private const val FAVORITE_COLLECTION = "favorites"
private const val MY_PERSON_COLLECTION = "persons"
private const val ADD_WATCH_LATER_COLLECTION = "AWLC"
private const val ADD_FAVORITE_COLLECTION = "AFC"
private const val ADD_MY_PERSON_COLLECTION = "AMPC"
private const val DELETE_WATCH_LATER_COLLECTION = "DW"
private const val DELETE_FAVORITE_COLLECTION = "DF"
private const val DELETE_MY_PERSON_COLLECTION = "DMP"
private const val CLEAR_USER_DATA = "CUD"
private const val SYNCHRONIZE_DATA_ACCOUNT = "SynchronizeDataAccount"
private const val ITEMS_COLLECTION = "items"
private const val EXTRA_MOVIE = "movie"
private const val EXTRA_PERSON = "person"
private const val ROOT_COLLECTION = "root collection"
private const val USERS = "users"
private const val EXTRA_ITEM_ID = "itemId"

class FirebaseStorageService : Service() {

    @Inject lateinit var filmRepo: FilmRepo
    @Inject lateinit var firestore: FirebaseFirestore
    @Inject lateinit var auth: FirebaseAuth
    private var movie: Movie? = null
    private var person: PersonResponse? = null
    private var itemId: String? = null

    private val executor = Executors.newSingleThreadExecutor()

    init {
        App.appComponent.inject(this)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        var currentFlag = ""
        if (intent == null) Log.e(TAG, "intent is null")
        //Log.e(TAG, "uid:::${auth.currentUser.uid}")
        if (intent != null && auth.currentUser != null) {
            currentFlag = intent.getStringExtra(TYPE_COLLECTION)!!
            when {
                currentFlag.contains(ADD_WATCH_LATER_COLLECTION) -> {
                    movie = intent.getParcelableExtra<Movie>(EXTRA_MOVIE)
                    movie?.let { sendWatchLaterMovieToStorage(it) }
                }
                currentFlag.contains(ADD_FAVORITE_COLLECTION) -> {
                    movie = intent.getParcelableExtra<Movie>(EXTRA_MOVIE)
                    movie?.let { sendFavoriteMovieToStorage(it) }
                }
                currentFlag.contains(ADD_MY_PERSON_COLLECTION) -> {
                    person = intent.getParcelableExtra<PersonResponse>(EXTRA_PERSON)
                    person?.let { sendMyPersonToStorage(it) }
                }
                currentFlag.contains(DELETE_WATCH_LATER_COLLECTION) -> {
                    itemId = intent.getStringExtra(EXTRA_ITEM_ID)
                    itemId?.let { deleteWatchLaterMovieFromStorage(it) }
                }
                currentFlag.contains(DELETE_FAVORITE_COLLECTION) -> {
                    itemId = intent.getStringExtra(EXTRA_ITEM_ID)
                    //Log.e(TAG, itemId.toString())
                    itemId?.let { deleteFavoriteMovieFromStorage(it) }
                }
                currentFlag.contains(DELETE_MY_PERSON_COLLECTION) -> {
                    itemId = intent.getStringExtra(EXTRA_ITEM_ID)
                    itemId?.let { deleteMyPersonFromStorage(it) }
                }
                currentFlag.contains(CLEAR_USER_DATA) -> {
                    clearAllUserData()
                }
                currentFlag.contains(SYNCHRONIZE_DATA_ACCOUNT) -> {
                    synchronizeUserDataWithApp()
                }
                else -> {
                    Log.e(TAG, " Error:::type:::$$currentFlag")
                }
            }
        }
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        Log.e(TAG, "Service is destroyed")
        super.onDestroy()
    }

    private fun sendMyPersonToStorage(person: PersonResponse) {
        //Log.e(TAG, "sendMyPersonToStorage is started")
        firestore.collection(ROOT_COLLECTION).document(MY_PERSON_COLLECTION).collection(USERS).document(auth.currentUser.uid).collection(
            ITEMS_COLLECTION
        ).document(person.personId.toString()).set(person)
            .addOnFailureListener { Log.e(TAG, "${it.localizedMessage}") }
    }

    private fun sendWatchLaterMovieToStorage(movie: Movie) {
        //Log.e(TAG, "WL:::$movie")
        //Log.e(TAG, "uid:::${auth.currentUser.uid}")
        firestore.collection(ROOT_COLLECTION).document(WATCH_LATER_COLLECTION).collection(USERS).document(auth.currentUser.uid).collection(
            ITEMS_COLLECTION
        ).document(movie.filmId.toString()).set(movie)
            .addOnFailureListener { Log.e(TAG, "${it.localizedMessage}") }
    }

    private fun sendFavoriteMovieToStorage(movie: Movie) {
        //Log.e(TAG, "F:::$movie")
        //Log.e(TAG, "uid:::${auth.currentUser.uid}")
        firestore.collection(ROOT_COLLECTION).document(FAVORITE_COLLECTION).collection(USERS).document(auth.currentUser.uid).collection(
            ITEMS_COLLECTION
        ).document(movie.filmId.toString()).set(movie)
            .addOnFailureListener { Log.e(TAG, "${it.localizedMessage}") }
    }

    private fun deleteMyPersonFromStorage(personId: String) {
        Log.e(TAG, "deleteMP")
        Log.e(TAG, personId)
        firestore.collection(ROOT_COLLECTION).document(MY_PERSON_COLLECTION).collection(USERS).document(auth.currentUser.uid).collection(
            ITEMS_COLLECTION
        ).document(personId).delete()
            .addOnFailureListener { Log.e(TAG, "${it.localizedMessage}") }
    }

    private fun deleteWatchLaterMovieFromStorage(movieId: String) {
        Log.e(TAG, "deleteWL")
        Log.e(TAG, movieId)
        firestore.collection(ROOT_COLLECTION).document(WATCH_LATER_COLLECTION).collection(USERS).document(auth.currentUser.uid).collection(
            ITEMS_COLLECTION
        ).document(movieId).delete()
            .addOnFailureListener { Log.e(TAG, "${it.localizedMessage}") }
    }

    private fun deleteFavoriteMovieFromStorage(movieId: String) {
        Log.e(TAG, "delFM")
        Log.e(TAG, movieId)
        firestore.collection(ROOT_COLLECTION).document(FAVORITE_COLLECTION).collection(USERS).document(auth.currentUser.uid).collection(
            ITEMS_COLLECTION
        ).document(movieId).delete()
            .addOnFailureListener { Log.e(TAG, "${it.localizedMessage}") }
    }

    private fun synchronizeUserDataWithApp() {
        executor.execute {
            val watchParentName = filmRepo.getWatchParentName()

            val watchFromDB = mutableListOf<Movie>()
            val favoritesFromDB = mutableListOf<Movie>()
            val personsFromDB = filmRepo.myPersonsDao.getAll()

            val personsFromStorage = getAllPersonsFromStorage(false) as MutableList<PersonResponse>
            val favoriteFromStorage = getAllFavoriteFilmsFromStorage(false) as MutableList<Movie>
            val watchFromStorage = getAllWatchFilmsFromStorage(false) as MutableList<Movie>

            for (movie in filmRepo.myFilmsDao.getAll()) {
                if (movie.parent.contains(watchParentName)) watchFromDB.add(movie)
                else favoritesFromDB.add(movie)
            }

            Handler(Looper.getMainLooper()).postDelayed({
                personsFromDB.forEach { sendMyPersonToStorage(it) }
            }, 1500)
            Handler(Looper.getMainLooper()).postDelayed({
                watchFromDB.forEach { sendWatchLaterMovieToStorage(it) }
            }, 2000)
            Handler(Looper.getMainLooper()).postDelayed({
                favoritesFromDB.forEach { sendFavoriteMovieToStorage(it) }
            }, 2100)


            Handler(Looper.getMainLooper()).postDelayed({
                executor.execute {
                    addPersonsToAppDB(personsFromStorage)
                    addFavoriteMoviesToAppDB(favoriteFromStorage)
                    addWatchMoviesToAppDB(watchFromStorage)
                }
            }, 2200)
        }
    }

    private fun addPersonsToAppDB(persons: MutableList<PersonResponse>) {
        filmRepo.myPersonsDao.insert(persons)
    }

    private fun addWatchMoviesToAppDB(movies: MutableList<Movie>) {
        filmRepo.myFilmsDao.insert(movies)
    }

    private fun addFavoriteMoviesToAppDB(movies: MutableList<Movie>) {
        filmRepo.myFilmsDao.insert(movies)
    }

    //Может возникнуть траблы с пустыми ретернами в гетах. По возможности стоит уубрать хэндлеры из clearAllUserData

    private fun getAllPersonsFromStorage(onlyIds: Boolean) : MutableList<out Any> {
        val items = mutableListOf<PersonResponse>()
        val itemIds = mutableListOf<String>()
        firestore.collection(ROOT_COLLECTION).document(MY_PERSON_COLLECTION).collection(USERS).document(auth.currentUser.uid).collection(
            ITEMS_COLLECTION
        ).get().addOnSuccessListener { result ->
            for (count in result) {
                if (onlyIds) itemIds.add(count.id)
                else items.add(count.toObject(PersonResponse::class.java))
            }
        }
        return if (onlyIds) itemIds
        else items
    }

    private fun getAllFavoriteFilmsFromStorage(onlyIds: Boolean) : MutableList<out Any> {
        val items = mutableListOf<Movie>()
        val itemIds = mutableListOf<String>()
        firestore.collection(ROOT_COLLECTION).document(FAVORITE_COLLECTION).collection(USERS).document(auth.currentUser.uid).collection(
            ITEMS_COLLECTION
        ).get().addOnSuccessListener { result ->
            for (count in result) {
                if (onlyIds) itemIds.add(count.id)
                else items.add(count.toObject(Movie::class.java))
            }
        }
        return if (onlyIds) itemIds
        else items
    }

    private fun getAllWatchFilmsFromStorage(onlyIds: Boolean) : MutableList<out Any> {
        val items = mutableListOf<Movie>()
        val itemIds = mutableListOf<String>()
        firestore.collection(ROOT_COLLECTION).document(WATCH_LATER_COLLECTION).collection(USERS).document(auth.currentUser.uid).collection(
            ITEMS_COLLECTION
        ).get().addOnSuccessListener { result ->
            for (count in result) {
                if (onlyIds) itemIds.add(count.id)
                else items.add(count.toObject(Movie::class.java))
            }
        }
        return if (onlyIds) itemIds
        else items
    }

    private fun clearAllUserData() {
        executor.execute {
            val favoritesItemIds = getAllFavoriteFilmsFromStorage(true) as MutableList<String>
            val watchItemIds = getAllWatchFilmsFromStorage(true) as MutableList<String>
            val personsItemIds = getAllPersonsFromStorage(true) as MutableList<String>
            Log.e(TAG, "clearUserData")
            Log.e(TAG, favoritesItemIds.toString())

            Handler(Looper.getMainLooper()).postDelayed({
                Log.e(TAG, "fav:::${favoritesItemIds.size}")
                for (count in favoritesItemIds) {
                    deleteFavoriteMovieFromStorage(count)
                }
            }, 1800)

            Handler(Looper.getMainLooper()).postDelayed({
                Log.e(TAG, "wat:::${watchItemIds.size}")
                for (count in watchItemIds) {
                    deleteWatchLaterMovieFromStorage(count)
                }
            }, 1900)

            Handler(Looper.getMainLooper()).postDelayed({
                Log.e(TAG, "per:::${personsItemIds.size}")
                for (count in personsItemIds) {
                    deleteMyPersonFromStorage(count)
                }
            }, 2000)
        }
    }
}