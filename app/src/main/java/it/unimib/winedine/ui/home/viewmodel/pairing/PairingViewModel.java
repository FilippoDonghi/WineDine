package it.unimib.winedine.ui.home.viewmodel.pairing;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import it.unimib.winedine.model.Result;
import it.unimib.winedine.repository.pairing.PairingRepository;

public class PairingViewModel extends ViewModel {
    private final PairingRepository pairingRepository;
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Result> pairingResult = new MutableLiveData<>();


    public PairingViewModel(PairingRepository pairingRepository) {
        this.pairingRepository = pairingRepository;
    }


    public void fetchPairing(String wine) {
        pairingRepository.getPairing(wine).observeForever(pairingResult::postValue);
    }

    public MutableLiveData<Result> getPairingResult() {
        return pairingResult;
    }


    public void resetPairingResult() {
        pairingResult.setValue(null);
    }
}



