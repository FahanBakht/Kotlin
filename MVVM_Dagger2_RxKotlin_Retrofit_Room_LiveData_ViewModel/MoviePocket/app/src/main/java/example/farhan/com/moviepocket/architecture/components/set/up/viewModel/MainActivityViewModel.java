package example.farhan.com.moviepocket.architecture.components.set.up.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import example.farhan.com.moviepocket.model.MovieShort;
import example.farhan.com.moviepocket.repository.MoviesRepository;

// View Model class for Main Activity load and get data with Live Data Observer's
public class MainActivityViewModel extends AndroidViewModel {

    private LiveData<ArrayList<MovieShort>> movieArrayList;
    private LiveData<ArrayList<MovieShort>> movieArrayListSearch;
    private LiveData<ArrayList<MovieShort>> movieArrayListOnPagination;
    private MoviesRepository moviesRepository;
    private int currentPage = 0;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        moviesRepository = MoviesRepository.getInstance();
    }

    public LiveData<ArrayList<MovieShort>> getMoviesList(String sortBy) {

        if (movieArrayList == null) {
            movieArrayList = loadMovies(sortBy);
        }
        return movieArrayList;
    }

    public LiveData<ArrayList<MovieShort>> getSearchMovies(String query) {

        if (movieArrayListSearch == null) {
            movieArrayListSearch = loadSearchMovies(query);
        }
        return movieArrayListSearch;
    }

    public LiveData<ArrayList<MovieShort>> getMoviesListOnPagination(String sortBy, int page) {
        if (currentPage < page) {
            movieArrayListOnPagination = loadMoviesOnPagination(sortBy, page);
            currentPage = page;
        }
        return movieArrayListOnPagination;
    }

    private LiveData<ArrayList<MovieShort>> loadMovies(String sortBy) {
        return moviesRepository.getMovieLists(sortBy);
    }

    private LiveData<ArrayList<MovieShort>> loadSearchMovies(String query) {
        return moviesRepository.getSearchMoviesShort(query);
    }

    private LiveData<ArrayList<MovieShort>> loadMoviesOnPagination(String sortBy, int page) {
        return moviesRepository.getMovieListsOnPagination(sortBy, page);
    }

    public void clearMoviesList() {
        this.movieArrayList = null;
    }

    public void clearSearchMovieList() {
        this.movieArrayListSearch = null;
    }
}
