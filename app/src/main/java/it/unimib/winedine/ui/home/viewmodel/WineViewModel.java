package it.unimib.winedine.ui.home.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import it.unimib.winedine.database.WineFireStoreDatabase;
import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.repository.wine.WinesRepository;

public class WineViewModel extends ViewModel {
    private static final String TAG = WineViewModel.class.getSimpleName();

    private final WinesRepository winesRepository;
    private final MutableLiveData<List<String>> categories = new MutableLiveData<>();
    private final MutableLiveData<HashMap<String, List<String>>> winesMap = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final int page;
    private MutableLiveData<Result> bottlesListLiveData = new MutableLiveData<>();
    private MutableLiveData<Result> favoriteWinesListLiveData;


  public WineViewModel(WinesRepository winesRepository) {
        this.winesRepository = winesRepository;
        this.page = 1;
  }


    public MutableLiveData<Result> getBottles(String wine, long lastUpdate) {
      fetchWines(wine, lastUpdate);
      return bottlesListLiveData;
    }

    public MutableLiveData<Result> getFavoriteWinesListLiveData() {
        if (favoriteWinesListLiveData == null) {
            favoriteWinesListLiveData = new MutableLiveData<>();
            loadFavoriteWines(); // Carica i preferiti inizialmente
        }
        return favoriteWinesListLiveData;
    }

    private void loadFavoriteWines() {
        favoriteWinesListLiveData = winesRepository.getFavoriteWines();
    }

    private void getFavoriteWines() {
        favoriteWinesListLiveData= winesRepository.getFavoriteWines();
    }

    public void updateWine(Bottle bottle){
          winesRepository.updateWine(bottle);
            getFavoriteWines();
    }


    private void fetchWines(String wine, long lastUpdate) {
        bottlesListLiveData.setValue(new Result.Loading()); // 🔹 Corretto: creiamo un'istanza della classe

        winesRepository.fetchWines(wine, page, lastUpdate)
                .observeForever(result -> bottlesListLiveData.postValue(result));
    }

    public LiveData<List<String>> getCategories() {
        return categories;
    }

    public LiveData<HashMap<String, List<String>>> getWinesMap() {
        return winesMap;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadCategories() {
        winesRepository.getCategoriesFromFirestore(new WineFireStoreDatabase.FirestoreCallback() {
            @Override
            public void onSuccess(List<String> fetchedCategories, HashMap<String, List<String>> fetchedWinesMap) {
                categories.postValue(fetchedCategories);
                winesMap.postValue(fetchedWinesMap);

            }

            @Override
            public void onFailure(Exception e) {
                error.postValue("Errore nel recupero delle categorie: " + e.getMessage());
            }
        });
    }


}

