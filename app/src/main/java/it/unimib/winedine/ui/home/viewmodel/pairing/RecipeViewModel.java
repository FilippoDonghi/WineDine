package it.unimib.winedine.ui.home.viewmodel.pairing;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import it.unimib.winedine.model.Result;
import it.unimib.winedine.repository.pairing.RecipeRepository;

public class RecipeViewModel extends ViewModel {
    private final RecipeRepository repository;

    public RecipeViewModel(RecipeRepository repository) {
        this.repository = repository;
    }

    /** Invece di postare tu sui MutableLiveData,
     ritorna semplicemente ciò che il repository espone. */
    public LiveData<Result> getRecipes(String[] ingredients) {
        repository.cancelPendingRequest();
        return repository.getRecipes(ingredients);
    }
}


