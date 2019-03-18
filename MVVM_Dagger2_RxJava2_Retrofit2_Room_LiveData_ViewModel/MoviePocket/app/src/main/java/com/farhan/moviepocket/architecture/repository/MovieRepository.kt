package com.farhan.moviepocket.architecture.repository

import android.arch.lifecycle.LiveData
import com.farhan.moviepocket.architecture.MovieDao
import com.farhan.moviepocket.model.Data
import org.jetbrains.anko.doAsync
import javax.inject.Inject

class MovieRepository @Inject constructor(private val movieDao: MovieDao) {


     var movieLiveDataList: LiveData<List<Data>> = movieDao.loadAllMovies()

    fun insertMovie(data: Data) {
        doAsync {
            movieDao.insertProduct(data)
        }
    }

    private fun getAllMovies() {
        doAsync {
            movieLiveDataList
        }
    }
}