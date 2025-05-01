package it.unimib.winedine.ui.home.viewmodel.wine;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.List;

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

    public MutableLiveData<Result> getBottles(String wine) {
      fetchWines(wine);
      return bottlesListLiveData;
    }

    public void fetchWines(String wine) {
        bottlesListLiveData.setValue(new Result.Loading());

        winesRepository.fetchWines(wine, page)
                .observeForever(result -> bottlesListLiveData.postValue(result));
    }

    public MutableLiveData<Result> getFavoriteWinesListLiveData() {
            if (favoriteWinesListLiveData == null) {
                getFavoriteWines();
            }
        return favoriteWinesListLiveData;
    }

    private void getFavoriteWines() {
        favoriteWinesListLiveData= winesRepository.getFavoriteWines();
    }

    public void updateWine(Bottle bottle){
        winesRepository.updateWine(bottle);
    }

}

