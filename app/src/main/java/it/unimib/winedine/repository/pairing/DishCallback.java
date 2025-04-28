package it.unimib.winedine.repository.pairing;

import it.unimib.winedine.model.DishAPIResponse;

public interface DishCallback {
    void onDishSuccess(DishAPIResponse response);
    void onFailure(Exception e);
}
