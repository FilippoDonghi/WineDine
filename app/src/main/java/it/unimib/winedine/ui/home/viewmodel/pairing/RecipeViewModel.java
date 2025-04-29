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
    private final MutableLiveData<Result> recipesResult = new MutableLiveData<>();
    private LiveData<Result> currentRecipesLiveData;
    private Observer<Result> recipesObserver;

    public RecipeViewModel(RecipeRepository repository) {
        this.repository = repository;
        this.recipesObserver = result -> recipesResult.postValue(result);
    }

    public void fetchRecipes(String[] ingredients) {
        repository.cancelPendingRequest();
        if (currentRecipesLiveData != null) {
            currentRecipesLiveData.removeObserver(recipesObserver);
        }

        currentRecipesLiveData = repository.getRecipes(ingredients);
        currentRecipesLiveData.observeForever(recipesObserver); // Usa observeForever
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Pulisci l'observer quando il ViewModel viene distrutto
        if (currentRecipesLiveData != null) {
            currentRecipesLiveData.removeObserver(recipesObserver);
        }
        repository.cancelPendingRequest();
    }

    public MutableLiveData<Result> getRecipesResult() {
        return recipesResult;
    }

    public void resetRecipesResult() {
        recipesResult.setValue(null);
        repository.cancelPendingRequest();
    }

}

