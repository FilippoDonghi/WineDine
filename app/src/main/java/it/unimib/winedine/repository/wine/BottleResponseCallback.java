package it.unimib.winedine.repository.wine;

import java.util.List;

import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.WineAPIResponse;

public interface BottleResponseCallback {
    void onSuccessFromRemote(WineAPIResponse wineAPIResponse, long lastUpdate);
    void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal(List<Bottle> bottlesList);
    void onFailureFromLocal(Exception exception);
    void onWinesFavoriteStatusChanged(Bottle bottles, List<Bottle> favoriteBottles);
    void onWinesFavoriteStatusChanged(List<Bottle> bottles);
    void onDeleteFavoriteWinesSuccess(List<Bottle> favoriteBottles);
    void deleteFavoriteWines(Bottle bottle);
    void insertWine(Bottle bottle);
}
