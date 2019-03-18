package com.farhan.moviepocket.app

import android.app.Application
import com.farhan.moviepocket.BuildConfig
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}