package com.raaf.android.searchmovie.api

import com.google.gson.JsonElement
import com.raaf.android.searchmovie.dataModel.StaffPerson
import com.raaf.android.searchmovie.dataModel.rootJSON.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://kinopoiskapiunofficial.tech/"

interface FilmWebService {

//    Films
//    Поиск фильма по id(1)
    @GET("api/v2.1/films/{id}")
    suspend fun fetchMovie(@Path(value = "id", encoded = true) id: Int,
                   @Query("append_to_response") appendToResponse: Array<String>) : MovieById

    //    Кадры по фильму(2)
    @GET("api/v2.1/films/{id}/frames")
    suspend fun fetchFrames(@Path(value = "id", encoded = true) id: Int) : FramesByFilmId

    //    Трейлеры и тизеры по фильму(3)
    @GET("api/v2.1/films/{id}/videos")
    suspend fun fetchVideos(@Path(value = "id", encoded = true) id: Int) : TrailerResponse

    //    Студии по фильму(4)
    @GET("api/v2.1/films/{id}/studios")
    suspend fun fetchStudios(@Path(value = "id", encoded = true) id: Int) : JsonElement

    //    Сиквелы и приквелы по фильму(5)
    @GET("api/v2.1/films/{id}/sequels_and_prequels")
    suspend fun fetchSequelsAndPrequels(@Path(value = "id", encoded = true) id: Int) : List<SequelsPrequelsResponse>

    //    Спсиок фильмов по фразе(6)
    @GET("api/v2.1/films/search-by-keyword")
    suspend fun fetchSearchByKeyword(@Query("keyword") keyword: String,
                             @Query("page") page: Int) : SearchByKeyword

    //    Фильтры(7)
    @GET("api/v2.1/films/filters")
    suspend fun fetchFilters() : FiltersResponse

    //    Поиск по фильтрам(8)
    @GET("api/v2.1/films/search-by-filters")
    suspend fun fetchSearchByFilters(@Query("country") country: Array<Int>?,
                             @Query("genre") genre: Array<Int>?,
                             @Query("order") order: String,
                             @Query("type") type: String,
                             @Query("ratingFrom") ratingFrom: Int,
                             @Query("ratingTo") ratingTo: Int,
                             @Query("yearFrom") yearFrom: Int,
                             @Query("yearTo") yearTo: Int,
                             @Query("page") page: Int) : SearchByFilters

    //    Топ фильмов кинопоиска(9)
    @GET("api/v2.2/films/top")
    suspend fun fetchTop(@Query("type") type: String,
                 @Query("page") page: Int) : SearchByFilters

    //    Похожие фильмы(10)
    @GET("api/v2.2/films/{id}/similars")
    suspend fun fetchSimilarsMovieByFilmId(@Path(value = "id", encoded = true) id: Int) : RelatedFilmResponse

    //    Цифровые релизы(11)
    @GET("api/v2.1/films/releases")
    suspend fun fetchReleases(@Query("year") year: Int,
                      @Query("month") month: String,
                      @Query("page") page: Int) : DigitalReleases



//        Reviews
//    Общая инфа об отзывах(12)
    @GET("api/v1/reviews")
    suspend fun fetchReviews(@Query("filmId") filmId: Int,
                     @Query("page") page: Int) : JsonElement

    //    Отзывы по фильму(13)
    @GET("api/v2.1/films/details")
    suspend fun fetchDetailReviews(@Query("reviewId") reviewId: Int) : JsonElement



//        Staff
//    Студии по фильму(14)
    @GET("api/v1/staff")
    suspend fun fetchStaffByMovieId(@Query("filmId") filmId: Int) : List<StaffPerson>

//        Персона по id(15)
    @GET("api/v1/staff/{id}")
    suspend fun fetchStaffByPersonId(@Path(value = "id", encoded = true) id: Int) : PersonResponse

    companion object {

        fun create() : FilmWebService {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(FilmInterceptor())
                .build()

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

            return retrofit.create(FilmWebService::class.java)
        }
    }
}