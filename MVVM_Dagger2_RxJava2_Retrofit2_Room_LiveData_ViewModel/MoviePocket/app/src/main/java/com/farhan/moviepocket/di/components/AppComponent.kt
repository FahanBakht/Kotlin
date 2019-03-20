package com.farhan.moviepocket.di.components

import com.farhan.moviepocket.view.MainActivity
import dagger.Component
import javax.inject.Singleton
import com.farhan.moviepocket.architecture.AppDatabase
import com.farhan.moviepocket.architecture.MovieDao
import com.farhan.moviepocket.architecture.repository.MovieRepository
import android.app.Application
import com.farhan.moviepocket.architecture.workmanager.SyncWorker
import com.farhan.moviepocket.di.module.ApiModule
import com.farhan.moviepocket.di.module.AppModule
import com.farhan.moviepocket.di.module.RoomModule
import com.farhan.moviepocket.services.ApiService


@Singleton
@Component(modules = [ApiModule::class, RoomModule::class, AppModule::class])
interface AppComponent {

    fun inject(mainActivity: MainActivity)

    fun getApiService(): ApiService

    fun injectSyncWorker(syncWorker: SyncWorker)

    fun getMovieDao(): MovieDao

    fun getAppDataBase(): AppDatabase

    fun movieRepository(): MovieRepository

    fun application(): Application


}