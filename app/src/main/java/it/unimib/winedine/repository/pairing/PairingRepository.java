package it.unimib.winedine.repository.pairing;

import static it.unimib.winedine.util.Constants.FRESH_TIMEOUT;

import androidx.lifecycle.MutableLiveData;

import it.unimib.winedine.model.PairingAPIResponse;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.source.pairing.BasePairingLocalDataSource;
import it.unimib.winedine.source.pairing.BasePairingRemoteDataSource;

public class PairingRepository implements PairingResponseCallback{

    private BasePairingRemoteDataSource bottleRemoteDataSource;
    private BasePairingLocalDataSource bottleLocalDataSource;
    private final MutableLiveData<Result> allPairingsMutableLiveData;

    public PairingRepository(BasePairingRemoteDataSource bottleRemoteDataSource) {
        this.bottleLocalDataSource = bottleLocalDataSource;
        this.bottleLocalDataSource.setPairingCallback(this);
        this.bottleRemoteDataSource = bottleRemoteDataSource;
        this.bottleRemoteDataSource.setPairingCallback(this);
    }

    public MutableLiveData<Result> fetchPairings(String wine, long lastUpdate) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdate > FRESH_TIMEOUT) {    //fa la chiamata API
            bottleRemoteDataSource.getPairings(wine);
        } else {
           // bottleLocalDataSource.getWines();
        }
        return allPairingsMutableLiveData;
    }
    @Override
    public void onSuccessFromRemote(PairingAPIResponse pairingAPIResponse, long lastUpdate) {

    }

    @Override
    public void onFailureFromRemote(Exception exception) {

    }
}
