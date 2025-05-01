package it.unimib.winedine.source.wine;

import static it.unimib.winedine.util.Constants.UNEXPECTED_ERROR;

import android.util.Log;

import java.util.List;

import it.unimib.winedine.database.WineDao;
import it.unimib.winedine.database.WineRoomDatabase;
import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.util.SharedPreferencesUtils;

public class BottleLocalDataSource extends BaseBottleLocalDataSource {

    private final WineDao wineDao;
    private final SharedPreferencesUtils sharedPreferencesUtil;

    public BottleLocalDataSource(WineRoomDatabase winesRoomDatabase, SharedPreferencesUtils sharedPreferencesUtil) {
        this.wineDao = winesRoomDatabase.wineDao();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }

    @Override
    public void getFavoriteWines() {
        WineRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Bottle> favoriteBottle = wineDao.getLiked();
            responseCallback.onWinesFavoriteStatusChanged(favoriteBottle);
        });
    }
    @Override
    public void insertBottle(Bottle bottle) {
        WineRoomDatabase.databaseWriteExecutor.execute(() -> {
            try {
                long id = wineDao.insertBottle(bottle);
                bottle.setUid(id);
                Log.d("DB_INSERT", "Inserita bottle con ID: " + id + " e liked = " + bottle.getLiked());
                responseCallback.onSuccessFromLocal(List.of(bottle));
            } catch (Exception e) {
                Log.e("DB_INSERT", "Errore durante l'inserimento della bottle", e);
                responseCallback.onFailureFromLocal(e);
            }
        });
    }

    @Override
    public void insertWines(List<Bottle> bottleList) {
        WineRoomDatabase.databaseWriteExecutor.execute(() -> {
            // Reads the news from the database
            List<Bottle> allBottles = wineDao.getAll();

            if (bottleList != null) {
                List<Long> insertedWinesIds = wineDao.insertBottlesList(bottleList);
                for (int i = 0; i < bottleList.size(); i++) {
                    bottleList.get(i).setUid(insertedWinesIds.get(i));
                }
                responseCallback.onSuccessFromLocal(bottleList);}
        });
    }

    @Override
    public void updateWine(Bottle bottle) {
        WineRoomDatabase.databaseWriteExecutor.execute(() -> {
            Log.d("DB_UPDATE", "Tentativo di aggiornare bottle con ID: " + bottle.getUid() + " a liked = " + bottle.getLiked());
            int rowUpdatedCounter = wineDao.updateBottle(bottle);
            if(rowUpdatedCounter == 1){

                Bottle updatedBottle = wineDao.getBottle(bottle.getUid());
                Log.d("DB_UPDATE", "Bottiglia aggiornata nel DB: " + updatedBottle.getLiked());
                responseCallback.onWinesFavoriteStatusChanged(updatedBottle, wineDao.getLiked());
            } else {
                Log.e("DB_UPDATE", "Errore: nessuna riga aggiornata");
                responseCallback.onFailureFromLocal(new Exception(UNEXPECTED_ERROR));
            }
        });
    }

    public void deleteFavoriteWines(Bottle bottle) {
        WineRoomDatabase.databaseWriteExecutor.execute(() -> {
            wineDao.deleteWine(bottle);
            responseCallback.onWinesFavoriteStatusChanged(bottle, wineDao.getLiked());
        });
    }
}

