package it.unimib.winedine.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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
    }

