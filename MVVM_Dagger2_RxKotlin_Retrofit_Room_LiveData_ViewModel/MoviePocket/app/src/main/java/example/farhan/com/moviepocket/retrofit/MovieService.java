package example.farhan.com.moviepocket.retrofit;

import example.farhan.com.moviepocket.model.BaseModel;
import example.farhan.com.moviepocket.model.CastInfo;
import example.farhan.com.moviepocket.model.MovieDetails;
import example.farhan.com.moviepocket.model.MovieShort;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

// Service class for retrofit calls
public interface MovieService {

    @GET("movie/{sort_by}")
    Call<BaseModel<MovieShort>> getMoviesShort(@Path("sort_by") String sortBy, @Query("api_key") String apiKey);

    @GET("movie/{sort_by}")
    Call<BaseModel<MovieShort>> getMoviesShortOnPagination(@Path("sort_by") String sortBy, @Query("api_key") String apiKey, @Query("page") int page);

    @GET("movie/{movie_id}")
    Call<MovieDetails> getMoviesDetails(@Path("movie_id") long id, @Query("api_key") String apiKey, @Query("append_to_response") String appendTo);

    @GET("person/{person_id}")
    Call<CastInfo> getCastInfo(@Path("person_id") long personId, @Query("api_key") String apiKey);

    @GET("search/movie")
    Call<BaseModel<MovieShort>> getSearchMoviesShort(@Query("api_key") String apiKey, @Query("query") String query);

}
