package it.unimib.winedine.source.wine;

import static it.unimib.winedine.util.Constants.API_KEY_ERROR;
import static it.unimib.winedine.util.Constants.FRESH_TIMEOUT;
import static it.unimib.winedine.util.Constants.RECOMMENDATION_NUMBER_VALUE;
import static it.unimib.winedine.util.Constants.RETROFIT_ERROR;
import static it.unimib.winedine.util.Constants.WINE_API_KEY;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import it.unimib.winedine.R;
import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.WineAPIResponse;
import it.unimib.winedine.service.WineAPIService;
import it.unimib.winedine.util.ServiceLocator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottleRemoteDataSource extends BaseBottleRemoteDataSource {

    private final WineAPIService winesAPIService;

    public BottleRemoteDataSource() {
        this.winesAPIService = ServiceLocator.getInstance().getWinesAPIService();
    }

    @Override
    public void getWines(String wine) {
        Call<WineAPIResponse> winesResponseCall = winesAPIService.getWines(wine, RECOMMENDATION_NUMBER_VALUE,WINE_API_KEY);
        Log.d("API Request", "URL: https://api.spoonacular.com/food/wine/recommendation?wine="
                + wine + "&number=" + RECOMMENDATION_NUMBER_VALUE + "&apiKey=" + WINE_API_KEY);

        winesResponseCall.enqueue(new Callback<WineAPIResponse>() {

            @Override
            public void onResponse(@NonNull Call<WineAPIResponse> call,
                                   @NonNull Response<WineAPIResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    WineAPIResponse apiResponse = response.body();

                    if (apiResponse.getRecommendedWines() == null || apiResponse.getRecommendedWines().isEmpty()) {
                        Log.w("API Response", "Success, but no wines returned");
                        // Successo "vuoto", ma comunque valido
                        apiResponse.setRecommendedWines(Collections.emptyList());
                    }

                    responseCallback.onSuccessFromRemote(apiResponse, System.currentTimeMillis());

                } else {
                    responseCallback.onFailureFromRemote(new Exception("Errore nella risposta dell'API"));
                }
            }


                @Override
            public void onFailure(@NonNull Call<WineAPIResponse> call, @NonNull Throwable t) {
                responseCallback.onFailureFromRemote(new Exception(RETROFIT_ERROR));
            }
        });
    }


}



