package it.unimib.winedine.ui.home.viewmodel.pairing;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.winedine.model.Result;
import it.unimib.winedine.repository.pairing.DishRepository;

public class DishViewModel extends ViewModel {
    private final DishRepository repository;
    private final MutableLiveData<Result> dishResult = new MutableLiveData<>();

    public DishViewModel(DishRepository repository) {
        this.repository = repository;
    }

    public void fetchDish(int id) {
        repository.getDish(id).observeForever(dishResult::postValue);
    }

    public MutableLiveData<Result> getDishResult() {
        return dishResult;
    }
}