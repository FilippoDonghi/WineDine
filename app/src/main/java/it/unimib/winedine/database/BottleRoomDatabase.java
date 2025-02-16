package it.unimib.winedine.database;

import static it.unimib.winedine.util.Constants.*;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.util.Constants;

@Database(entities = {Bottle.class}, version = DATABASE_VERSION)
    public abstract class BottleRoomDatabase extends RoomDatabase {

        public abstract BottleDao bottleDao();

        private static volatile BottleRoomDatabase INSTANCE;
        public static final ExecutorService databaseWriteExecutor =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        public static BottleRoomDatabase getDatabase(final Context context) {
            if (INSTANCE == null) {
                synchronized (BottleRoomDatabase.class) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                        BottleRoomDatabase.class, Constants.SAVED_BOTTLES_DATABASE)
                                .allowMainThreadQueries().build();
                    }
                }
            }
            return INSTANCE;
        }
    }

