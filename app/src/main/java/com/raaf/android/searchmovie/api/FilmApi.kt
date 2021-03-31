package com.raaf.android.searchmovie.api

import com.google.gson.JsonElement
import com.raaf.android.searchmovie.dataModel.rootJSON.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FilmApi {

//    Films
//    Поиск фильма по id(1)
    @GET("api/v2.1/films/{id}")
    fun fetchMovie(@Path(value = "id", encoded = true) id: Int,
                   @Query("append_to_response") appendToResponse: Array<String>) : Call<MovieById>

//    Кадры по фильму(2)
    @GET("api/v2.1/films/{id}/frames")
    fun fetchFrames(@Path(value = "id", encoded = true) id: Int) : Call<JsonElement>

//    Трейлеры и тизеры по фильму(3)
    @GET("api/v2.1/films/{id}/videos")
    fun fetchStudios(@Path(value = "id", encoded = true) id: Int) : Call<JsonElement>

//    Студии по фильму(4)
    @GET("api/v2.1/films/{id}/studios")
    fun fetchVideos(@Path(value = "id", encoded = true) id: Int) : Call<JsonElement>

//    Сиквелы и приквелы по фильму(5)
    @GET("api/v2.1/films/{id}/sequels-and-prequels")
    fun fetchSequelsAndPrequels(@Path(value = "id", encoded = true) id: Int) : Call<JsonElement>

//    Спсиок фильмов по фразе(6)
    @GET("api/v2.1/films/search-by-keyword")
    fun fetchSearchByKeyword(@Query("keyword") keyword: String,
                             @Query("page") page: Int) : Call<SearchByKeyword>

//    Фильтры(7)
    @GET("api/v2.1/films/filters")
    fun fetchFilters() : Call<FiltersResponse>

//    Поиск по фильтрам(8)
    @GET("api/v2.1/films/search-by-filters")
    fun fetchSearchByFilters(@Query("country") country: Array<Int>?,
                             @Query("genre") genre: Array<Int>?,
                             @Query("order") order: String,
                             @Query("type") type: String,
                             @Query("ratingFrom") ratingFrom: Int,
                             @Query("ratingTo") ratingTo: Int,
                             @Query("yearFrom") yearFrom: Int,
                             @Query("yearTo") yearTo: Int,
                             @Query("page") page: Int) : Call<SearchByFilters>

//    Топ фильмов кинопоиска(9)
    @GET("api/v2.2/films/top")
    fun fetchTop(@Query("type") type: String,
                 @Query("page") page: Int) : Call<SearchByFilters>

//    Цифровые релизы(10)
    @GET("api/v2.1/films/releases")
    fun fetchReleases(@Query("year") year: Int,
                      @Query("month") month: String,
                      @Query("page") page: Int) : Call<DigitalReleases>



//    Reviews
//    Общая инфа об отзывах(11)
    @GET("api/v1/reviews")
    fun fetchReviews(@Query("filmId") filmId: Int,
                     @Query("page") page: Int) : Call<JsonElement>

//    Отзывы по фильму(12)
    @GET("api/v2.1/films/details")
    fun fetchDetailReviews(@Query("reviewId") reviewId: Int) : Call<JsonElement>



//    Staff
//    Студии по фильму(13)
    @GET("api/v1/staff")
    fun fetchStaffByMovieId(@Query("filmId") filmId: Int) : Call<JsonElement>

//    Персона по id(14)
    @GET("api/v1/staff/{id}")
    fun fetchStaffByPersonId(@Path(value = "id", encoded = true) id: Int) : Call<JsonElement>
}