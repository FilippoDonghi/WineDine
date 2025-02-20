package it.unimib.winedine.source.wine;

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
    }

