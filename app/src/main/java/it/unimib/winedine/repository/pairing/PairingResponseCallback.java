package it.unimib.winedine.repository.pairing;

import it.unimib.winedine.model.PairingAPIResponse;
import it.unimib.winedine.model.WineAPIResponse;

public interface PairingResponseCallback {
    void onSuccessFromRemote(PairingAPIResponse pairingAPIResponse, long lastUpdate);
    void onFailureFromRemote(Exception exception);
}
