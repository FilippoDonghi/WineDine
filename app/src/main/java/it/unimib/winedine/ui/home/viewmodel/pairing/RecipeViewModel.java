package it.unimib.winedine.ui.home.viewmodel.pairing;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.winedine.model.Result;
import it.unimib.winedine.repository.pairing.RecipeRepository;

public class RecipeViewModel extends ViewModel {
    private final RecipeRepository repository;
    private final MutableLiveData<Result> recipesResult = new MutableLiveData<>();

    public RecipeViewModel(RecipeRepository repository) {
        this.repository = repository;
    }

    public void fetchRecipes(String[] ingredients) {
        repository.getRecipes(ingredients).observeForever(recipesResult::postValue);
    }

    public MutableLiveData<Result> getRecipesResult() {
        return recipesResult;
    }

    public void resetRecipesResult() {
        recipesResult.setValue(null);
    }
}