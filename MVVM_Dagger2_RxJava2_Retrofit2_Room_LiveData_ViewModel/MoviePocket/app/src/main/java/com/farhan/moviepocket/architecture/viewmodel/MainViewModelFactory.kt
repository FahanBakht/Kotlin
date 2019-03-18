package com.farhan.moviepocket.architecture.viewmodel

import android.arch.lifecycle.ViewModelProvider
import com.farhan.moviepocket.architecture.repository.MovieRepository
import android.arch.lifecycle.ViewModel


class MainViewModelFactory(private var movieRepository: MovieRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(movieRepository) as T
    }
}