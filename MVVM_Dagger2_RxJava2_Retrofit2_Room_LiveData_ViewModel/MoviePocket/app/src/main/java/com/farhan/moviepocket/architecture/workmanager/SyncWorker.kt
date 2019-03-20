package com.farhan.moviepocket.architecture.workmanager

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.annotation.UiThread
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.farhan.moviepocket.architecture.repository.MovieRepository
import com.farhan.moviepocket.di.components.DaggerAppComponent
import com.farhan.moviepocket.di.module.AppModule
import com.farhan.moviepocket.di.module.RoomModule
import com.farhan.moviepocket.model.Data
import com.farhan.moviepocket.model.Movie
import com.farhan.moviepocket.services.ApiService
import com.farhan.moviepocket.utils.Utils.toByte
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import timber.log.Timber
import javax.inject.Inject
import io.reactivex.disposables.Disposable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target


class SyncWorker constructor(var context: Context, params: WorkerParameters) : Worker(context, params) {

    init {
        DaggerAppComponent.builder().roomModule(RoomModule(context as Application))
            .appModule(AppModule(context as Application))
            .build().injectSyncWorker(this)
    }

    @Inject
    lateinit var apiService: ApiService
    @Inject
    lateinit var movieRepository: MovieRepository

    override fun doWork(): Result {
        loadMovies()


        return Result.success()
    }

    private fun loadMovies() {
        apiService.getMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Movie> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(movie: Movie) {
                    Timber.e("loadMovies Size: ${movie.data.size}")
                    insertAllMovies(movie)
                }

                override fun onError(e: Throwable) {

                }

                override fun onComplete() {

                }

            })
    }

    private fun insertAllMovies(movie: Movie) {
        doAsync {
            //movieRepository.deleteAll()
            for (data in movie.data) {
                /* Glide.with(context)
                     .asBitmap()
                     .load(data.poster)
                     .listener(object : RequestListener<Bitmap> {
                         override fun onLoadFailed(
                             e: GlideException?,
                             model: Any?,
                             target: Target<Bitmap>?,
                             isFirstResource: Boolean
                         ): Boolean {
                             return false
                         }

                         override fun onResourceReady(
                             resource: Bitmap,
                             model: Any?,
                             target: Target<Bitmap>?,
                             dataSource: DataSource?,
                             isFirstResource: Boolean
                         ): Boolean {
                             data.imgBase64 = toByte(bitmap = resource)

                             return false
                         }
                     })*/ // .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get()
                data.imgBase64 = "Example"
                movieRepository.insertMovie(data)
            }
            uiThread {
                Timber.e("Inserted Completed Total= ${movieRepository.getTotalCount()}")
            }
        }
    }
}