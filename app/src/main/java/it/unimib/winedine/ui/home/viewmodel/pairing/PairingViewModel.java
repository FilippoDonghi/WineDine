package it.unimib.winedine.ui.home.viewmodel.pairing;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import it.unimib.winedine.model.Result;
import it.unimib.winedine.repository.pairing.PairingRepository;

public class PairingViewModel extends ViewModel {
    private final PairingRepository pairingRepository;
    private final MutableLiveData<Result> recipesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public PairingViewModel(PairingRepository pairingRepository) {
        this.pairingRepository = pairingRepository;

        pairingRepository.getAllRecipesForPairingsMutableLiveData().observeForever(result -> {
            if (result != null) {
                recipesLiveData.postValue(result);
            }
        });
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

