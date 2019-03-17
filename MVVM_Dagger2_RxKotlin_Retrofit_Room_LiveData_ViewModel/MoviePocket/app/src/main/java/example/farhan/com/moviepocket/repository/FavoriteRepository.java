package example.farhan.com.moviepocket.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutionException;

import example.farhan.com.moviepocket.architecture.components.set.up.AppDatabase;
import example.farhan.com.moviepocket.interfaces.AsyncTaskResponse;
import example.farhan.com.moviepocket.model.Favorite;

// Favorite Repository class which get data from database and send to Favorite View model Async
public class FavoriteRepository {

    private static final String TAG = "FavoriteRepository";
    private static AppDatabase appDatabase;
    private static LiveData<List<Favorite>> mListLiveData;
    private static LiveData<Favorite> mLiveData;

    private static class SingletonHelper {
        private static final FavoriteRepository INSTANCE = new FavoriteRepository();
    }

    FavoriteRepository() {
    }

    public static FavoriteRepository getInstance(Context context) {
        appDatabase = AppDatabase.getInstance(context);

        return FavoriteRepository.SingletonHelper.INSTANCE;
    }


    public DeleteFavoriteById gDeleteFavoriteById(AsyncTaskResponse<String> asyncTaskResponse) {

        return new DeleteFavoriteById(asyncTaskResponse);
    }

    public static class DeleteFavoriteById extends AsyncTask<Integer, Void, String> {
        private AsyncTaskResponse<String> asyncTaskResponse;

        private DeleteFavoriteById(AsyncTaskResponse<String> asyncTaskResponse) {
            this.asyncTaskResponse = asyncTaskResponse;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            asyncTaskResponse.processStart();
        }

        @Override
        protected String doInBackground(Integer... integers) {
            appDatabase.favoriteDao().delete(integers[0]);
            return "Deleted from Favorites";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            asyncTaskResponse.processFinish(s);
        }
    }

    public LiveData<Favorite> getFavById(int param) {
        try {
            mLiveData = new GetFavoriteById().execute(param).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "InterruptedException " + e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.e(TAG, "ExecutionException " + e.getMessage());
        }
        return mLiveData;
    }

    public static class GetFavoriteById extends AsyncTask<Integer, Void, LiveData<Favorite>> {

        private GetFavoriteById() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected LiveData<Favorite> doInBackground(Integer... integers) {
            return appDatabase.favoriteDao().loadFavoriteById(integers[0]);

        }

        @Override
        protected void onPostExecute(LiveData<Favorite> favoriteLiveData) {
            super.onPostExecute(favoriteLiveData);
        }
    }

    public InsertFavoriteAsync getInsertFavoriteAsync(AsyncTaskResponse<String> asyncTaskResponse) {
        return new InsertFavoriteAsync(asyncTaskResponse);
    }

    public static class InsertFavoriteAsync extends AsyncTask<Favorite, Void, String> {

        private AsyncTaskResponse<String> asyncTaskResponse;

        private InsertFavoriteAsync(AsyncTaskResponse<String> asyncTaskResponse) {
            this.asyncTaskResponse = asyncTaskResponse;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            asyncTaskResponse.processStart();
        }

        @Override
        protected String doInBackground(Favorite... favorites) {
            appDatabase.favoriteDao().insertFavorite(favorites[0]);
            return "Inserted to Favorites";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            asyncTaskResponse.processFinish(s);
        }
    }

    public LiveData<List<Favorite>> getAllFavFirstTime() {
        try {
            mListLiveData = new GetAllFavoritesAsync().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "InterruptedException " + e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.e(TAG, "ExecutionException " + e.getMessage());
        }
        return mListLiveData;
    }

    public static class GetAllFavoritesAsync extends AsyncTask<Void, Void, LiveData<List<Favorite>>> {

        private GetAllFavoritesAsync() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected LiveData<List<Favorite>> doInBackground(Void... voids) {
            mListLiveData = appDatabase.favoriteDao().loadAllFavorites();
            return mListLiveData;
        }

        @Override
        protected void onPostExecute(LiveData<List<Favorite>> listLiveData) {
            super.onPostExecute(listLiveData);
        }
    }
}
