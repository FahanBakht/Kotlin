package example.farhan.com.moviepocket.retrofit;

import example.farhan.com.moviepocket.utils.Constants;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Retrofit Singleton
public class RetrofitClient {

    private static RetrofitClient retrofitClient;
    private Retrofit mRetrofit;
    private  MovieService movieService;

    private RetrofitClient(){
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.STATIC_MOVIES_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        movieService = mRetrofit.create(MovieService.class);
    }

    public static RetrofitClient getInstance() {
        if (retrofitClient == null) {
            retrofitClient = new RetrofitClient();
        }

        return retrofitClient;
    }

    public MovieService getService() {
        return movieService;
    }
}
