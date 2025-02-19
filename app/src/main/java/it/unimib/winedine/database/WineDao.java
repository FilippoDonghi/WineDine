package it.unimib.winedine.database;

import androidx.room.Dao;
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
        void insertAll(List<Bottle> bottles);

        @Query("DELETE FROM Bottle")
        void clearAll();
    }

