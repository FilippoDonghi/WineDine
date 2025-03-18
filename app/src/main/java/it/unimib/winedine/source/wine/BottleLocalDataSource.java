package it.unimib.winedine.source.wine;

import static it.unimib.winedine.util.Constants.UNEXPECTED_ERROR;

import java.util.List;

import it.unimib.winedine.database.WineDao;
import it.unimib.winedine.database.WineRoomDatabase;
import it.unimib.winedine.model.Bottle;

public class BottleLocalDataSource extends BaseBottleLocalDataSource {

    private final WineDao wineDao;

    public BottleLocalDataSource(WineRoomDatabase winesRoomDatabase) {
        this.wineDao = winesRoomDatabase.wineDao();
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
                    }
                }


                List<Long> insertedWinesIds = wineDao.insertBottlesList(bottleList);
                for (int i = 0; i < bottleList.size(); i++) {
                    bottleList.get(i).setUid(insertedWinesIds.get(i));
                }

                responseCallback.onSuccessFromLocal(bottleList);
            }
        });
    }

    @Override
    public void updateWine(Bottle bottle) {
        WineRoomDatabase.databaseWriteExecutor.execute(() -> {
            int rowUpdatedCounter = wineDao.updateBottle(bottle);

            // It means that the update succeeded because only one row had to be updated
            if (rowUpdatedCounter == 1) {
                Bottle updatedBottle = wineDao.getBottle(bottle.getUid());
                responseCallback.onWinesFavoriteStatusChanged(updatedBottle, wineDao.getLiked());
            } else {
                responseCallback.onFailureFromLocal(new Exception(UNEXPECTED_ERROR));
            }
        });
    }
}

