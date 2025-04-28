package it.unimib.winedine.ui.home.viewmodel.pairing;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.winedine.repository.pairing.DishRepository;

public class DishViewModelFactory implements ViewModelProvider.Factory {
    private final DishRepository repository;

    public DishViewModelFactory(DishRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DishViewModel(repository);
    }
}
