package com.raaf.android.searchmovie.api

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

private const val API_KEY = "22091391-7e7c-443d-a37c-8331872d6266"

class FilmInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()

        val newRequest: Request = originalRequest.newBuilder()
            .addHeader("X-API-KEY", API_KEY)
            .url(originalRequest.url())
            .build()

        return chain.proceed(newRequest)
    }
}