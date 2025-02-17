package it.unimib.winedine.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it.unimib.winedine.model.Wine;

    @Dao
    public interface WineDao {

        @Query("SELECT * FROM wine WHERE category = :category")
        List<Wine> getWinesByCategory(String category);


        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertAll(List<Wine> wines);

        @Query("DELETE FROM wine")
        void clearAll();
    }

