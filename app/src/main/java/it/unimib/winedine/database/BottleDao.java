package it.unimib.winedine.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import it.unimib.winedine.model.Bottle;

@Dao
public interface BottleDao {
    @Query("SELECT * FROM Bottle")
    List<Bottle> getAll();

    @Query("SELECT * FROM Bottle WHERE uid = :id")
    Bottle getBottle(long id);

    @Query("SELECT * FROM Bottle WHERE liked = 1")
    List<Bottle> getLiked();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Bottle... recommendedWines);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Bottle> recommendedWines);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertBottlesList(List<Bottle> recommendedWines);

    @Update
    int updateArticle(Bottle bottle);

    @Update
    int updateListFavoriteArticles(List<Bottle> recommendedWines);

    @Delete
    void delete(Bottle bottle);

    @Query("DELETE from Bottle WHERE liked = 0")
    void deleteCached();

    @Query("DELETE from Bottle")
    void deleteAll();
}


