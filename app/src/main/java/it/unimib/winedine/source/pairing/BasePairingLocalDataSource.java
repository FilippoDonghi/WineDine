package it.unimib.winedine.source.pairing;

import java.util.List;

import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.repository.pairing.PairingResponseCallback;

public abstract class BasePairingLocalDataSource {
    protected PairingResponseCallback responseCallback;

    public void setPairingCallback(PairingResponseCallback responseCallback) {
        this.responseCallback=responseCallback;
    }

    public abstract void getPairings();

    public abstract void insertPairings(List<String> pairingsList);
}
