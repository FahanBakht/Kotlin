package com.farhan.moviepocket.architecture.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.farhan.moviepocket.architecture.MovieDao
import com.farhan.moviepocket.model.Data
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.ArrayList
import javax.inject.Inject

class MovieRepository @Inject constructor(private val movieDao: MovieDao) {

    //var movieLiveDataList: LiveData<List<Data>> = movieDao.loadAllMovies()

    fun insertMovie(data: Data) {
        doAsync {
            movieDao.insertProduct(data)
        }
    }

    fun getAllMovies(): LiveData<List<Data>> {
        //var data: LiveData<List<Data>>? = null
        val data = MutableLiveData<List<Data>>()
        doAsync {
            val movieList = movieDao.loadAllMovies()
            uiThread {

                data.value = movieList
            }
        }
        return data
    }

    fun getTotalCount(): Int {
        var totalCount = 0
        doAsync {
            totalCount = movieDao.getTotalMoviesCount()
        }.get()
        return totalCount
    }

    fun deleteAll(){
        doAsync {
            movieDao.deleteAllMovies()
        }
    }
}