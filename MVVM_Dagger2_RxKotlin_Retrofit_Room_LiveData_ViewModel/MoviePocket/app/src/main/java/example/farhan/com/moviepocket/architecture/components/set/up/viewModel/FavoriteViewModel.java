package example.farhan.com.moviepocket.architecture.components.set.up.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import example.farhan.com.moviepocket.interfaces.AsyncTaskResponse;
import example.farhan.com.moviepocket.model.Favorite;
import example.farhan.com.moviepocket.repository.FavoriteRepository;

// View Model class for Favorite to load and get data with Live Data Observer's
public class FavoriteViewModel extends AndroidViewModel {

    private FavoriteRepository favoriteRepository;
    private LiveData<List<Favorite>> listLiveData;
    private LiveData<Favorite> mLiveData;

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        favoriteRepository = FavoriteRepository.getInstance(application);
        listLiveData = favoriteRepository.getAllFavFirstTime();
    }

    public LiveData<List<Favorite>> getAllFavorite() {
        if (listLiveData == null) {
            listLiveData = favoriteRepository.getAllFavFirstTime();
        }
        return listLiveData;
    }

    public LiveData<Favorite> getFavoriteLiveData(int id) {
        if (mLiveData == null) {
            mLiveData = favoriteRepository.getFavById(id);
        }
        return mLiveData;
    }

    public void insertFavorite(Favorite favorite, AsyncTaskResponse<String> stringAsyncTaskResponse) {
        FavoriteRepository.InsertFavoriteAsync insertFavoriteAsync = favoriteRepository.getInsertFavoriteAsync(stringAsyncTaskResponse);
        insertFavoriteAsync.execute(favorite);
    }

    public void deleteFavoriteById(int id, AsyncTaskResponse<String> stringAsyncTaskResponse) {
        FavoriteRepository.DeleteFavoriteById gDeleteFavoriteById = favoriteRepository.gDeleteFavoriteById(stringAsyncTaskResponse);
        gDeleteFavoriteById.execute(id);
    }

}
