package it.unimib.winedine.ui.home.viewmodel.wine;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.winedine.repository.user.IUserRepository;
import it.unimib.winedine.repository.wine.WinesRepository;

public class WineViewModelFactory implements ViewModelProvider.Factory {

    private final WinesRepository winesRepository;
    private final IUserRepository userRepository;

   public WineViewModelFactory(WinesRepository winesRepository, IUserRepository userRepository) {
        this.winesRepository = winesRepository;
        this.userRepository = userRepository;
   }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new WineViewModel(winesRepository, userRepository);
    }
}

