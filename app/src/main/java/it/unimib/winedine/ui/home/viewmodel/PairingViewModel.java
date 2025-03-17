package it.unimib.winedine.ui.home.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import it.unimib.winedine.model.PairingAPIResponse;
import it.unimib.winedine.model.Recipe;
import it.unimib.winedine.repository.pairing.PairingRepository;
import it.unimib.winedine.repository.pairing.PairingResponseCallback;

public class PairingViewModel extends ViewModel {
    private final PairingRepository pairingRepository;
    private final MutableLiveData<List<Recipe>> recipesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public PairingViewModel(PairingRepository pairingRepository) {
        this.pairingRepository = pairingRepository;
    }

    public void fetchPairingInfo(String wine) {
        pairingRepository.setCallback(new PairingResponseCallback() {
            @Override
            public void onPairingSuccess(PairingAPIResponse pairingResponse, long lastUpdate) {
                // Gestione testo di abbinamento se necessario
            }

            @Override
            public void onRecipeSuccess(List<Recipe> recipes) {
                // Aggiornamento parziale
            }

            @Override
            public void onFailure(Exception exception) {
                errorLiveData.postValue(exception.getMessage());
            }

            @Override
            public void onAllRequestsCompleted(List<Recipe> aggregatedRecipes) {
                recipesLiveData.postValue(aggregatedRecipes);
            }
        });
        pairingRepository.fetchPairingAndRecipes(wine);
    }

    public MutableLiveData<List<Recipe>> getRecipesLiveData() {
        return recipesLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
}
