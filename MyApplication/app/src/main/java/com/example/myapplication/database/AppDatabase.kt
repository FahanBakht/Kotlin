package com.example.myapplication.database

import android.content.Context

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.example.myapplication.views.main.dao.MainDao
import com.example.myapplication.views.main.model.ValueHolder

// Database to Store favorites list
@Database(entities = [ValueHolder::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract val mainDao: MainDao

    companion object {
        private const val DATABASE_NAME = "myDB"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {

            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, DATABASE_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }

        }
    }
}