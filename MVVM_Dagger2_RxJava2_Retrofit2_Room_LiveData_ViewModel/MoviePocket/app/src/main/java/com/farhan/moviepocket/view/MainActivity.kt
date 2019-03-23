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
import android.support.annotation.NonNull
import android.support.annotation.Nullable
import com.farhan.moviepocket.architecture.repository.MovieRepository
import com.farhan.moviepocket.architecture.viewmodel.MainViewModelFactory
import com.farhan.moviepocket.di.components.DaggerAppComponent
import com.farhan.moviepocket.model.Data
import com.farhan.moviepocket.utils.Utils
import android.support.test.espresso.IdlingResource
import android.support.annotation.VisibleForTesting
import com.farhan.moviepocket.idlingResource.SimpleIdlingResource

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var movieRepository: MovieRepository
    private var disposable: CompositeDisposable = CompositeDisposable()
    private lateinit var mAdapter: MoviesAdapter
    // The Idling Resource which will be null in production.
    @Nullable
    private var mIdlingResource: SimpleIdlingResource? = null

    @VisibleForTesting
    @NonNull
    fun getIdlingResource(): IdlingResource {
        if (mIdlingResource == null) {
            mIdlingResource = SimpleIdlingResource()
        }
        return mIdlingResource!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DaggerAppComponent.builder().roomModule(RoomModule(application)).appModule(AppModule(application)).build()
            .inject(this)
        val factory = MainViewModelFactory(movieRepository)

        val mainViewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)

        initViews()
        setUpRecyclerView()

        observeLiveData(mainViewModel)

        // SwipeRefreshLayout Event
        swipe_refresh_layout.setOnRefreshListener {
            if (Utils.isNetworkAvailable(application)) {
                mainViewModel.clearMoviesList()
                mAdapter.clearMoviesData()

                observeLiveData(mainViewModel)

            } else {
                swipe_refresh_layout.isRefreshing = false
                Utils.showToast(application, "Can't process This Operation reburies network connection.")
            }
        }
    }

    private fun observeLiveData(mainViewModel: MainViewModel) {
        mainViewModel.getMovieLiveDataList()?.observe(this@MainActivity, Observer<List<Data>> {
            if (mIdlingResource != null) {
                mIdlingResource!!.setIdleState(true)
            }
            Timber.e("Observe runs Total: ${movieRepository.getTotalCount()}")
            loading_indicator.visibility = View.GONE
            swipe_refresh_layout.isRefreshing = false
            val mList = ArrayList<Data>(it!!)
            mAdapter.setMovieData(mList)
        })
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Appbar Text Tittle Fade Animation
        appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            toolbar_title.alpha = 1.0f - Math.abs(verticalOffset / appBarLayout.totalScrollRange.toFloat())
        })
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
