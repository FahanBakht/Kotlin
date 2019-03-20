package com.farhan.moviepocket.di.module

import dagger.Module
import android.app.Application
import javax.inject.Singleton
import dagger.Provides

@Module
class AppModule(application: Application) {

   private var mApplication: Application = application

    @Provides
    @Singleton
    fun providesApplication(): Application {
        return mApplication
    }
}