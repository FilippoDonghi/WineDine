package it.unimib.winedine.repository.pairing;

import java.util.List;

import it.unimib.winedine.model.Recipe;

public interface RecipeCallback {
    void onRecipesSuccess(List<Recipe> recipes);
    void onFailure(Exception e);

}
