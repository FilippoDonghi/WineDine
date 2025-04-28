package it.unimib.winedine.source.pairing;

import static it.unimib.winedine.util.Constants.WINE_API_KEY;

import android.util.Log;

import androidx.annotation.NonNull;

import it.unimib.winedine.model.DishAPIResponse;
import it.unimib.winedine.service.WineAPIService;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.ServiceLocator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DishRemoteDataSource extends BaseDishRemoteDataSource{
    private final WineAPIService wineAPIService;

    public DishRemoteDataSource() {
        this.wineAPIService = ServiceLocator.getInstance().getWinesAPIService();
    }

    @Override
    public void getDishes(int id) {
        Call<DishAPIResponse> call = wineAPIService.getDish(id, WINE_API_KEY);
        Log.d("API_DEBUG", "Dish URL: https://api.spoonacular.com/recipes/" + id + "/information?apiKey=" + Constants.WINE_API_KEY);

        call.enqueue(new Callback<DishAPIResponse>() {
            @Override
            public void onResponse(@NonNull Call<DishAPIResponse> call,
                                   @NonNull Response<DishAPIResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    responseCallback.onDishSuccess(response.body());
                } else {
                    responseCallback.onFailure(new Exception("Dish API error"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<DishAPIResponse> call,
                                  @NonNull Throwable t) {
                responseCallback.onFailure(new Exception(t));
            }
        });
    }
}
