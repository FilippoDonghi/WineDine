package it.unimib.winedine.repository.pairing;

import java.util.List;

import it.unimib.winedine.model.PairingAPIResponse;
import it.unimib.winedine.model.Recipe;
import it.unimib.winedine.model.WineAPIResponse;

public interface PairingResponseCallback {

    void onPairingSuccess(PairingAPIResponse pairingResponse, long lastUpdate);
    void onRecipeSuccess(List<Recipe> recipes);
    void onFailure(Exception exception);
    void onAllRequestsCompleted(List<Recipe> aggregatedRecipes);
}
