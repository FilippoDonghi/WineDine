package it.unimib.winedine.source.pairing;

import androidx.lifecycle.LiveData;

import it.unimib.winedine.model.PairingAPIResponse;
import it.unimib.winedine.repository.pairing.PairingResponseCallback;
import it.unimib.winedine.repository.wine.BottleResponseCallback;

public abstract class BasePairingRemoteDataSource {
    protected PairingResponseCallback responseCallback;

    public void setPairingCallback(PairingResponseCallback responseCallback) {
        this.responseCallback = responseCallback;
    }

    public abstract void getPairingAndRecipes(String wine);
    public abstract void getRecipesForPairings(String[] ingredients);
}


