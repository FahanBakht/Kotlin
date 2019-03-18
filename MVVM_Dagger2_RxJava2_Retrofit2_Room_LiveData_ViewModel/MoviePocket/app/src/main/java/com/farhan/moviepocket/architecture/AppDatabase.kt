package com.farhan.moviepocket.architecture

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.farhan.moviepocket.model.Data
import com.farhan.moviepocket.utils.Constants.ROOM_VERSION

@Database(entities = [Data::class], version = ROOM_VERSION, exportSchema = false)
abstract class AppDatabase  : RoomDatabase() {

    abstract fun getMovieDao(): MovieDao
}