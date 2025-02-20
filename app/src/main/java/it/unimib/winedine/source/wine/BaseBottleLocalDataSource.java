package it.unimib.winedine.source.wine;

import java.util.List;

import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.repository.wine.BottleResponseCallback;

public abstract class  BaseBottleLocalDataSource {

    protected BottleResponseCallback responseCallback;

   public void setBottleCallback(BottleResponseCallback responseCallback) {
       this.responseCallback = responseCallback;
   }

    public abstract void getWines();

    public abstract void insertWines(List<Bottle> bottleList);
}
