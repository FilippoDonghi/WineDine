package it.unimib.winedine.ui.home.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.winedine.model.Result;
import it.unimib.winedine.repository.wine.WinesRepository;

public class WineViewModel extends ViewModel {
    private static final String TAG = WineViewModel.class.getSimpleName();

    private final WinesRepository winesRepository;
    private final int page;
    private MutableLiveData<Result> bottlesListLiveData;


  public WineViewModel(WinesRepository winesRepository) {
        this.winesRepository = winesRepository;
        this.page = 1;
  }


    public MutableLiveData<Result> getBottles(String wine, long lastUpdate) {
        if (bottlesListLiveData == null) {
            fetchWines(wine, lastUpdate);
        }
        return bottlesListLiveData;
    }


    private void fetchWines(String wine, long lastUpdate) {
        bottlesListLiveData = winesRepository.fetchWines(wine, page, lastUpdate);
    }
}
