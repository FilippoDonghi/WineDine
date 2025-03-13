package it.unimib.winedine.ui.home.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.winedine.repository.pairing.PairingRepository;
import it.unimib.winedine.repository.wine.WinesRepository;

public class PairingViewModelFactory implements ViewModelProvider.Factory{
    private final PairingRepository pairingRepository;

    public PairingViewModelFactory(PairingRepository pairingRepository) {
        this.pairingRepository = pairingRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new PairingViewModel(pairingRepository);
    }

}
