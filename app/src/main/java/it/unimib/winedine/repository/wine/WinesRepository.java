package it.unimib.winedine.repository.wine;

import static it.unimib.winedine.util.Constants.API_KEY_ERROR;
import static it.unimib.winedine.util.Constants.FRESH_TIMEOUT;
import static it.unimib.winedine.util.Constants.RECOMMENDATION_NUMBER_VALUE;
import static it.unimib.winedine.util.Constants.RETROFIT_ERROR;
import static it.unimib.winedine.util.Constants.WINE_API_KEY;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.Collections;
import java.util.List;

import it.unimib.winedine.R;
import it.unimib.winedine.adapter.BottleRecyclerAdapter;
import it.unimib.winedine.database.WineDao;
import it.unimib.winedine.database.WineFireStoreDatabase;
import it.unimib.winedine.database.WineRoomDatabase;
import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.model.WineAPIResponse;
import it.unimib.winedine.service.WineAPIService;
import it.unimib.winedine.source.wine.BaseBottleLocalDataSource;
import it.unimib.winedine.source.wine.BaseBottleRemoteDataSource;
import it.unimib.winedine.ui.home.fragment.BottleListFragment;
import it.unimib.winedine.util.ServiceLocator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WinesRepository implements BottleResponseCallback {

    public static final String TAG = WinesRepository.class.getName();

    private final MutableLiveData<Result> allWinesMutableLiveData;
    private final MutableLiveData<Result> favoriteWinesMutableLiveData;
    private final BaseBottleRemoteDataSource bottleRemoteDataSource;
    private final BaseBottleLocalDataSource bottleLocalDataSource;
    private final WineFireStoreDatabase wineFireStoreDatabase;


    public WinesRepository(BaseBottleRemoteDataSource bottleRemoteDataSource, BaseBottleLocalDataSource bottleLocalDataSource) {
        allWinesMutableLiveData = new MutableLiveData<>();
        favoriteWinesMutableLiveData = new MutableLiveData<>();
        this.bottleRemoteDataSource = bottleRemoteDataSource;
        this.bottleRemoteDataSource.setBottleCallback(this);
        this.bottleLocalDataSource = bottleLocalDataSource;
        this.bottleLocalDataSource.setBottleCallback(this);
        this.wineFireStoreDatabase = new WineFireStoreDatabase();
    }

    public void getCategoriesFromFirestore(WineFireStoreDatabase.FirestoreCallback callback) {
        wineFireStoreDatabase.getCategoriesFromFirestore(callback);
    }

    public MutableLiveData<Result> fetchWines(String wine, int number) {
            bottleRemoteDataSource.getWines(wine);
        return allWinesMutableLiveData;
    }

    public void updateWine(Bottle bottle) {
        bottleLocalDataSource.updateWine(bottle);
    }

    public void deleteFavoriteWines(Bottle bottle) {
        bottleLocalDataSource.deleteFavoriteWines(bottle);
    }

    public void insertWine(Bottle bottle) {
        bottleLocalDataSource.insertBottle(bottle);
    }

    public MutableLiveData<Result> getFavoriteWines() {
        bottleLocalDataSource.getFavoriteWines();
        return favoriteWinesMutableLiveData;
    }

    @Override
    public void onSuccessFromRemote(WineAPIResponse wineAPIResponse, long lastUpdate) {
        bottleLocalDataSource.insertWines(wineAPIResponse.getRecommendedWines());
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        allWinesMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromLocal(List<Bottle> bottlesList) {
        Result.WineSuccess result = new Result.WineSuccess(new WineAPIResponse(bottlesList));
        allWinesMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureFromLocal(Exception exception) {
        Result.Error resultError = new Result.Error(exception.getMessage());
        allWinesMutableLiveData.postValue(resultError);
        favoriteWinesMutableLiveData.postValue(resultError);
    }

    public void onWinesFavoriteStatusChanged(Bottle updatedBottle, List<Bottle> favoriteBottles) {
        Result result = allWinesMutableLiveData.getValue();

        if (result != null && result instanceof Result.WineSuccess) {
            Result.WineSuccess wineSuccess = (Result.WineSuccess) result;
            List<Bottle> allWines = wineSuccess.getData().getRecommendedWines();

            int index = allWines.indexOf(updatedBottle);
            if (index != -1) {
                allWines.set(index, updatedBottle);
                Log.d(TAG, "Updated bottle in allWines list: " + updatedBottle.getTitle());
                allWinesMutableLiveData.postValue(result);
            } else {
                Log.d(TAG, "Bottle not found in allWines list: " + updatedBottle.getTitle());
            }
        } else {
            Log.d(TAG, "Result is null or not an instance of WineSuccess");
        }

        Log.d(TAG, "Posting updated favorite wines list with " + favoriteBottles.size() + " items");
        favoriteWinesMutableLiveData.postValue(
                new Result.WineSuccess(new WineAPIResponse(favoriteBottles))
        );
    }




    @Override
    public void onWinesFavoriteStatusChanged(List<Bottle> favoriteBottles) {
        favoriteWinesMutableLiveData.postValue(new Result.WineSuccess(new WineAPIResponse(favoriteBottles)));
    }

    @Override
    public void onDeleteFavoriteWinesSuccess(List<Bottle> favoriteBottles) {
        Result allWinesResult = allWinesMutableLiveData.getValue();

        if (allWinesResult != null && allWinesResult.isSuccess()) {
            List<Bottle> oldAllWines = ((Result.WineSuccess) allWinesResult).getData().getRecommendedWines();
            for (Bottle bottle : favoriteBottles) {
                if (oldAllWines.contains(bottle)) {
                    oldAllWines.set(oldAllWines.indexOf(bottle), bottle);
                }
            }
            allWinesMutableLiveData.postValue(allWinesResult);
        }

        if (favoriteWinesMutableLiveData.getValue() != null &&
                favoriteWinesMutableLiveData.getValue().isSuccess()) {
            favoriteBottles.clear();
            Result.WineSuccess result = new Result.WineSuccess(new WineAPIResponse(favoriteBottles));
            favoriteWinesMutableLiveData.postValue(result);
        }
    }


}