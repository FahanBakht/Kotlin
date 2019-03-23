package com.farhan.moviepocket.di.components

import com.farhan.moviepocket.view.MainActivity
import dagger.Component
import javax.inject.Singleton
import com.farhan.moviepocket.architecture.database.AppDatabase
import com.farhan.moviepocket.architecture.dao.MovieDao
import com.farhan.moviepocket.architecture.repository.MovieRepository
import android.app.Application
import com.farhan.moviepocket.di.module.ApiModule
import com.farhan.moviepocket.di.module.AppModule
import com.farhan.moviepocket.di.module.RoomModule
import com.farhan.moviepocket.services.ApiService


@Singleton
@Component(modules = [ApiModule::class, RoomModule::class, AppModule::class])
interface AppComponent {

    fun inject(mainActivity: MainActivity)

    fun getApiService(): ApiService

    fun getMovieDao(): MovieDao

    fun getAppDataBase(): AppDatabase

    fun movieRepository(): MovieRepository

    fun application(): Application

    fun injectMovieRepository(movieRepository:MovieRepository)


}