package it.unimib.winedine.repository.wine;

import static it.unimib.winedine.util.Constants.API_KEY_ERROR;
import static it.unimib.winedine.util.Constants.FRESH_TIMEOUT;
import static it.unimib.winedine.util.Constants.RECOMMENDATION_NUMBER_VALUE;
import static it.unimib.winedine.util.Constants.RETROFIT_ERROR;
import static it.unimib.winedine.util.Constants.WINE_API_KEY;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

import it.unimib.winedine.R;
import it.unimib.winedine.database.WineDao;
import it.unimib.winedine.database.WineRoomDatabase;
import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.WineAPIResponse;
import it.unimib.winedine.service.WineAPIService;
import it.unimib.winedine.ui.home.fragment.BottleListFragment;
import it.unimib.winedine.util.ServiceLocator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WinesRepository{

    private final Application application;
    private final WineAPIService winesAPIService;
    private final WineDao winesDAO;
    public static final String TAG = WinesRepository.class.getName();


    public WinesRepository(Application application) {
        this.application = application;
        this.winesAPIService = ServiceLocator.getInstance().getWinesAPIService();
        WineRoomDatabase winesRoomDatabase = ServiceLocator.getInstance().getWinesDAO(application);
        this.winesDAO = winesRoomDatabase.wineDao();
    }


    public void fetchWines(String wine, int number, long lastUpdate, BottleResponseCallback responseCallback) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUpdate > FRESH_TIMEOUT) {    //fa la chiamata API
            Call<WineAPIResponse> winesResponseCall = winesAPIService.getWines(wine, RECOMMENDATION_NUMBER_VALUE,WINE_API_KEY);
            Log.d("API Request", "URL: https://api.spoonacular.com/food/wine/recommendation?wine="
                    + wine + "&number=" + RECOMMENDATION_NUMBER_VALUE + "&apiKey=" + R.string.wine_api_key);

            winesResponseCall.enqueue(new Callback<WineAPIResponse>() {

                @Override
                public void onResponse(@NonNull Call<WineAPIResponse> call,
                                       @NonNull Response<WineAPIResponse> response) {


                    if (response.body() != null && response.isSuccessful()) {
                        List<Bottle> bottleList = response.body().getRecommendedWines();
                        responseCallback.onSuccessFromLocal(bottleList);
                    } else {
                        responseCallback.onFailureFromRemote(new Exception(API_KEY_ERROR));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<WineAPIResponse> call, @NonNull Throwable t) {
                    responseCallback.onFailureFromRemote(new Exception(RETROFIT_ERROR));
                }
            });
        }
    }


    public void readDataFromDatabase(BottleResponseCallback responseCallback){
        WineRoomDatabase.databaseWriteExecutor.execute(() -> {
            // Reads the news from the database
            responseCallback.onSuccessFromLocal(winesDAO.getAll());
    });
}
}
