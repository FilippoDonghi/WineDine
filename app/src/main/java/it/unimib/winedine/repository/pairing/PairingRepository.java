package it.unimib.winedine.repository.pairing;

import it.unimib.winedine.model.PairingAPIResponse;

public class PairingRepository implements PairingResponseCallback{

    @Override
    public void onSuccessFromRemote(PairingAPIResponse pairingAPIResponse, long lastUpdate) {

    }

    @Override
    public void onFailureFromRemote(Exception exception) {

    }
}
