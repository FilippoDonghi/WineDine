package it.unimib.winedine.repository.pairing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import it.unimib.winedine.model.DishAPIResponse;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.source.pairing.BaseDishRemoteDataSource;

public class DishRepository {
    private final BaseDishRemoteDataSource remoteDataSource;

    public DishRepository(BaseDishRemoteDataSource remoteDataSource) {
        this.remoteDataSource = remoteDataSource;
    }


    public LiveData<Result> getDish(int id) {
        MutableLiveData<Result> dishLiveData = new MutableLiveData<>();
        // Imposto una callback “ad hoc” che posta su questo liveData
        remoteDataSource.setDishCallback(new DishCallback() {
            @Override
            public void onDishSuccess(DishAPIResponse response) {
                dishLiveData.postValue(new Result.DishSuccess(response));
            }
            @Override
            public void onFailure(Exception exception) {
                dishLiveData.postValue(new Result.Error(exception.getMessage()));
            }
        });
        remoteDataSource.getDishes(id);
        return dishLiveData;
    }
}
