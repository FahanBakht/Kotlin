package com.farhan.moviepocket.di.module

import com.farhan.moviepocket.services.ApiService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApiModule {

    @Singleton
    @Provides
    fun provideApiService(): ApiService {
        return ApiService.create()
    }
}