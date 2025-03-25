package it.unimib.winedine.source.pairing;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import it.unimib.winedine.model.PairingAPIResponse;
import it.unimib.winedine.model.Recipe;
import it.unimib.winedine.model.RecipeAPIResponse;
import it.unimib.winedine.service.WineAPIService;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.ServiceLocator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PairingRemoteDataSource extends BasePairingRemoteDataSource {
    private final WineAPIService wineAPIService;
    private List<Recipe> aggregatedRecipes = new ArrayList<>();
    private int pendingRequests = 0;

    public PairingRemoteDataSource() {
        this.wineAPIService = ServiceLocator.getInstance().getWinesAPIService();
    }

    @Override
    public void getPairingAndRecipes(String wine) {
        Call<PairingAPIResponse> pairingResponseCall = wineAPIService.getPairings(wine, Constants.WINE_API_KEY);

        Log.d("API_DEBUG", "Pairing URL: " + "https://api.spoonacular.com/food/wine/dishes?wine=" + wine + "&apiKey=" + Constants.WINE_API_KEY);

        pairingResponseCall.enqueue(new Callback<PairingAPIResponse>() {

                    @Override
                    public void onResponse(@NonNull Call<PairingAPIResponse> call,
                                           @NonNull Response<PairingAPIResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            String[] ingredients = response.body().getPairings();
                            responseCallback.onPairingSuccess(response.body(), System.currentTimeMillis());
                            getRecipesForPairings(ingredients);
                        } else {
                            responseCallback.onFailure(new Exception("Pairing API error"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PairingAPIResponse> call, @NonNull Throwable t) {
                        responseCallback.onFailure(new Exception(t));
                    }
                });
    }

    public void getRecipesForPairings(String[] ingredients) {
        Log.d("API_DEBUG", "Cercando ricette per " + ingredients.length + " ingredienti");
        pendingRequests = ingredients.length;

        for (String ingredient : ingredients) {
            Log.d("API_DEBUG", "Chiamata API per: " + ingredient);
            wineAPIService.getRecipes(ingredient, 25, 3, Constants.WINE_API_KEY)
                    .enqueue(new Callback<RecipeAPIResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<RecipeAPIResponse> call,
                                               @NonNull Response<RecipeAPIResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Log.d("API_DEBUG", "Trovate " + response.body().getResults().size() + " ricette per " + ingredient);
                                aggregatedRecipes.addAll(response.body().getResults());
                            } else {
                                Log.e("API_ERROR", "Errore per " + ingredient + ": " + response.code());
                            }
                            checkCompletion();
                        }

                        @Override
                        public void onFailure(Call<RecipeAPIResponse> call, Throwable t) {
                            Log.e("API_ERROR", "Fallimento per " + ingredient + ": " + t.getMessage());
                            checkCompletion();
                        }
                    });
        }
    }

    private void checkCompletion() {
        if (--pendingRequests == 0) {
            responseCallback.onAllRequestsCompleted(aggregatedRecipes);
        }
    }
}
