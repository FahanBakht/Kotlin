package com.farhan.moviepocket.dagger2.di

import com.farhan.moviepocket.services.ApiService
import com.farhan.moviepocket.ui.MainActivity
import dagger.Component
import javax.inject.Singleton
import com.farhan.moviepocket.architecture.AppDatabase
import com.farhan.moviepocket.architecture.MovieDao
import com.farhan.moviepocket.architecture.repository.MovieRepository
import android.app.Application
import com.farhan.moviepocket.architecture.viewmodel.MainViewModel


@Singleton
@Component(modules = [ApiModule::class, RoomModule::class, AppModule::class])
interface AppComponent {

    fun getApiService(): ApiService

    fun inject(mainActivity: MainActivity)

    fun getMovieDao(): MovieDao

    fun getAppDataBase(): AppDatabase

    fun movieRepository(): MovieRepository

    fun application(): Application
}