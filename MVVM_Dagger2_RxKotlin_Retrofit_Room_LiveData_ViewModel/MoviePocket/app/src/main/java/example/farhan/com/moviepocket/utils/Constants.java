package example.farhan.com.moviepocket.utils;

import example.farhan.com.moviepocket.BuildConfig;

// Constant Class to keep every Class Clean
public class Constants {

    private Constants() {
    }

    public static final String STATIC_MOVIES_BASE_URL = "https://api.themoviedb.org/3/";
    public static final String YOUTUBE_API_KEY = "AIzaSyAyVirgyaPoZXHrNq1NSd1FXoxsgnl5_Hc";
    public static final String API_KEY = BuildConfig.THE_MOVIE_DB_API_KEY;
    public static final String POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";
    public static final String MOVIE_ID = "movie_id";
    public static final String USER_NAME = "userName";
    public static final String MOVIE_NAME = "movieName";
    static final String PREF_KEY_USER_NAME = "userNameKey";
    public static final String FAVORITE = "favorite";
    public static final String IS_FAVORITE = "IS_FAVORITE";
    public static final String FAVORITE_OBJECT = "favoriteObject";
    public static final String UPCOMING = "upcoming";
    public static final String SORT_BY = "sort_by";
    public static final String IS_PLAY = "isPlay";
    public static final String SEARCH = "search";
    public static final String SEARCH_QUERY = "search_query";
    public static final String IMAGE_URL = "http://image.tmdb.org/t/p/w185";
    public static final String IMAGE_URL_CAST = "http://image.tmdb.org/t/p/w154/";
    public static final String IMAGE_URL_CAST_INFO = "http://image.tmdb.org/t/p/w780/";
    public static final String APPEND_TO_VIDEO = "videos";
    public static final String APPEND_TO_CREDIT = "credits";
    public static final String PROFILE_FRAGMENT_TAG = "ProfileFragment";
    public static final String REFERENCE_USERS = "Users";
    public static final String FIREBASE_PARENT_NODE_NAME = "Movie Pocket";
    public static final String MOVIES_DATA = "Movies";
    public static final String RATINGS = "Ratings";
    public static final String COMMENTS = "Comments";
    public static final String FROM = "from";
    public static final String ONLINE = "online";
    public static final String RUN_FOR_MOVIE = "movie";
    public static final String RUN_FOR_FAVORITE = "favorite";
    public static final int SLIDE_UP_MILI = 1000;

}
