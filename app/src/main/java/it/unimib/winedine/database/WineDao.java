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
    public interface WineDao {

    @Query("SELECT * FROM Bottle")
    List<Bottle> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Bottle... bottles);

    @Insert
    void insertAll(List<Bottle> bottles);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertBottlesList(List<Bottle> bottlesList);

    @Delete
    void delete(Bottle bottle);

    @Query("DELETE FROM Bottle")
    void clearAll();

    @Query("SELECT * FROM Bottle WHERE uid = :id")
    Bottle getBottle(long id);

    @Query("SELECT * FROM Bottle WHERE selectedWine = :wineType")
    List<Bottle> getBottlesBySelectedWine(String wineType);

    @Query("SELECT * FROM Bottle WHERE liked = 1")
    List<Bottle> getLiked();

    @Update
    int updateBottle(Bottle bottle);

    @Query("UPDATE Bottle SET liked = :liked WHERE uid = :id")
    int updateLikedStatus(long id, boolean liked);

    @Query("DELETE from Bottle WHERE liked = 0")
    void deleteCached();

    @Update
    int updateListFavoriteBottle(List<Bottle> bottles);


    @Delete
    void deleteWine(Bottle bottle);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertBottle(Bottle bottle);
    }



