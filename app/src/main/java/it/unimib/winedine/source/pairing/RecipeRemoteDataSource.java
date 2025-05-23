package it.unimib.winedine.source.pairing;

import static it.unimib.winedine.util.Constants.WINE_API_KEY;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import it.unimib.winedine.model.Recipe;
import it.unimib.winedine.model.RecipeAPIResponse;
import it.unimib.winedine.service.WineAPIService;
import it.unimib.winedine.util.ServiceLocator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeRemoteDataSource extends BaseRecipeRemoteDataSource {

    private final WineAPIService wineAPIService;
    private List<Recipe> aggregatedRecipes = new ArrayList<>();
    private int pendingRequests = 0;
    private Call currentCall;

    public RecipeRemoteDataSource() {
        this.wineAPIService = ServiceLocator.getInstance().getWinesAPIService();
    }

    @Override
    public void getRecipesForPairings(String[] ingredients) {
        aggregatedRecipes = new ArrayList<>();
        pendingRequests = ingredients.length;

        for (String ingredient : ingredients) {
            currentCall = wineAPIService.getRecipes(ingredient, 25, 3, WINE_API_KEY);
            currentCall.enqueue(new Callback<RecipeAPIResponse>() {
                @Override
                public void onResponse(@NonNull Call<RecipeAPIResponse> call,
                                       @NonNull Response<RecipeAPIResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        aggregatedRecipes.addAll(response.body().getResults());
                        Log.d("API_DEBUG", "Chiamata API pairing per: " + aggregatedRecipes.size());
                    }
                    checkCompletion();
                }

                @Override
                public void onFailure(@NonNull Call<RecipeAPIResponse> call,
                                      @NonNull Throwable t) {
                    checkCompletion();
                }
            });
        }
    }


    @Override
    public void cancelPendingRequests() {
        if (currentCall != null && !currentCall.isCanceled()) {
            currentCall.cancel();
        }
    }

    private void checkCompletion() {
        if (--pendingRequests == 0) {
            responseCallback.onRecipesSuccess(aggregatedRecipes);
        }
    }
}