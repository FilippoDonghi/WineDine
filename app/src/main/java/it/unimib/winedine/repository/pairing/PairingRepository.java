package it.unimib.winedine.repository.pairing;

import static it.unimib.winedine.util.Constants.FRESH_TIMEOUT;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import it.unimib.winedine.model.PairingAPIResponse;
import it.unimib.winedine.model.Recipe;
import it.unimib.winedine.model.RecipeAPIResponse;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.service.WineAPIService;
import it.unimib.winedine.source.pairing.BasePairingLocalDataSource;
import it.unimib.winedine.source.pairing.BasePairingRemoteDataSource;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.ServiceLocator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PairingRepository {
    private final WineAPIService wineAPIService;
    private PairingResponseCallback callback;
    private List<Recipe> aggregatedRecipes = new ArrayList<>();
    private int pendingRequests = 0;

    public PairingRepository() {
        this.wineAPIService = ServiceLocator.getInstance().getWinesAPIService();
    }

    public void setCallback(PairingResponseCallback callback) {
        this.callback = callback;
    }

    public void fetchPairingAndRecipes(String wine) {
        Log.d("API_DEBUG", "Pairing URL: " +
                "https://api.spoonacular.com/food/wine/dishes?wine=" + wine +
                "&apiKey=" + Constants.WINE_API_KEY);
        wineAPIService.getPairings(wine, Constants.WINE_API_KEY)
                .enqueue(new Callback<PairingAPIResponse>() {
                    @Override
                    public void onResponse(Call<PairingAPIResponse> call, Response<PairingAPIResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onPairingSuccess(response.body(), System.currentTimeMillis());
                            fetchRecipesForPairings(response.body().getPairings());
                        } else {
                            callback.onFailure(new Exception("Pairing API error"));
                        }
                    }

                    @Override
                    public void onFailure(Call<PairingAPIResponse> call, Throwable t) {
                        callback.onFailure(new Exception(t));
                    }
                });
    }

    private void fetchRecipesForPairings(String[] ingredients) {
        pendingRequests = ingredients.length;
        for (String ingredient : ingredients) {
            wineAPIService.getRecipes(
                    ingredient,
                    25, // maxFat
                    3,  // number
                    Constants.WINE_API_KEY
            ).enqueue(new Callback<RecipeAPIResponse>() {
                @Override
                public void onResponse(Call<RecipeAPIResponse> call, Response<RecipeAPIResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        aggregatedRecipes.addAll(response.body().getResults());
                        callback.onRecipeSuccess(response.body().getResults());
                    }
                    checkCompletion();
                }

                @Override
                public void onFailure(Call<RecipeAPIResponse> call, Throwable t) {
                    checkCompletion();
                }
            });
        }
    }

    private void checkCompletion() {
        if (--pendingRequests == 0) {
            callback.onAllRequestsCompleted(aggregatedRecipes);
        }
    }
}