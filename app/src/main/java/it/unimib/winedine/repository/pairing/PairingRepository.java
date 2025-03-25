package it.unimib.winedine.repository.pairing;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import it.unimib.winedine.model.PairingAPIResponse;
import it.unimib.winedine.model.Recipe;
import it.unimib.winedine.model.RecipeAPIResponse;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.service.WineAPIService;
import it.unimib.winedine.source.pairing.BasePairingRemoteDataSource;
import it.unimib.winedine.source.pairing.PairingRemoteDataSource;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.ServiceLocator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PairingRepository implements PairingResponseCallback {

    private final BasePairingRemoteDataSource pairingRemoteDataSource;
    private final MutableLiveData<Result> allPairingsRecipeMutableLiveData;
    private final MutableLiveData<Result> allRecipesForPairingsMutableLiveData;

    public PairingRepository(BasePairingRemoteDataSource pairingRemoteDataSource) {
        allPairingsRecipeMutableLiveData = new MutableLiveData<>();
        allRecipesForPairingsMutableLiveData = new MutableLiveData<>();
        this.pairingRemoteDataSource = new PairingRemoteDataSource();
        this.pairingRemoteDataSource.setPairingCallback(this);
    }

    public MutableLiveData<Result> fetchPairingAndRecipes(String wine) {
        pairingRemoteDataSource.getPairingAndRecipes(wine);
        return allPairingsRecipeMutableLiveData;
    }

    public MutableLiveData<Result> getAllRecipesForPairingsMutableLiveData() {
        return allRecipesForPairingsMutableLiveData;
    }

    public MutableLiveData<Result> fetchRecipesForPairings(String[] ingredients) {
        pairingRemoteDataSource.getRecipesForPairings(ingredients);
        return allRecipesForPairingsMutableLiveData;
    }


    @Override
    public void onPairingSuccess(PairingAPIResponse pairingResponse, long lastUpdate) {
        // Ottieni gli ingredienti dalla risposta e cerca le ricette
        if (pairingResponse.getPairings() != null) {
            getRecipesForPairings(pairingResponse.getPairings());
        }
    }

    @Override
    public void onRecipeSuccess(List<Recipe> recipes) {

    }

    @Override
    public void onFailure(Exception exception) {
        allPairingsRecipeMutableLiveData.postValue(new Result.Error(exception.getMessage()));

    }

    @Override
    public void onAllRequestsCompleted(List<Recipe> aggregatedRecipes) {
        allRecipesForPairingsMutableLiveData.postValue(new Result.RecipesSuccess(aggregatedRecipes));
    }



    // Metodo helper per ottenere ricette
    private void getRecipesForPairings(String[] ingredients) {
        pairingRemoteDataSource.getRecipesForPairings(ingredients);
    }
}

