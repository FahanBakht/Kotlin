package com.farhan.moviepocket.architecture.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.farhan.moviepocket.architecture.repository.MovieRepository
import com.farhan.moviepocket.model.Data
import timber.log.Timber

class MainViewModel constructor(val movieRepository: MovieRepository) : ViewModel() {

    var movieArrayList: LiveData<List<Data>>? = null

    init {
        Timber.e("MainViewModel init")
        movieArrayList = getMovieLiveDataList()
    }

    private fun getMovieLiveDataList(): LiveData<List<Data>>{
        Timber.e("getMovieLiveDataList()")
        if (movieArrayList == null){
            Timber.e("getMovieLiveDataList() If")
            movieArrayList = movieRepository.getAllMovies()
        }
        return movieArrayList!!
    }
}