package it.unimib.winedine.ui.home.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.List;

import it.unimib.winedine.model.PairingAPIResponse;
import it.unimib.winedine.model.Recipe;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.repository.pairing.PairingRepository;
import it.unimib.winedine.repository.pairing.PairingResponseCallback;

public class PairingViewModel extends ViewModel {
    private final PairingRepository pairingRepository;
    private final MutableLiveData<Result> recipesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public PairingViewModel(PairingRepository pairingRepository) {
        this.pairingRepository = pairingRepository;
        // Collega il LiveData del repository al ViewModel
        pairingRepository.getAllRecipesForPairingsMutableLiveData().observeForever(result -> {
            if (result != null) {
                recipesLiveData.postValue(result);
            }
        });
    }

    public void fetchPairingAndRecipes(String wine) {
        //recipesLiveData.setValue(new Result.Loading());
     //   pairingRepository.fetchRecipesForPairings(wine) // Assicurati che questo osservi il LiveData delle ricette
     //           .observeForever(result -> recipesLiveData.postValue(result));
    }

    public void getPairingAndRecipes(String wine) {
        recipesLiveData.setValue(new Result.Loading());
        pairingRepository.fetchPairingAndRecipes(wine);
    }

    public MutableLiveData<Result> getRecipesLiveData() {
        return recipesLiveData;
    }


    public void observeRecipes(LifecycleOwner owner, Observer<Result> observer) {
        recipesLiveData.observe(owner, observer);
    }
    }

