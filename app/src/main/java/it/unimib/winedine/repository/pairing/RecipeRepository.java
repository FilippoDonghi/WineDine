package it.unimib.winedine.repository.pairing;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.winedine.model.Recipe;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.source.pairing.BaseRecipeRemoteDataSource;

public class RecipeRepository implements RecipeCallback{
    private final BaseRecipeRemoteDataSource remoteDataSource;
    private final MutableLiveData<Result> recipesLiveData = new MutableLiveData<>();

    public RecipeRepository(BaseRecipeRemoteDataSource remoteDataSource) {
        this.remoteDataSource = remoteDataSource;
        this.remoteDataSource.setRecipeCallback(this);
    }

    public MutableLiveData<Result> getRecipes(String[] ingredients) {
        remoteDataSource.getRecipesForPairings(ingredients);
        return recipesLiveData;
    }

    @Override
    public void onRecipesSuccess(List<Recipe> recipes) {
        recipesLiveData.postValue(new Result.RecipesSuccess(recipes));
    }

    @Override
    public void onFailure(Exception exception) {
        recipesLiveData.postValue(new Result.Error(exception.getMessage()));
    }
}


