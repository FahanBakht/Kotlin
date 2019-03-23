package com.farhan.moviepocket.architecture.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.farhan.moviepocket.architecture.dao.MovieDao
import com.farhan.moviepocket.di.components.DaggerAppComponent
import com.farhan.moviepocket.di.module.AppModule
import com.farhan.moviepocket.di.module.RoomModule
import com.farhan.moviepocket.model.Data
import com.farhan.moviepocket.model.Movie
import com.farhan.moviepocket.services.ApiService
import com.farhan.moviepocket.utils.Utils.isNetworkAvailable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import timber.log.Timber
import javax.inject.Inject

class MovieRepository @Inject constructor(private val movieDao: MovieDao, mApplication: Application) {

    init {
        DaggerAppComponent.builder().roomModule(RoomModule(mApplication))
            .appModule(AppModule(mApplication))
            .build().injectMovieRepository(this)
    }

    @Inject
    lateinit var apiService: ApiService
    @Inject
    lateinit var application: Application


    fun insertMovie(data: List<Data>) {
        doAsync {
            if (movieDao.getTotalMoviesCount() != data.size) {
                Timber.e("Need to Inserting")
                movieDao.insertProduct(data)
            } else {
                Timber.e("No need to insert")
            }

            Timber.e("Inserted Completed Total= ${movieDao.getTotalMoviesCount()}")
        }
    }

    fun getAllMovies(): LiveData<List<Data>> {
        Timber.e("getAllMovies")
        return movieDao.loadAllMovies()
    }

    fun getTotalCount(): Int {
        var totalCount = 0
        doAsync {
            totalCount = movieDao.getTotalMoviesCount()
        }.get()
        return totalCount
    }

    fun deleteAll() {
            doAsync {
                movieDao.deleteAllMovies()
                Timber.e("deleteAll(): ${movieDao.getTotalMoviesCount()}")
            }.get()
    }

    fun loadMovies(): LiveData<List<Data>> {
        if (isNetworkAvailable(application)) {
            val mLiveData: MutableLiveData<List<Data>> = MutableLiveData()
            apiService.getMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Movie> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(movie: Movie) {
                        insertMovie(movie.data)

                        Timber.e("loadMovies Size: ${movie.data.size}")

                        mLiveData.value = movie.data
                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onComplete() {
                    }
                })
            return mLiveData
        } else {
            Timber.e("loadMovies() else: ${getTotalCount()}")
            return getAllMovies()
        }
    }
}