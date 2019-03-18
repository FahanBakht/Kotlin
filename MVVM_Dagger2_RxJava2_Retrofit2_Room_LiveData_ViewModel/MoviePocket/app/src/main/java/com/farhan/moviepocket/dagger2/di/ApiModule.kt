package com.farhan.moviepocket.dagger2.di

import com.farhan.moviepocket.services.ApiService
import dagger.Module
import dagger.Provides

@Module
class ApiModule {

    @Provides
    fun provideApiService(): ApiService {
        return ApiService.create()
    }
}