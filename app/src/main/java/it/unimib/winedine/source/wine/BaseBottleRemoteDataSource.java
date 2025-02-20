package it.unimib.winedine.source.wine;

import it.unimib.winedine.repository.wine.BottleResponseCallback;

public abstract class BaseBottleRemoteDataSource {
    protected BottleResponseCallback responseCallback;

    public void setBottleCallback(BottleResponseCallback responseCallback) {
        this.responseCallback = responseCallback;
    }

   public abstract void getWines(String wine);
}