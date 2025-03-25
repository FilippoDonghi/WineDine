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
    public void getWines() {
        WineRoomDatabase.databaseWriteExecutor.execute(() -> {
            responseCallback.onSuccessFromLocal(wineDao.getAll());
        });
    }

    @Override
    public void getFavoriteWines() {
        WineRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Bottle> favoriteBottle = wineDao.getLiked();
            responseCallback.onWinesFavoriteStatusChanged(favoriteBottle);
        });
    }

    @Override
    public void insertWines(List<Bottle> bottleList) {
        WineRoomDatabase.databaseWriteExecutor.execute(() -> {
            // Reads the news from the database
            List<Bottle> allBottles = wineDao.getAll();

            if (bottleList != null) {

                for (Bottle bottle : bottleList) {
                    if (bottleList.contains(bottle)) {
                        bottleList.set(bottleList.indexOf(bottle), bottle);
                    }}
                List<Long> insertedWinesIds = wineDao.insertBottlesList(bottleList);
                for (int i = 0; i < bottleList.size(); i++) {
                    bottleList.get(i).setUid(insertedWinesIds.get(i));
                }
                responseCallback.onSuccessFromLocal(bottleList);}
        });
    }

    @Override
    public void updateWine(Bottle bottle) {
       /* WineRoomDatabase.databaseWriteExecutor.execute(() -> {
            Log.d("DB_UPDATE", "Tentativo di aggiornare bottle con ID: " + bottle.getUid() + " a liked = " + bottle.getLiked());

            int rowUpdatedCounter = wineDao.updateLikedStatus(bottle.getUid(), bottle.getLiked());

            Log.d("DB_UPDATE", "Righe aggiornate: " + rowUpdatedCounter);

            if (rowUpdatedCounter > 0) {
                Bottle updatedBottle = wineDao.getBottle(bottle.getUid());
                Log.d("DB_UPDATE", "Bottiglia aggiornata nel DB: " + updatedBottle.getLiked());

                responseCallback.onWinesFavoriteStatusChanged(updatedBottle, wineDao.getLiked());
            } else {
                Log.e("DB_UPDATE", "Errore: nessuna riga aggiornata");
                responseCallback.onFailureFromLocal(new Exception("Errore nell'aggiornamento del 'liked'"));
            }
        });*/
        WineRoomDatabase.databaseWriteExecutor.execute(() -> {
            int rowUpdatedCounter = wineDao.updateBottle(bottle);
            if(rowUpdatedCounter == 1){
                Bottle updatedBottle = wineDao.getBottle(bottle.getUid());
                responseCallback.onWinesFavoriteStatusChanged(updatedBottle, wineDao.getLiked());
            } else {
                responseCallback.onFailureFromLocal(new Exception(UNEXPECTED_ERROR));
            }
        });
    }

    @Override
    public void deleteFavoriteWines() {
        WineRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Bottle> favoriteWines = wineDao.getLiked();
            for (Bottle article : favoriteWines) {
                article.setLiked(false);
            }
            int updatedRowsNumber = wineDao.updateListFavoriteBottle(favoriteWines);

            if (updatedRowsNumber == favoriteWines.size()) {
                responseCallback.onDeleteFavoriteWinesSuccess(favoriteWines);
            } else {
               responseCallback.onFailureFromLocal(new Exception(UNEXPECTED_ERROR));
            }
        });
    }

}

