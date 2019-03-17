package example.farhan.com.moviepocket;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.farhan.com.moviepocket.adapter.CastAdapter;
import example.farhan.com.moviepocket.adapter.CommentArrayAdapter;
import example.farhan.com.moviepocket.adapter.RateUsArrayAdapter;
import example.farhan.com.moviepocket.architecture.components.set.up.viewModel.DetailActivityViewModel;
import example.farhan.com.moviepocket.architecture.components.set.up.viewModel.FavoriteViewModel;
import example.farhan.com.moviepocket.interfaces.AsyncTaskResponse;
import example.farhan.com.moviepocket.model.Cast;
import example.farhan.com.moviepocket.model.CastInfo;
import example.farhan.com.moviepocket.model.Comment;
import example.farhan.com.moviepocket.model.Favorite;
import example.farhan.com.moviepocket.model.Genre;
import example.farhan.com.moviepocket.model.MovieDetails;
import example.farhan.com.moviepocket.model.ProductionCompanies;
import example.farhan.com.moviepocket.model.Rating;
import example.farhan.com.moviepocket.model.Results;
import example.farhan.com.moviepocket.model.SpokenLanguage;
import example.farhan.com.moviepocket.utils.Constants;
import example.farhan.com.moviepocket.utils.LoadingDialog;
import example.farhan.com.moviepocket.utils.NetworkUtils;
import example.farhan.com.moviepocket.utils.Utils;
import example.farhan.com.moviepocket.widget.UpdaterWidgetService;

