package com.farhan.moviepocket.di.module

import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import android.arch.persistence.room.Room
import android.app.Application
import com.farhan.moviepocket.architecture.database.AppDatabase
import com.farhan.moviepocket.architecture.dao.MovieDao
import com.farhan.moviepocket.architecture.repository.MovieRepository
import com.farhan.moviepocket.utils.Constants.ROOM_DB_NAME


@Module
class RoomModule(mApplication: Application) {

    private var appDatabase: AppDatabase = Room.databaseBuilder(mApplication, AppDatabase::class.java, ROOM_DB_NAME).build()

    @Singleton
    @Provides
    fun providesAppDatabase(): AppDatabase {
        return appDatabase
    }

    @Singleton
    @Provides
    fun providesMoviesDao(appDatabase: AppDatabase): MovieDao {
        return appDatabase.getMovieDao()
    }

    @Singleton
    @Provides
    fun movieRepository(movieDao: MovieDao, mApplication: Application): MovieRepository {
        return MovieRepository(movieDao,mApplication)
    }

}