package example.farhan.com.moviepocket.architecture.components.set.up;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import example.farhan.com.moviepocket.model.Favorite;

@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM favorites")
    LiveData<List<Favorite>> loadAllFavorites();

    @Insert
    void insertFavorite(Favorite favorite);

    @Query("SELECT * FROM favorites WHERE id = :id")
    LiveData<Favorite> loadFavoriteById(int id);

    @Query("DELETE FROM favorites WHERE id = :id")
    void delete(int id);

    @Query("SELECT * FROM favorites")
    List<Favorite> getAllFavoriteForWidget();
}
