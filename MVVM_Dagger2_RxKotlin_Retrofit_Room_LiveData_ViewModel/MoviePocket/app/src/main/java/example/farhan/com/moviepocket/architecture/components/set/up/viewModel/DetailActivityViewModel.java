package example.farhan.com.moviepocket.architecture.components.set.up.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import example.farhan.com.moviepocket.model.CastInfo;
import example.farhan.com.moviepocket.model.MovieDetails;
import example.farhan.com.moviepocket.repository.MoviesRepository;

// View Model class for Detail Activity to load and get data with Live Data Observer's
public class DetailActivityViewModel extends AndroidViewModel {

    private LiveData<MovieDetails> movieDetailsLiveData;
    private MoviesRepository moviesRepository;
    private LiveData<CastInfo> castInfo;

    public DetailActivityViewModel(@NonNull Application application) {
        super(application);
        moviesRepository = MoviesRepository.getInstance();
    }

    public LiveData<MovieDetails> getMoveDetails(long id) {
        if (movieDetailsLiveData == null) {
            movieDetailsLiveData = loadMoveDetails(id);
        }
        return movieDetailsLiveData;
    }

    public LiveData<CastInfo> getCastInfo(long id) {
        if (castInfo == null) {
            castInfo = loadCastInfo(id);
        }
        return castInfo;
    }

    private LiveData<MovieDetails> loadMoveDetails(long id) {
        return moviesRepository.getMoviesDetails(id);
    }

    private LiveData<CastInfo> loadCastInfo(long id) {
        return moviesRepository.getCastInfo(id);
    }
}
