package com.farhan.moviepocket.services

import com.farhan.moviepocket.model.Movie
import com.farhan.moviepocket.utils.Constants
import io.reactivex.Single
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {

    @GET("movies")
    fun getMovies(): Single<Movie>

    companion object {
        fun create(): ApiService {
            val retrofit = retrofit2.Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.BASE_URL)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}