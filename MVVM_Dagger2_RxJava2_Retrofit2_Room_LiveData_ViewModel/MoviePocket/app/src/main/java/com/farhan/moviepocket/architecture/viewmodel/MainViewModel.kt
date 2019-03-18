package com.farhan.moviepocket.architecture.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.farhan.moviepocket.architecture.repository.MovieRepository
import com.farhan.moviepocket.model.Data

class MainViewModel constructor(movieRepository: MovieRepository) : ViewModel() {

    var getMovieLiveDataList: LiveData<List<Data>> = movieRepository.movieLiveDataList

}