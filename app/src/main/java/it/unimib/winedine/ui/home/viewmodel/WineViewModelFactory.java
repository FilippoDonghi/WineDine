package it.unimib.winedine.ui.home.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.winedine.repository.wine.WinesRepository;

public class WineViewModelFactory implements ViewModelProvider.Factory {

    private final WinesRepository winesRepository;

   public WineViewModelFactory(WinesRepository winesRepository) {
        this.winesRepository = winesRepository;
   }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new WineViewModel(winesRepository);
    }
}

