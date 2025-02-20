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

import java.util.List;

import it.unimib.winedine.R;
import it.unimib.winedine.adapter.BottleRecyclerAdapter;
import it.unimib.winedine.database.WineDao;
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
    private final BaseBottleRemoteDataSource bottleRemoteDataSource;
    private final BaseBottleLocalDataSource bottleLocalDataSource;

    public WinesRepository(BaseBottleRemoteDataSource bottleRemoteDataSource, BaseBottleLocalDataSource bottleLocalDataSource) {
        this.allWinesMutableLiveData = new MutableLiveData<>();
        this.bottleRemoteDataSource = bottleRemoteDataSource;
        this.bottleRemoteDataSource.setBottleCallback(this);
        this.bottleLocalDataSource = bottleLocalDataSource;
        this.bottleLocalDataSource.setBottleCallback(this);
    }

    public MutableLiveData<Result> fetchWines(String wine, int number, long lastUpdate) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdate > FRESH_TIMEOUT) {    //fa la chiamata API
            bottleRemoteDataSource.getWines(wine);
        } else {
            bottleLocalDataSource.getWines();
        }
        return allWinesMutableLiveData;
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
    }

    @Override
    public void onWinesFavoriteStatusChanged(Bottle bottles, List<Bottle> favoriteBottles) {

    }

    @Override
    public void onWinesFavoriteStatusChanged(List<Bottle> bottles) {

    }

    @Override
    public void onDeleteFavoriteWinesSuccess(List<Bottle> favoriteBottles) {

    }
}
