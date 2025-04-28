package it.unimib.winedine.source.pairing;

import it.unimib.winedine.repository.pairing.DishCallback;

public abstract class BaseDishRemoteDataSource {
    protected DishCallback responseCallback;

    public void setDishCallback(DishCallback responseCallback) {
        this.responseCallback = responseCallback;
    }
    public abstract void getDishes(int id);
}
