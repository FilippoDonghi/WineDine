package it.unimib.winedine.ui.home.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.repository.wine.WinesRepository;

public class WineViewModel extends ViewModel {
    private static final String TAG = WineViewModel.class.getSimpleName();

    private final WinesRepository winesRepository;
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
        bottlesListLiveData.setValue(new Result.Loading());

        winesRepository.fetchWines(wine, page, lastUpdate)
                .observeForever(result -> bottlesListLiveData.postValue(result));
    }
}
