package it.unimib.winedine.repository.wine;

import java.util.List;

import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.BottleAPIResponse;

public interface BottleResponseCallback {
    void onSuccessFromRemote(BottleAPIResponse bottleAPIResponse, long lastUpdate);
    void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal(List<Bottle> bottlesList);
    void onFailureFromLocal(Exception exception);
    void onNewsFavoriteStatusChanged(Bottle bottles, List<Bottle> favoriteBottles);
    void onNewsFavoriteStatusChanged(List<Bottle> bottles);
    void onDeleteFavoriteNewsSuccess(List<Bottle> favoriteBottles);
}
