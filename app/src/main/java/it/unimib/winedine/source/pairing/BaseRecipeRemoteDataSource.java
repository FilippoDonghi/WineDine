package it.unimib.winedine.source.pairing;

import it.unimib.winedine.repository.pairing.RecipeCallback;

public abstract class BaseRecipeRemoteDataSource {
    protected RecipeCallback responseCallback;

    public void setRecipeCallback(RecipeCallback responseCallback) {
        this.responseCallback = responseCallback;
    }
    public abstract void getRecipesForPairings(String[] ingredients);
    public abstract void cancelPendingRequests();
}
