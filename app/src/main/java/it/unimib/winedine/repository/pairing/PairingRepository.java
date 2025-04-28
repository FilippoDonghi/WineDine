package it.unimib.winedine.repository.pairing;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import it.unimib.winedine.model.DishAPIResponse;
import it.unimib.winedine.model.PairingAPIResponse;
import it.unimib.winedine.model.Recipe;
import it.unimib.winedine.model.RecipeAPIResponse;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.service.WineAPIService;
import it.unimib.winedine.source.pairing.BasePairingRemoteDataSource;
import it.unimib.winedine.source.pairing.PairingRemoteDataSource;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.ServiceLocator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PairingRepository implements PairingResponseCallback {
    private final BasePairingRemoteDataSource remoteDataSource;
    private final MutableLiveData<Result> pairingLiveData = new MutableLiveData<>();

    public PairingRepository(BasePairingRemoteDataSource remoteDataSource) {
        this.remoteDataSource = remoteDataSource;
        this.remoteDataSource.setPairingCallback(this);
    }

    public MutableLiveData<Result> getPairing(String wine) {
        remoteDataSource.getPairing(wine);
        return pairingLiveData;
    }

    @Override
    public void onPairingSuccess(PairingAPIResponse response) {
        pairingLiveData.postValue(new Result.PairingSuccess(response));
    }

    @Override
    public void onFailure(Exception exception) {
        pairingLiveData.postValue(new Result.Error(exception.getMessage()));
    }
}
