package it.unimib.winedine.source.pairing;

import static it.unimib.winedine.util.Constants.API_KEY_ERROR;
import static it.unimib.winedine.util.Constants.RETROFIT_ERROR;
import static it.unimib.winedine.util.Constants.WINE_API_KEY;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import it.unimib.winedine.model.DishAPIResponse;
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
    public void getPairing(String wine) {
        Call<PairingAPIResponse> call = wineAPIService.getPairings(wine, WINE_API_KEY);
        call.enqueue(new Callback<PairingAPIResponse>() {
            @Override
            public void onResponse(@NonNull Call<PairingAPIResponse> call,
                                   @NonNull Response<PairingAPIResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    responseCallback.onPairingSuccess(response.body());
                    Log.d("API_DEBUG", "Chiamata API pairing per: " + wine);
                } else {
                    responseCallback.onFailure(new Exception("Pairing API error"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<PairingAPIResponse> call,
                                  @NonNull Throwable t) {
                responseCallback.onFailure(new Exception(t));
            }
        });
    }




}
