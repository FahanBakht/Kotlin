package com.farhan.moviepocket.view

import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import com.farhan.moviepocket.R
import com.farhan.moviepocket.adapter.MoviesAdapter
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import timber.log.Timber
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.View
import com.farhan.moviepocket.architecture.viewmodel.MainViewModel
import com.farhan.moviepocket.di.module.AppModule
import com.farhan.moviepocket.di.module.RoomModule
import com.farhan.moviepocket.utils.Utils.calculateNoOfColumns
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.arch.lifecycle.ViewModelProviders
import androidx.work.*
import com.farhan.moviepocket.architecture.repository.MovieRepository
import com.farhan.moviepocket.architecture.viewmodel.MainViewModelFactory
import com.farhan.moviepocket.architecture.workmanager.SyncWorker
import com.farhan.moviepocket.di.components.DaggerAppComponent
import com.farhan.moviepocket.model.Data
import com.farhan.moviepocket.utils.Constants.ONE_TIME_WORK

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var movieRepository : MovieRepository
    private var disposable: CompositeDisposable = CompositeDisposable()
    private lateinit var mAdapter: MoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DaggerAppComponent.builder().roomModule(RoomModule(application)).appModule(
            AppModule(
                application
            )
        ).build()
            .inject(this)
        val factory  = MainViewModelFactory(movieRepository)
        val mainViewModel = ViewModelProviders.of(this,factory).get(MainViewModel::class.java)

        runSyncWorker()
        initViews()
        setUpRecyclerView()

        mainViewModel.movieArrayList!!.observe(this@MainActivity, Observer<List<Data>>{
            Timber.e("Observe runs Total: ${movieRepository.getTotalCount()}")

            loading_indicator.visibility = View.GONE
            swipe_refresh_layout.isRefreshing = false
            val mList = ArrayList<Data>(it!!)
            mAdapter.setMovieData(mList)
        })
    }

    private fun runSyncWorker(){
        val myConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>().setConstraints(myConstraints).build()
        WorkManager.getInstance().enqueueUniqueWork(ONE_TIME_WORK, ExistingWorkPolicy.KEEP, syncWorkRequest)
        WorkManager.getInstance().getWorkInfoByIdLiveData(syncWorkRequest.id)
            .observe(this@MainActivity, Observer { workInfo ->
                Timber.e("SyncWorker Observer Runs $workInfo")
            })
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Appbar Text Tittle Fade Animation
        appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            toolbar_title.alpha = 1.0f - Math.abs(verticalOffset / appBarLayout.totalScrollRange.toFloat())
        })

        // SwipeRefreshLayout Event
        swipe_refresh_layout.setOnRefreshListener {

        }
    }

    private fun setUpRecyclerView() {
        mAdapter = MoviesAdapter()
        rc_movie.layoutManager = GridLayoutManager(this, calculateNoOfColumns(this@MainActivity))
        rc_movie.adapter = mAdapter
    }

    // Search Bar which allow us to search for movie
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        val mSearch = menu.findItem(R.id.action_search)
        val mSearchView = mSearch.actionView as SearchView
        mSearchView.queryHint = getString(R.string.search)
        mSearchView.clearFocus()
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                mAdapter.filter.filter(newText)
                return true
            }
        })
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
