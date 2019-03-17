package example.farhan.com.moviepocket;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.farhan.com.moviepocket.adapter.MoviesAdapter;
import example.farhan.com.moviepocket.architecture.components.set.up.viewModel.FavoriteViewModel;
import example.farhan.com.moviepocket.architecture.components.set.up.viewModel.MainActivityViewModel;
import example.farhan.com.moviepocket.fragment.ProfileFragment;
import example.farhan.com.moviepocket.model.Favorite;
import example.farhan.com.moviepocket.model.MovieShort;
import example.farhan.com.moviepocket.model.User;
import example.farhan.com.moviepocket.utils.Constants;
import example.farhan.com.moviepocket.utils.LoadingDialog;
import example.farhan.com.moviepocket.utils.NetworkUtils;
import example.farhan.com.moviepocket.utils.Prefs;
import example.farhan.com.moviepocket.utils.Utils;

import static example.farhan.com.moviepocket.utils.Constants.FAVORITE;
import static example.farhan.com.moviepocket.utils.Constants.FAVORITE_OBJECT;
import static example.farhan.com.moviepocket.utils.Constants.FIREBASE_PARENT_NODE_NAME;
import static example.farhan.com.moviepocket.utils.Constants.FROM;
import static example.farhan.com.moviepocket.utils.Constants.MOVIE_ID;
import static example.farhan.com.moviepocket.utils.Constants.MOVIE_NAME;
import static example.farhan.com.moviepocket.utils.Constants.ONLINE;
import static example.farhan.com.moviepocket.utils.Constants.REFERENCE_USERS;
import static example.farhan.com.moviepocket.utils.Constants.SEARCH;
import static example.farhan.com.moviepocket.utils.Constants.SEARCH_QUERY;
import static example.farhan.com.moviepocket.utils.Constants.SORT_BY;
import static example.farhan.com.moviepocket.utils.Constants.USER_NAME;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MoviesAdapter.MoviesAdapterOnClickHandler, ProfileFragment.OnHideToolbar, MoviesAdapter.MoviesAdapterOnClickHandlerForFavorite {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int CAME_FROM_ON_SWIPE = 1;
    private static final int CAME_FROM_SIMPLE_CALL = 0;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view_movie_listing)
    RecyclerView mRecyclerView;
    @BindView(R.id.appBar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.toolbar_title)
    AppCompatTextView mAppCompatTextView;
    @BindView(R.id.loading_indicator)
    com.wang.avi.AVLoadingIndicatorView loadingIndicator;
    @BindView(R.id.loading_indicator_on_pagination)
    com.wang.avi.AVLoadingIndicatorView loadingIndicatorOnPagination;
    @BindView(R.id.root_frame_main_activity)
    FrameLayout rootFrameLayout;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.main_empty_view)
    RelativeLayout emptyView;

    private ActionBarDrawerToggle toggle;
    private Boolean mToolBarNavigationListenerIsRegistered = false;
    private FirebaseAuth mAuth;
    private User userObj;
    private DatabaseReference mDatabaseReference;
    private LoadingDialog loadingDialog = LoadingDialog.getInstance();
    private Utils utils = Utils.getInstance();
    private MoviesAdapter mAdapter;
    private StaggeredGridLayoutManager sGLM;
    private MainActivityViewModel mainActivityViewModel;
    private String sortBy = Constants.POPULAR;
    private int navProfileId = 0;
    private int currentPage = 1;
    private boolean isFetchingMovies;
    private Prefs mPrefs = Prefs.getInstance();
    private FavoriteViewModel favoriteViewModel;
    private String searchQuery;
    MenuItem searchMenuItem;

    // To register Custom Dialog
    @Override
    protected void onResume() {
        super.onResume();
        loadingDialog.setLoadingDialog(MainActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();
        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        favoriteViewModel = ViewModelProviders.of(this).get(FavoriteViewModel.class);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        if (savedInstanceState != null && savedInstanceState.containsKey(SORT_BY)) {
            sortBy = savedInstanceState.getString(SORT_BY);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(REFERENCE_USERS)) {
            preserveConfigurationHeaderData(headerView, (User) savedInstanceState.getParcelable(REFERENCE_USERS));
        } else {
            preserveConfigurationHeaderData(headerView, null);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(SEARCH_QUERY)) {
            searchQuery = savedInstanceState.getString(SEARCH_QUERY);
        }

        // SwipeRefreshLayout Event
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtils.isNetworkAvailable(MainActivity.this)) {
                    if (sortBy.equals(SEARCH)) {
                        searchMovie(searchQuery, CAME_FROM_ON_SWIPE);
                    } else {
                        retrieveMovieList(sortBy, CAME_FROM_ON_SWIPE);
                    }
                } else {
                    utils.showToast(MainActivity.this, getString(R.string.no_network_found_sort_change_to_favorite));
                    sortBy = FAVORITE;
                    retrieveMovieFromFavorite();
                }
            }
        });

        // Appbar Text Tittle Fade Animation
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mAppCompatTextView.setAlpha(1.0f - Math.abs(verticalOffset / (float) appBarLayout.getTotalScrollRange()));
            }
        });

        // Set up Main Movie RecyclerView Adapter
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        sGLM = new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sGLM);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MoviesAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setMoviesAdapterOnClickHandler(MainActivity.this);
        mAdapter.setMoviesAdapterOnClickHandlerForFavorite(this);

        // Check every time when call sortBy change and if no network switch to Favorite
        switch (sortBy) {
            case FAVORITE:
                retrieveMovieFromFavorite();
                break;
            case SEARCH:
                searchMovie(searchQuery, CAME_FROM_SIMPLE_CALL);
                break;
            default:
                retrieveMovieList(sortBy, CAME_FROM_SIMPLE_CALL);
                break;
        }

        // Set up RecyclerView scroll event to launch Pagination
        setupOnScrollListener();

        // Drawer Callbacks used for to remove the laggy FragmentTransaction
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                switch (navProfileId) {
                    case R.id.nav_profile: {
                        animationFade(mAppCompatTextView);
                        searchMenuItem.setVisible(false);
                        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        // Remove hamburger
                        toggle.setDrawerIndicatorEnabled(false);
                        // Show back button
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        // when DrawerToggle is disabled i.e. setDrawerIndicatorEnabled(false), navigation icon
                        // clicks are disabled i.e. the UP button will not work.
                        // We need to add a listener, as in below, so DrawerToggle will forward
                        // click events to this listener.

                        mAppCompatTextView.setText(R.string.profile);

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                        ProfileFragment fragment = ProfileFragment.newInstance(userObj);
                        transaction.replace(R.id.root_frame_main_activity, fragment);
                        transaction.addToBackStack(Constants.PROFILE_FRAGMENT_TAG);
                        transaction.commit();
                        navProfileId = 0;
                    }
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        // Check if the icon is Drawer Hamburger or Back arrow
        if (!mToolBarNavigationListenerIsRegistered) {
            toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Doesn't have to be onBackPressed
                    onBackPressed();
                }
            });
            mToolBarNavigationListenerIsRegistered = true;
        }
    }

    // Method which get's Favorite Data and show's in RecyclerView and also keep an track of Network Status and Empty View
    private void retrieveMovieFromFavorite() {
        loadingIndicator.smoothToShow();
        drawerFavoriteSelected();
        favoriteViewModel.getAllFavorite().observe(this, new Observer<List<Favorite>>() {
            @Override
            public void onChanged(@Nullable List<Favorite> favorites) {
                if (favorites != null && sortBy.equals(FAVORITE)) {
                    if (favorites.size() != 0) {
                        mAdapter.clearMoviesData();
                        mAdapter.setFavoriteData(new ArrayList<>(favorites), Constants.RUN_FOR_FAVORITE);
                        loadingIndicator.smoothToHide();
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (emptyView.getVisibility() == View.VISIBLE) {
                            emptyView.setVisibility(View.GONE);
                        }
                    } else {
                        mAdapter.clearFavoriteData();
                        mAdapter.clearMoviesData();
                        loadingIndicator.smoothToHide();
                        mSwipeRefreshLayout.setRefreshing(false);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (favorites == null && sortBy.equals(FAVORITE)) {
                        loadingIndicator.smoothToHide();
                        mSwipeRefreshLayout.setRefreshing(false);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    // RecyclerView Scroll event for Pagination
    private void setupOnScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int totalItemCount = sGLM.getItemCount();
                int visibleItemCount = sGLM.getChildCount();
                int firstVisibleItem = sGLM.findFirstVisibleItemPositions(null)[0];

                if (firstVisibleItem + visibleItemCount >= totalItemCount && !(sortBy.equals(FAVORITE)) && !(sortBy.equals(SEARCH))) {
                    if (!isFetchingMovies) {
                        retrieveMovieListOnPagination(sortBy, currentPage + 1);
                    }
                }
            }
        });
    }

    // Retrieve Initial List of Movies by calling this method
    // 0 for simple call
    // 1 from on Refresh
    private void retrieveMovieList(String Sort, int cameFrom) {
        if (!(NetworkUtils.isNetworkAvailable(MainActivity.this))) {
            utils.showToast(MainActivity.this, getString(R.string.no_network_found_sort_change_to_favorite));
            sortBy = FAVORITE;
            retrieveMovieFromFavorite();
            return;
        }

        if (emptyView.getVisibility() == View.VISIBLE) {
            emptyView.setVisibility(View.GONE);
        }

        if (cameFrom == CAME_FROM_SIMPLE_CALL) {
            loadingIndicator.smoothToShow();
            mainActivityViewModel.getMoviesList(Sort).observe(this, new Observer<ArrayList<MovieShort>>() {
                @Override
                public void onChanged(@Nullable ArrayList<MovieShort> movieShorts) {
                    if (movieShorts != null) {
                        mAdapter.clearFavoriteData();
                        mAdapter.setMovieData(movieShorts, Constants.RUN_FOR_MOVIE);
                        loadingIndicator.smoothToHide();
                    } else {
                        loadingIndicator.smoothToHide();
                        utils.showToast(MainActivity.this, getString(R.string.error_unable_to_load_data));
                    }
                }
            });
        } else if (cameFrom == CAME_FROM_ON_SWIPE) {
            mSwipeRefreshLayout.setRefreshing(true);
            mainActivityViewModel.clearMoviesList();
            mAdapter.clearFavoriteData();
            mAdapter.clearMoviesData();
            currentPage = 1;

            if (sortBy.equals(FAVORITE)) {
                retrieveMovieFromFavorite();
                mSwipeRefreshLayout.setRefreshing(false);
            } else {
                mainActivityViewModel.getMoviesList(Sort).observe(this, new Observer<ArrayList<MovieShort>>() {
                    @Override
                    public void onChanged(@Nullable ArrayList<MovieShort> movieShorts) {
                        if (movieShorts != null) {
                            mAdapter.setMovieData(movieShorts, Constants.RUN_FOR_MOVIE);
                            mSwipeRefreshLayout.setRefreshing(false);
                        } else {
                            mSwipeRefreshLayout.setRefreshing(false);
                            utils.showToast(MainActivity.this, getString(R.string.error_unable_to_load_data));
                        }
                    }
                });
            }
        }
    }

    // Retrieve More List of Movies when Scrolling by calling this method
    private void retrieveMovieListOnPagination(String Sort, final int Page) {
        if (!(NetworkUtils.isNetworkAvailable(MainActivity.this))) {
            utils.showToast(MainActivity.this, "No Network is found Sort Changed to Favorite");
            return;
        }

        isFetchingMovies = true;
        loadingIndicatorOnPagination.show();
        mainActivityViewModel.getMoviesListOnPagination(Sort, Page).observe(this, new Observer<ArrayList<MovieShort>>() {
            @Override
            public void onChanged(@Nullable ArrayList<MovieShort> movieShorts) {
                if (movieShorts != null) {
                    //Collections.sort(movieShorts);
                    loadingIndicatorOnPagination.hide();
                    mAdapter.appendMoviesData(movieShorts);

                } else {
                    loadingIndicatorOnPagination.hide();
                    utils.showToast(MainActivity.this, "Error: unable to load data");
                }

                currentPage = Page;
                isFetchingMovies = false;
            }
        });
    }

    // Get Data from FireBase to show in Drawer Header and check and retrieve on configuration changes
    private void preserveConfigurationHeaderData(final View NavHeaderView, User userObject) {
        final de.hdodenhof.circleimageview.CircleImageView imageProfile = NavHeaderView.findViewById(R.id.nav_header_image);
        final TextView userName = NavHeaderView.findViewById(R.id.nav_drawer_username);
        final TextView email = NavHeaderView.findViewById(R.id.nav_drawer_email);

        if (userObject != null) {
            Glide.with(MainActivity.this)
                    .asBitmap()
                    .load(userObject.getProfileImageUrl())
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imageProfile);

            userName.setText(userObject.getName());
            email.setText(userObject.getEmail());
            Log.e(TAG, "Trigger MainActivity addValueEventListener Offline");
            return;
        }

        mDatabaseReference.child(FIREBASE_PARENT_NODE_NAME).child(Constants.REFERENCE_USERS).child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    userObj = dataSnapshot.getValue(User.class);
                    if (userObj != null) {
                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load(userObj.getProfileImageUrl())
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                .into(imageProfile);

                        userName.setText(userObj.getName());
                        email.setText(userObj.getEmail());
                        Log.e(TAG, "Trigger MainActivity addValueEventListener");
                    }

                } else {
                    utils.showToast(MainActivity.this, "Error in retrieving user's information");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                utils.showToast(MainActivity.this, "Error " + databaseError.getMessage());
            }
        });
    }

    // RecyclerView OnClick item view when data is load from Internet
    @Override
    public void onClick(MovieShort moviesShortObj) {
        if (NetworkUtils.isNetworkAvailable(MainActivity.this)) {
            String userName = mPrefs.getUserNameFromPref(MainActivity.this);
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra(FROM, ONLINE);
            intent.putExtra(MOVIE_ID, moviesShortObj.getId());
            intent.putExtra(USER_NAME, userName);
            intent.putExtra(MOVIE_NAME, moviesShortObj.getTitle());
            startActivity(intent);
        } else {
            utils.showToast(MainActivity.this, getString(R.string.no_network_found_sort_change_to_favorite));
            sortBy = FAVORITE;
            retrieveMovieFromFavorite();
        }
    }

    // RecyclerView OnClick item view when data is load from Database Favorite
    @Override
    public void onClick(Favorite favoriteObj) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(FROM, FAVORITE);
        Gson gson = new Gson();
        String favoriteString = gson.toJson(favoriteObj);
        intent.putExtra(MOVIE_ID, Long.valueOf(favoriteObj.getId()));
        intent.putExtra(FAVORITE_OBJECT, favoriteString);
        intent.putExtra(MOVIE_NAME, favoriteObj.getTitle());
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SORT_BY, sortBy);
        outState.putParcelable(REFERENCE_USERS, userObj);
        outState.putString(SEARCH_QUERY, searchQuery);
    }

    // Show About us Dialog
    public void showAboutUsDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.about_us, null);
        dialog.setView(rootView);
        AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.AboutUsDialogTheme;
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageButton sendEmail = rootView.findViewById(R.id.about_us_send_email);
        ImageButton call = rootView.findViewById(R.id.about_us_call);

        alertDialog.show();


        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_SUBJECT, "Movie Pocket");
                intent.putExtra(Intent.EXTRA_TEXT, "Email From Movie Pocket");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    utils.showToast(MainActivity.this, getString(R.string.error_no_app_found_to_handel));
                }
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", getResources().getString(R.string.myPhone), null));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    utils.showToast(MainActivity.this, getString(R.string.error_no_app_found_to_call));
                }
            }
        });

        utils.doKeepDialog(alertDialog);
    }

    // Handle navigation view item clicks here.
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            if (sortBy.equals(Constants.POPULAR)) {
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
            sortBy = Constants.POPULAR;
            currentPage = 1;
            mainActivityViewModel.clearMoviesList();
            retrieveMovieList(sortBy, CAME_FROM_SIMPLE_CALL);

        } else if (id == R.id.nav_profile) {
            if (NetworkUtils.isNetworkAvailable(MainActivity.this)) {
                navProfileId = R.id.nav_profile;
            } else {
                navProfileId = 0;
                utils.showToast(MainActivity.this, getString(R.string.no_network_to_access_profile));
                return true;
            }
        } else if (id == R.id.nav_favorite) {
            if (sortBy.equals(Constants.FAVORITE)) {
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
            sortBy = Constants.FAVORITE;
            currentPage = 1;
            mainActivityViewModel.clearMoviesList();
            retrieveMovieFromFavorite();

        } else if (id == R.id.nav_up_coming) {
            if (sortBy.equals(Constants.UPCOMING)) {
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
            sortBy = Constants.UPCOMING;
            currentPage = 1;
            mainActivityViewModel.clearMoviesList();
            retrieveMovieList(sortBy, CAME_FROM_SIMPLE_CALL);

        } else if (id == R.id.nav_top_rated) {
            if (sortBy.equals(Constants.TOP_RATED)) {
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
            sortBy = Constants.TOP_RATED;
            currentPage = 1;
            mainActivityViewModel.clearMoviesList();
            retrieveMovieList(sortBy, CAME_FROM_SIMPLE_CALL);

        } else if (id == R.id.nav_about_us) {
            showAboutUsDialog();

        } else if (id == R.id.nav_logout) {
            if (NetworkUtils.isNetworkAvailable(MainActivity.this)) {
                loadingDialog.showLoadingDialog();
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                loadingDialog.dismissLoadingDialog();
                MainActivity.this.finish();
            } else {
                utils.showToast(MainActivity.this, getString(R.string.no_internet_connection_error));
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Select drawer favorite when there is no connection
    private void drawerFavoriteSelected() {
        navigationView.getMenu().getItem(2).setCheckable(true);
    }

    // Setting things back after fragment detach
    @Override
    public void onHidingToolbar() {
        animationFade(mAppCompatTextView);
        searchMenuItem.setVisible(true);
        mAppCompatTextView.setText(R.string.app_name);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        // Remove back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        // Show hamburger
        toggle.setDrawerIndicatorEnabled(true);
        mToolBarNavigationListenerIsRegistered = false;
    }

    // Animation for Tool bar Tittle text
    private void animationFade(View view) {
        AlphaAnimation fadeIn = new AlphaAnimation(1.0f, 0.0f);
        AlphaAnimation fadeOut = new AlphaAnimation(0.0f, 1.0f);
        view.startAnimation(fadeIn);
        view.startAnimation(fadeOut);
        fadeIn.setDuration(1000);
        fadeIn.setFillAfter(true);
        fadeOut.setDuration(1000);
        fadeOut.setFillAfter(true);
        fadeIn.setStartOffset(500);
        fadeOut.setStartOffset(500);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Search Bar which allow us to search for movie
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        MenuItem mSearch = menu.findItem(R.id.action_search);
        searchMenuItem = mSearch;
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint(getString(R.string.search));
        if (searchQuery != null && !searchQuery.equals("")) {
            mSearch.expandActionView();
            mSearchView.setQuery(searchQuery, false);
            mSearchView.clearFocus();
        }

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query;
                searchMovie(query, CAME_FROM_SIMPLE_CALL);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        return true;
    }

    // Search Movie Method which calls search Query
    private void searchMovie(String query, final int cameFrom) {
        if (NetworkUtils.isNetworkAvailable(MainActivity.this)) {

            if (emptyView.getVisibility() == View.VISIBLE) {
                emptyView.setVisibility(View.GONE);
            }

            loadingIndicator.smoothToShow();
            mainActivityViewModel.getSearchMovies(query).observe(MainActivity.this, new Observer<ArrayList<MovieShort>>() {
                @Override
                public void onChanged(@Nullable ArrayList<MovieShort> movieShorts) {
                    if (movieShorts != null) {
                        sortBy = SEARCH;
                        mAdapter.clearFavoriteData();
                        currentPage = 1;
                        mainActivityViewModel.clearMoviesList();
                        if (cameFrom == CAME_FROM_ON_SWIPE) {
                            mainActivityViewModel.clearSearchMovieList();
                        }
                        mAdapter.setMovieData(movieShorts, Constants.RUN_FOR_MOVIE);
                        mSwipeRefreshLayout.setRefreshing(false);
                        loadingIndicator.smoothToHide();
                    } else {
                        loadingIndicator.smoothToHide();
                        utils.showToast(MainActivity.this, getString(R.string.error_unable_to_load_data));
                    }
                }
            });
        } else {
            loadingIndicator.smoothToHide();
            utils.showToast(MainActivity.this, getString(R.string.no_network_found_sort_change_to_favorite));
            sortBy = FAVORITE;
            retrieveMovieFromFavorite();
        }
    }
}