import static example.farhan.com.moviepocket.utils.Constants.COMMENTS;
import static example.farhan.com.moviepocket.utils.Constants.FAVORITE;
import static example.farhan.com.moviepocket.utils.Constants.FAVORITE_OBJECT;
import static example.farhan.com.moviepocket.utils.Constants.FIREBASE_PARENT_NODE_NAME;
import static example.farhan.com.moviepocket.utils.Constants.FROM;
import static example.farhan.com.moviepocket.utils.Constants.IS_FAVORITE;
import static example.farhan.com.moviepocket.utils.Constants.IS_PLAY;
import static example.farhan.com.moviepocket.utils.Constants.MOVIES_DATA;
import static example.farhan.com.moviepocket.utils.Constants.MOVIE_ID;
import static example.farhan.com.moviepocket.utils.Constants.MOVIE_NAME;
import static example.farhan.com.moviepocket.utils.Constants.ONLINE;
import static example.farhan.com.moviepocket.utils.Constants.RATINGS;
import static example.farhan.com.moviepocket.utils.Constants.SLIDE_UP_MILI;
import static example.farhan.com.moviepocket.utils.Constants.USER_NAME;
import static example.farhan.com.moviepocket.utils.Constants.YOUTUBE_API_KEY;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener, YouTubePlayer.OnInitializedListener, CastAdapter.CastAdapterOnClickHandler {

    @BindView(R.id.img_detail_poster)
    ImageView imgPoster;
    @BindView(R.id.tv_detail_movie_name)
    TextView movieTitle;
    @BindView(R.id.tv_detail_movie_name_view_rating)
    TextView movieRating;
    @BindView(R.id.tv_detail_movie_rate_us)
    TextView movieRateMovie;
    @BindView(R.id.tv_detail_movie_tag_line)
    TextView movieTagLine;
    @BindView(R.id.tv_detail_movie_overview)
    TextView movieOverview;
    @BindView(R.id.detail_line_one)
    View lineOne;
    @BindView(R.id.detail_line_two)
    View lineTwo;
    @BindView(R.id.detail_cast_label)
    TextView castLabel;
    @BindView(R.id.rv_detail_movie_cast)
    RecyclerView mRecyclerViewCast;
    @BindView(R.id.tv_detail_movie_imbd_rating)
    TextView movieImbdRating;
    @BindView(R.id.tv_detail_movie_status)
    TextView movieStatus;
    @BindView(R.id.tv_detail_movie_spoken_language)
    TextView movieLanguage;
    @BindView(R.id.tv_detail_movie_duration)
    TextView movieDuration;
    @BindView(R.id.tv_detail_movie_budget)
    TextView movieBudget;
    @BindView(R.id.tv_detail_movie_genres)
    TextView movieGenres;
    @BindView(R.id.tv_detail_movie_revenue)
    TextView movieRevenue;
    @BindView(R.id.tv_detail_movie_release_date)
    TextView movieReleaseDate;
    @BindView(R.id.tv_detail_movie_production_companies)
    TextView movieProductionCompanies;
    @BindView(R.id.et_comment)
    EditText commentField;
    @BindView(R.id.btn_comment_send)
    ImageButton btnSendComment;
    @BindView(R.id.comment_list_view)
    example.farhan.com.moviepocket.utils.NonScrollListView commentListView;
    @BindView(R.id.toolbar_detail_activity)
    android.support.v7.widget.Toolbar toolbar;
    @BindView(R.id.appBar_detail)
    AppBarLayout appBar;
    @BindView(R.id.comment_container)
    LinearLayout commentContainer;
    @BindView(R.id.detail_line_three)
    View lineThree;
    @BindView(R.id.label_comment)
    TextView labelComment;

    public static final int DEFAULT_MSG_LENGTH_LIMIT = 50;
    private static final String TAG = DetailActivity.class.getSimpleName();
    private Utils utils = Utils.getInstance();
    private LoadingDialog loadingDialog = LoadingDialog.getInstance();
    private int seek;
    private boolean fullScreenVideo = false;
    private ArrayList<String> movieTrailerKeys;
    private long movieId;
    private DetailActivityViewModel detailActivityViewModel;
    private YouTubePlayerFragment youTubePlayerFragment;
    private CastAdapter castAdapter;
    private ArrayList<Rating> ratingArrayList;
    private String userName;
    private String userKey;
    private ArrayList<String> key;
    private RateUsArrayAdapter rateUsArrayAdapter;
    private DatabaseReference myReference;
    private ArrayList<Comment> commentArrayList;
    private CommentArrayAdapter commentAdapter;
    private boolean isPlay = false;
    private Menu mMenu;
    private boolean isFavorite = false;
    private FavoriteViewModel favoriteViewModel;
    private Favorite mFavorite;
    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        detailActivityViewModel = ViewModelProviders.of(this).get(DetailActivityViewModel.class);
        favoriteViewModel = ViewModelProviders.of(this).get(FavoriteViewModel.class);

        loadingDialog.setLoadingDialog(DetailActivity.this);
        setSupportActionBar(toolbar);
        String movieName = getIntent().getExtras().getString(MOVIE_NAME);
        getSupportActionBar().setTitle(movieName);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Getting Data from Intent and check if user came from Online or offline
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(MOVIE_ID) && extras.containsKey(USER_NAME) && extras.containsKey(FROM)) {
            from = extras.getString(FROM);
            movieId = extras.getLong(MOVIE_ID);
            userName = extras.getString(USER_NAME);
        } else if (extras != null && extras.containsKey(FAVORITE_OBJECT) && extras.containsKey(MOVIE_ID) && extras.containsKey(FROM)) {
            from = extras.getString(FROM);
            String favoriteString = extras.getString(FAVORITE_OBJECT);
            Gson gson = new Gson();
            mFavorite = gson.fromJson(favoriteString, Favorite.class);
            movieId = extras.getLong(MOVIE_ID);
        } else {
            this.finish();
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(IS_PLAY))
                isPlay = savedInstanceState.getBoolean(IS_PLAY);

            if (savedInstanceState.containsKey(MOVIE_ID))
                movieId = savedInstanceState.getLong(MOVIE_ID);

            if (savedInstanceState.containsKey(IS_FAVORITE))
                isFavorite = savedInstanceState.getBoolean(IS_FAVORITE);
        }
        // initialize Youtube
        youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_player_fragment);

        //To check Favorite Status
        invalidateOptionsMenu();

        // Check if User came from ONLINE or OFFLINE to set data accordingly
        switch (from) {
            case ONLINE:
                FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                myReference = mDatabase.getReference();
                userKey = FirebaseAuth.getInstance().getUid();
                checkOrientation();
                movieTrailerKeys = new ArrayList<>();

                movieRateMovie.setOnClickListener(this);
                ratingArrayList = new ArrayList<>();
                commentArrayList = new ArrayList<>();
                key = new ArrayList<>();
                btnSendComment.setOnClickListener(this);

                retrieveMovieDetails(movieId);

                //Check for any issues in Youtube Players
                final YouTubeInitializationResult result = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this);
                if (result != YouTubeInitializationResult.SUCCESS) {
                    //If there are any issues we can show an error dialog.
                    result.getErrorDialog(this, 0).show();
                }

                LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                mRecyclerViewCast.setLayoutManager(layoutManager);
                mRecyclerViewCast.setHasFixedSize(true);
                castAdapter = new CastAdapter();
                mRecyclerViewCast.setAdapter(castAdapter);
                castAdapter.setCastAdapterOnClickHandler(DetailActivity.this);

                rateUsArrayAdapter = new RateUsArrayAdapter(DetailActivity.this, ratingArrayList);
                getRatingDataAndUpdate();

                commentAdapter = new CommentArrayAdapter(DetailActivity.this, commentArrayList);
                commentListView.setAdapter(commentAdapter);
                CheckCommentText();
                getCommentsAndUpdate();
                break;
            case FAVORITE:
                setOfflineMovieDataToViews();
                break;
            default:
                this.finish();
                break;
        }

        // Appbar Text Tittle Fade Animation
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                toolbar.setAlpha(1.0f - Math.abs(verticalOffset / (float) appBarLayout.getTotalScrollRange()));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_menu_detail_activity, menu);
        checkFavoriteState(menu);
        checkFavoriteState((int) movieId, menu);
        return true;
    }

    // Method to check Favorite Status
    private void checkFavoriteState(Menu menu) {
        if (isFavorite) {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_fill));
        } else {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_blank));
        }
    }

    // Override this method to do what you want when the menu is recreated
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.mMenu = menu;
        checkFavoriteState(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    // To insert or delete from favorite when item menu click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.item_menu_favorite: {
                if (isFavorite) {
                    isFavorite = false;
                    deleteFavoriteById((int) movieId);
                } else {
                    String imageByte = toByte(((BitmapDrawable) imgPoster.getDrawable()).getBitmap());
                    String tagLineString;
                    if (movieTagLine != null) {
                        tagLineString = movieTagLine.getText().toString();
                    } else {
                        tagLineString = "";
                    }
                    Favorite favorite = new Favorite((int) movieId,
                            movieTitle.getText().toString(),
                            movieReleaseDate.getText().toString(),
                            imageByte,
                            movieImbdRating.getText().toString(),
                            movieOverview.getText().toString(),
                            !isFavorite,
                            movieRating.getText().toString(),
                            tagLineString,
                            movieStatus.getText().toString(),
                            movieLanguage.getText().toString(),
                            movieDuration.getText().toString(),
                            movieBudget.getText().toString(),
                            movieGenres.getText().toString(),
                            movieRevenue.getText().toString(),
                            movieProductionCompanies.getText().toString(),
                            convertCommentArrayListToString(commentArrayList));
                    isFavorite = true;
                    insertFavorite(favorite);
                }
                checkFavoriteState(mMenu);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // Async Insert Favorite
    private void insertFavorite(Favorite favorite) {
        favoriteViewModel.insertFavorite(favorite, new AsyncTaskResponse<String>() {
            @Override
            public void processStart() {
                loadingDialog.showLoadingDialog();
            }

            @Override
            public void processFinish(String s) {
                utils.showToast(DetailActivity.this, s);
                loadingDialog.dismissLoadingDialog();
                UpdaterWidgetService.startActionGetAllFavoriteFromDb(DetailActivity.this);
            }
        });
    }

    // Async Delete Favorite
    private void deleteFavoriteById(int id) {
        favoriteViewModel.deleteFavoriteById(id, new AsyncTaskResponse<String>() {
            @Override
            public void processStart() {
                loadingDialog.showLoadingDialog();
            }

            @Override
            public void processFinish(String s) {
                utils.showToast(DetailActivity.this, s);
                loadingDialog.dismissLoadingDialog();
                UpdaterWidgetService.startActionGetAllFavoriteFromDb(DetailActivity.this);
            }
        });
    }

    // Check Favorite Initial State
    private void checkFavoriteState(int id, final Menu menu) {
        favoriteViewModel.getFavoriteLiveData(id).observe(this, new Observer<Favorite>() {
            @Override
            public void onChanged(@Nullable Favorite favorite) {
                if (favorite != null) {
                    isFavorite = favorite.getFavorite();
                } else {
                    isFavorite = false;
                }
                checkFavoriteState(menu);
            }
        });
    }

    // Helper method to convert image into to byte to save in database
    private static String toByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] image = stream.toByteArray();
        String encodedImage = Base64.encodeToString(image, Base64.DEFAULT);
        return encodedImage;
    }

    // Helper method to convert Comment Array list into String
    private String convertCommentArrayListToString(ArrayList<Comment> commentArrayList) {
        Gson gson = new Gson();
        return gson.toJson(commentArrayList);
    }

    // Helper method to convert back String to Array list of Comments
    private ArrayList<Comment> convertStringCommentsToArrayList(String commentString) {
        Gson gson = new Gson();
        return gson.fromJson(commentString, new TypeToken<List<Comment>>() {
        }.getType());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_detail_movie_rate_us: {
                showRatingDialog();
                break;
            }
            case R.id.btn_comment_send: {
                if (NetworkUtils.isNetworkAvailable(DetailActivity.this)) {
                    onClickCommentSend();
                } else {
                    utils.showToast(DetailActivity.this, getString(R.string.no_internet_connection_error));
                }

                break;
            }
        }
    }

    // Animation to slide up views
    public void slideUp(View view, int timeMilli) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                view.getHeight(),
                0);
        animate.setDuration(timeMilli);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // When There is new Comment it will Trigger Updated ArrayList and Show in the ListView
    private void getCommentsAndUpdate() {
        myReference.child(FIREBASE_PARENT_NODE_NAME).child(MOVIES_DATA).child(String.valueOf(movieId)).child(COMMENTS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Comment commentObj = dataSnapshot.getValue(Comment.class);
                commentArrayList.add(0, commentObj);
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // When There is new Ratings it will Trigger Updated ArrayList and Show in the ListView
    private void getRatingDataAndUpdate() {
        myReference.child(FIREBASE_PARENT_NODE_NAME).child(MOVIES_DATA).child(String.valueOf(movieId)).child(RATINGS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Rating ratingObj = dataSnapshot.getValue(Rating.class);
                ratingArrayList.add(0, ratingObj);
                String objectKey = dataSnapshot.getKey();
                key.add(objectKey);
                setUsersRating(ratingArrayList);
                rateUsArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                Rating ratingObj = dataSnapshot.getValue(Rating.class);
                String objectKey = dataSnapshot.getKey();
                int index = key.indexOf(objectKey);
                ratingArrayList.set(index, ratingObj);
                setUsersRating(ratingArrayList);
                rateUsArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Helper method to set User rating when offline
    private void setUsersRating(ArrayList<Rating> ratingArrayList) {
        if (ratingArrayList.size() != 0) {

            float rating = 0.0f;

            for (Rating ratingObj : ratingArrayList) {

                rating += ratingObj.getRating();
            }
            movieRating.setText(rating * 2 + "/10");
        } else {
            Log.e(TAG, "setUsersRating ratingArrayList = null");
        }
    }

    // Async Method call when user came from online to get its Detail's and show them
    private void retrieveMovieDetails(long id) {
        loadingDialog.showLoadingDialog();
        detailActivityViewModel.getMoveDetails(id).observe(this, new Observer<MovieDetails>() {
            @Override
            public void onChanged(@Nullable MovieDetails movieDetails) {
                if (movieDetails != null) {
                    setMovieDataToViews(movieDetails);
                } else {
                    loadingDialog.dismissLoadingDialog();
                    Log.e(TAG, "movieDetails is null");
                }
            }
        });
    }

    // Set Movie Data to its views when online
    @SuppressLint("SetTextI18n")
    private void setMovieDataToViews(MovieDetails movieDetailsObj) {

        movieTrailerKeys.addAll(getResultInString(movieDetailsObj.getVideos().getResults()));

        Glide.with(this)
                .asBitmap()
                .load(Constants.IMAGE_URL + movieDetailsObj.getPoster_path())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(imgPoster);

        slideUp(movieImbdRating, SLIDE_UP_MILI);
        movieImbdRating.setText(movieDetailsObj.getVote_average().toString());
        slideUp(movieTitle, SLIDE_UP_MILI);
        movieTitle.setText(movieDetailsObj.getTitle());
        if (movieDetailsObj.getTagline() != null) {
            slideUp(movieTagLine, SLIDE_UP_MILI);
            movieTagLine.setText(movieDetailsObj.getTagline());
        } else {
            movieTagLine.setVisibility(View.GONE);
        }
        slideUp(movieOverview, SLIDE_UP_MILI);
        movieOverview.setText(movieDetailsObj.getOverview());
        slideUp(movieStatus, SLIDE_UP_MILI);
        movieStatus.setText(movieDetailsObj.getStatus());
        slideUp(movieLanguage, SLIDE_UP_MILI);
        movieLanguage.setText(getStringFromSpokenLanguageList(movieDetailsObj.getSpoken_languages()));
        slideUp(movieDuration, SLIDE_UP_MILI);
        movieDuration.setText(movieDetailsObj.getRuntime() + getString(R.string.min_append));
        slideUp(movieBudget, SLIDE_UP_MILI);
        movieBudget.setText(getString(R.string.dollar_append) + movieDetailsObj.getBudget());
        slideUp(movieGenres, SLIDE_UP_MILI);
        movieGenres.setText(getStringFromGenres(movieDetailsObj.getGenres()));
        slideUp(movieRevenue, SLIDE_UP_MILI);
        movieRevenue.setText(getString(R.string.dollar_append) + movieDetailsObj.getRevenue());
        slideUp(movieReleaseDate, SLIDE_UP_MILI);
        movieReleaseDate.setText(movieDetailsObj.getRelease_date());
        slideUp(movieProductionCompanies, SLIDE_UP_MILI);
        movieProductionCompanies.setText(getStringFromProductionCompanies(movieDetailsObj.getProduction_companies()));
        youTubePlayerFragment.initialize(YOUTUBE_API_KEY, this);
        castAdapter.setCastData(getCastList(movieDetailsObj.getCredits().getCast()));
        loadingDialog.dismissLoadingDialog();
    }

    // Sets movie data to its view when offline
    @SuppressLint("SetTextI18n")
    private void setOfflineMovieDataToViews() {
        loadingDialog.showLoadingDialog();
        isFavorite = mFavorite.getFavorite();

        movieRateMovie.setVisibility(View.GONE);
        lineOne.setVisibility(View.GONE);
        lineTwo.setVisibility(View.GONE);
        castLabel.setVisibility(View.GONE);
        mRecyclerViewCast.setVisibility(View.GONE);
        commentContainer.setVisibility(View.GONE);
        youTubePlayerFragment.getView().setVisibility(View.GONE);

        movieTitle.setText(mFavorite.getTitle());
        slideUp(movieTitle, SLIDE_UP_MILI);
        movieReleaseDate.setText(mFavorite.getRelease_date());
        slideUp(movieReleaseDate, SLIDE_UP_MILI);

        imgPoster.setImageBitmap(utils.convertBase64ToBitmap(mFavorite.getImage()));

        movieImbdRating.setText(mFavorite.getVoteAverage());
        slideUp(movieImbdRating, SLIDE_UP_MILI);

        movieOverview.setText(mFavorite.getOverview());
        slideUp(movieOverview, SLIDE_UP_MILI);

        if (mFavorite.getMyRating() != null && !(mFavorite.getMyRating().equals(""))) {
            movieRating.setText(mFavorite.getMyRating());
            slideUp(movieRating, SLIDE_UP_MILI);
        } else {
            movieRating.setText(0 + "/10");
        }


        if (mFavorite.getTagLine() != null && !(mFavorite.getTagLine().equals(""))) {
            movieTagLine.setText(mFavorite.getTagLine());
            slideUp(movieTagLine, SLIDE_UP_MILI);
        } else {
            movieTagLine.setVisibility(View.GONE);
        }

        movieStatus.setText(mFavorite.getStatus());
        slideUp(movieStatus, SLIDE_UP_MILI);

        movieLanguage.setText(mFavorite.getSpokenLanguages());
        slideUp(movieLanguage, SLIDE_UP_MILI);

        movieDuration.setText(mFavorite.getRuntime());
        slideUp(movieDuration, SLIDE_UP_MILI);

        movieBudget.setText(mFavorite.getBudget());
        slideUp(movieBudget, SLIDE_UP_MILI);

        movieGenres.setText(mFavorite.getGenres());
        slideUp(movieGenres, SLIDE_UP_MILI);

        movieRevenue.setText(mFavorite.getRevenue());
        slideUp(movieRevenue, SLIDE_UP_MILI);

        movieProductionCompanies.setText(mFavorite.getProductionCompanies());
        slideUp(movieProductionCompanies, SLIDE_UP_MILI);

        commentArrayList = convertStringCommentsToArrayList(mFavorite.getComments());
        if (commentArrayList.size() == 0) {
            lineThree.setVisibility(View.GONE);
            labelComment.setVisibility(View.GONE);
        } else {
            commentAdapter = new CommentArrayAdapter(DetailActivity.this, commentArrayList);
            commentListView.setAdapter(commentAdapter);
        }
        loadingDialog.dismissLoadingDialog();
    }

    // Youtube Initialization Success Method
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        // add listeners to YouTubePlayer instance
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(playbackEventListener);
        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

        if (!wasRestored) {
            youTubePlayer.cueVideos(movieTrailerKeys);
        } else {
            if (isPlay) {
                if (fullScreenVideo) {
                    youTubePlayer.setFullscreen(true);
                } else {
                    youTubePlayer.setFullscreen(false);
                }
                playbackEventListener.onSeekTo(seek);
                youTubePlayer.play();
            }
        }
    }

    // Youtube Initialization Failure Method
    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    // Youtube Player Callback
    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onBuffering(boolean arg0) {
        }

        @Override
        public void onPaused() {
            isPlay = false;
        }

        @Override
        public void onPlaying() {
            isPlay = true;
        }

        @Override
        public void onSeekTo(int arg0) {
            seek = arg0;
        }

        @Override
        public void onStopped() {
        }
    };

    // Youtube player events perform
    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {

        @Override
        public void onAdStarted() {
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
        }

        @Override
        public void onLoaded(String arg0) {
        }

        @Override
        public void onLoading() {
        }

        @Override
        public void onVideoEnded() {
        }

        @Override
        public void onVideoStarted() {
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_PLAY, isPlay);
        outState.putLong(MOVIE_ID, movieId);
        outState.putBoolean(IS_FAVORITE, isFavorite);
    }

    // Send comment to Firebase Database
    private void onClickCommentSend() {
        String comment = commentField.getText().toString();

        Comment commentObj = new Comment(userName, comment, getDateInString());

        if (!comment.isEmpty()) {

            String generatedKey = myReference.push().getKey();
            commentObj.setKey(generatedKey);
            myReference.child(FIREBASE_PARENT_NODE_NAME).child(MOVIES_DATA).child(String.valueOf(movieId)).child(COMMENTS).child(generatedKey).setValue(commentObj);

            commentField.setText("");

        } else {
            commentField.setError(getString(R.string.please_type_some_comments));
        }
    }

    // Helper method to format current time into String
    private String getDateInString() {
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM MM dd", Locale.ENGLISH);
        return sdf.format(date);
    }

    // Enable Send button when there's text to send
    // Setting the Filter into Comment EditText
    private void CheckCommentText() {
        commentField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    btnSendComment.setEnabled(true);
                } else {
                    btnSendComment.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        commentField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});
    }

    // Pop up an Dialog Box which takes Rating
    private void showRatingDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        View promptsView = inflater.inflate(R.layout.rate_us_dialog, null);

        final RatingBar ratingBar = promptsView.findViewById(R.id.rate_us_dialog_rating_bar);
        final TextView ratingStr = promptsView.findViewById(R.id.rate_us_dialog_rating_in_Number);

        int customGoldActive = Color.parseColor("#FFC107");
        int customGoldBack = Color.parseColor("#fffde7");

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(1).setColorFilter(customGoldActive, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(2).setColorFilter(customGoldActive, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(customGoldBack, PorterDuff.Mode.SRC_ATOP);

        ListView rateUsListView = promptsView.findViewById(R.id.listViewRating);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(promptsView);

        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.colorAccent));
        String builderTitle = getResources().getString(R.string.rate_the_movie);

        // Initialize a new spannable string builder instance
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(builderTitle);

        // Apply the text color span
        ssBuilder.setSpan(
                foregroundColorSpan,
                0,
                builderTitle.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Set the alert dialog title using spannable string builder
        //builder.setTitle(ssBuilder);

        builder.setTitle(ssBuilder)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (NetworkUtils.isNetworkAvailable(DetailActivity.this)) {
                            float rating = ratingBar.getRating();

                            Rating ratingObj = new Rating(userName, rating);

                            if (rating != 0.0f) {
                                myReference.child(FIREBASE_PARENT_NODE_NAME).child(MOVIES_DATA).child(String.valueOf(movieId)).child(RATINGS).child(userKey).setValue(ratingObj);

                            } else {
                                utils.showToast(DetailActivity.this, getString(R.string.no_rating));
                            }
                        } else {
                            utils.showToast(DetailActivity.this, getString(R.string.no_internet_connection_error));
                        }
                    }
                })
                .setNegativeButton(R.string.notNow, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        rateUsListView.setAdapter(rateUsArrayAdapter);
        //builder.create().show();

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        ratingBar.setOnRatingBarChangeListener(
                new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        ratingStr.setText(String.valueOf(rating * 2));
                    }
                }
        );
    }

    // Helper method to check Screen Configuration for Youtube player
    private void checkOrientation() {
        int orientation = this.getResources().getConfiguration().orientation;
        fullScreenVideo = orientation != Configuration.ORIENTATION_PORTRAIT;
    }

    // Helper method to get Cast Array list filtered to show in ListView
    private ArrayList<Cast> getCastList(List<Cast> castList) {

        ArrayList<Cast> castArrayList = new ArrayList<>();
        for (Cast castObj : castList) {
            if (castObj.getProfile_path() != null)
                castArrayList.add(castObj);
        }
        return castArrayList;
    }

    // Helper method to get cast images Key
    private ArrayList<String> getResultInString(List<Results> arrayListResults) {
        ArrayList<String> stringKey = new ArrayList<>();
        for (Results results : arrayListResults) {
            stringKey.add(results.getKey());
        }
        return stringKey;
    }

    // Helper method to convert Language Array list into String for offline support
    private String getStringFromSpokenLanguageList(List<SpokenLanguage> arrayList) {
        ArrayList<String> arrayListString = new ArrayList<>();
        for (SpokenLanguage spokenLanguage : arrayList) {
            arrayListString.add(spokenLanguage.getName());
        }
        return android.text.TextUtils.join(", ", arrayListString);
    }

    // Helper method to convert Genre Array list into string for offline support
    private String getStringFromGenres(List<Genre> arrayList) {
        ArrayList<String> arrayListString = new ArrayList<>();
        for (Genre genre : arrayList) {
            arrayListString.add(genre.getGenres());
        }
        return android.text.TextUtils.join(", ", arrayListString);
    }

    // Helper method to convert production companies into String for offline support
    private String getStringFromProductionCompanies(List<ProductionCompanies> arrayList) {
        ArrayList<String> arrayListString = new ArrayList<>();
        for (ProductionCompanies productionCompanies : arrayList) {
            arrayListString.add(productionCompanies.getName());
        }
        return android.text.TextUtils.join(", ", arrayListString);
    }

    @Override
    public void onClick(Cast cast) {
        showCastFullInfoDialog(cast);
    }

    // Show Cast Full info dialog
    private void showCastFullInfoDialog(final Cast castObj) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.show_cast_info_dialog, null);
        dialog.setView(rootView);
        AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.AboutUsDialogTheme;
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final com.wang.avi.AVLoadingIndicatorView loadingIndicator = rootView.findViewById(R.id.loading_indicator_cast_info);
        loadingIndicator.smoothToShow();

        LinearLayout llBottomSheet = rootView.findViewById(R.id.bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        // set callback for changes
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        final ImageView castImage = rootView.findViewById(R.id.img_cast_info);
        final LinearLayout containerBirthday = rootView.findViewById(R.id.container_cast_info_birthday);
        final LinearLayout containerPlaceOfBirthday = rootView.findViewById(R.id.container_cast_info_place_of_birth);
        final TextView castName = rootView.findViewById(R.id.tv_cast_info_name);
        final TextView castBirthday = rootView.findViewById(R.id.tv_cast_info_birthday);
        final TextView castPlaceOfBirth = rootView.findViewById(R.id.tv_cast_info_place_of_birth);
        final TextView castBio = rootView.findViewById(R.id.tv_cast_info_bio);
        detailActivityViewModel.getCastInfo(castObj.getId()).observe(this, new Observer<CastInfo>() {
            @Override
            public void onChanged(@Nullable CastInfo castInfo) {

                Glide.with(DetailActivity.this)
                        .asBitmap()
                        .load(Constants.IMAGE_URL_CAST_INFO + castObj.getProfile_path())
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .apply(new RequestOptions().error(R.drawable.error_blank_image))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                super.onLoadFailed(errorDrawable);
                                loadingIndicator.smoothToHide();
                            }

                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                castImage.setImageBitmap(resource);
                                loadingIndicator.smoothToHide();
                            }
                        });

                castName.setText(castInfo.getName());
                if (castInfo.getBirthday() != null) {
                    castBirthday.setText(castInfo.getBirthday());
                } else {
                    containerBirthday.setVisibility(View.GONE);
                }
                if (castInfo.getPlace_of_birth() != null) {
                    castPlaceOfBirth.setText(castInfo.getPlace_of_birth());
                } else {
                    containerPlaceOfBirthday.setVisibility(View.GONE);
                }
                castBio.setText(castInfo.getBiography());

            }
        });
        alertDialog.show();

        utils.doKeepDialog(alertDialog);
    }
}
