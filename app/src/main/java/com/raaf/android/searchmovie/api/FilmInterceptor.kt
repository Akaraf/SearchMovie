package com.raaf.android.searchmovie.api

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


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
