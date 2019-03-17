package example.farhan.com.moviepocket.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

import example.farhan.com.moviepocket.model.BaseModel;
import example.farhan.com.moviepocket.model.CastInfo;
import example.farhan.com.moviepocket.model.MovieDetails;
import example.farhan.com.moviepocket.model.MovieShort;
import example.farhan.com.moviepocket.retrofit.RetrofitClient;
import example.farhan.com.moviepocket.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


// Main and Detail Activity Repository class which get data from REST API and send to their respective View model Async
public class MoviesRepository {

    private static final String TAG = "MoviesRepository";

    private static class SingletonHelper {
        private static final MoviesRepository INSTANCE = new MoviesRepository();
    }

    public static MoviesRepository getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public MoviesRepository() {
    }

    public LiveData<ArrayList<MovieShort>> getMovieLists(String sortBy) {
        final MutableLiveData<ArrayList<MovieShort>> data = new MutableLiveData<>();
        RetrofitClient.getInstance().getService().getMoviesShort(sortBy, Constants.API_KEY).enqueue(new Callback<BaseModel<MovieShort>>() {
            @Override
            public void onResponse(@NonNull Call<BaseModel<MovieShort>> call, @NonNull Response<BaseModel<MovieShort>> response) {
                if (response.body() != null) {
                    Collections.sort(response.body().getResults());
                    data.setValue(response.body().getResults());
                } else {
                    Log.e(TAG, "response.body() " + response.body());
                }
            }

            @Override
            public void onFailure(Call<BaseModel<MovieShort>> call, Throwable t) {
                Log.e(TAG, "onFailure: callSortByApi" + t.getMessage());
            }
        });
        return data;
    }

    public LiveData<ArrayList<MovieShort>> getSearchMoviesShort(String query) {
        final MutableLiveData<ArrayList<MovieShort>> data = new MutableLiveData<>();
        RetrofitClient.getInstance().getService().getSearchMoviesShort(Constants.API_KEY, query).enqueue(new Callback<BaseModel<MovieShort>>() {
            @Override
            public void onResponse(@NonNull Call<BaseModel<MovieShort>> call, @NonNull Response<BaseModel<MovieShort>> response) {
                if (response.body() != null) {
                    data.setValue(response.body().getResults());
                } else {
                    Log.e(TAG, "response.body() " + response.body());
                }
            }

            @Override
            public void onFailure(Call<BaseModel<MovieShort>> call, Throwable t) {
                Log.e(TAG, "onFailure: callSortByApi" + t.getMessage());
            }
        });
        return data;
    }

    public LiveData<ArrayList<MovieShort>> getMovieListsOnPagination(String sortBy, int page) {
        final MutableLiveData<ArrayList<MovieShort>> data = new MutableLiveData<>();
        RetrofitClient.getInstance().getService().getMoviesShortOnPagination(sortBy, Constants.API_KEY, page).enqueue(new Callback<BaseModel<MovieShort>>() {
            @Override
            public void onResponse(@NonNull Call<BaseModel<MovieShort>> call, @NonNull Response<BaseModel<MovieShort>> response) {
                if (response.body() != null) {
                    Collections.sort(response.body().getResults());
                    data.setValue(response.body().getResults());
                } else {
                    Log.e(TAG, "response.body() " + response.body());
                }
            }

            @Override
            public void onFailure(Call<BaseModel<MovieShort>> call, Throwable t) {
                Log.e(TAG, "onFailure: callSortByApi" + t.getMessage());
            }
        });
        return data;
    }

    public LiveData<MovieDetails> getMoviesDetails(long id) {
        final MutableLiveData<MovieDetails> data = new MutableLiveData<>();
        RetrofitClient.getInstance().getService().getMoviesDetails(id, Constants.API_KEY, Constants.APPEND_TO_VIDEO + "," + Constants.APPEND_TO_CREDIT).enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                if (response.body() != null) {
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "response.body() " + response.body());
                }
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                Log.e(TAG, "onFailure: callSortByApi" + t.getMessage());
            }
        });
        return data;
    }

    public LiveData<CastInfo> getCastInfo(long id) {
        final MutableLiveData<CastInfo> data = new MutableLiveData<>();
        RetrofitClient.getInstance().getService().getCastInfo(id, Constants.API_KEY).enqueue(new Callback<CastInfo>() {
            @Override
            public void onResponse(Call<CastInfo> call, Response<CastInfo> response) {
                if (response.body() != null) {
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "response.body() " + response.body());
                }
            }

            @Override
            public void onFailure(Call<CastInfo> call, Throwable t) {
                Log.e(TAG, "onFailure: callSortByApi" + t.getMessage());
            }
        });

        return data;
    }
}
