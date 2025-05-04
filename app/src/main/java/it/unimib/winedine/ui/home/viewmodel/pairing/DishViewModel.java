package it.unimib.winedine.ui.home.viewmodel.pairing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.winedine.model.Result;
import it.unimib.winedine.repository.pairing.DishRepository;

public class DishViewModel extends ViewModel {
    private final DishRepository repository;

    public DishViewModel(DishRepository repository) {
        this.repository = repository;
    }


    public LiveData<Result> getDish(int id) {
        return repository.getDish(id);
    }
}
