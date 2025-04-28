package it.unimib.winedine.repository.pairing;

import androidx.lifecycle.MutableLiveData;

import it.unimib.winedine.model.DishAPIResponse;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.source.pairing.BaseDishRemoteDataSource;

public class DishRepository implements DishCallback {
    private final BaseDishRemoteDataSource remoteDataSource;
    private final MutableLiveData<Result> dishLiveData = new MutableLiveData<>();

    public DishRepository(BaseDishRemoteDataSource remoteDataSource) {
        this.remoteDataSource = remoteDataSource;
        this.remoteDataSource.setDishCallback(this);
    }

    public MutableLiveData<Result> getDish(int id) {
        remoteDataSource.getDishes(id);
        return dishLiveData;
    }

    @Override
    public void onDishSuccess(DishAPIResponse response) {
        dishLiveData.postValue(new Result.DishSuccess(response));
    }

    @Override
    public void onFailure(Exception exception) {
        dishLiveData.postValue(new Result.Error(exception.getMessage()));
    }
}
