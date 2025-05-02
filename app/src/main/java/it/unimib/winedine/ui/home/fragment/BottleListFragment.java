package it.unimib.winedine.ui.home.fragment;

import static it.unimib.winedine.util.Constants.SHARED_PREFERENCES_EMAIL;
import static it.unimib.winedine.util.Constants.SHARED_PREFERENCES_FILENAME;
import static it.unimib.winedine.util.Constants.SHARED_PREFERENCES_ID_TOKEN;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unimib.winedine.R;
import it.unimib.winedine.adapter.BottleRecyclerAdapter;
import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.repository.user.IUserRepository;
import it.unimib.winedine.repository.wine.WinesRepository;
import it.unimib.winedine.ui.home.viewmodel.wine.WineViewModel;
import it.unimib.winedine.ui.home.viewmodel.wine.WineViewModelFactory;
import it.unimib.winedine.ui.welcome.viewmodel.UserViewModel;
import it.unimib.winedine.ui.welcome.viewmodel.UserViewModelFactory;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.ServiceLocator;
import it.unimib.winedine.util.SharedPreferencesUtils;

public class BottleListFragment extends Fragment {

    public static final String TAG = BottleListFragment.class.getName();

    private RecyclerView recyclerView;
    private String selectedWine;
    private WinesRepository winesRepository;
    private List<Bottle> bottleList;
    private BottleRecyclerAdapter bottleAdapter;
    private WineViewModel wineViewModel;
    private UserViewModel userViewModel;

    public BottleListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedWine = getArguments().getString("selectedWine");
        }

        IUserRepository userRepository = ServiceLocator.getInstance()
                .getUserRepository(requireActivity().getApplication());

        userViewModel = new ViewModelProvider(requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        winesRepository = ServiceLocator.getInstance().getWinesRepository(
                requireActivity().getApplication(),
                requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
        );

        wineViewModel = new ViewModelProvider(requireActivity(),
                new WineViewModelFactory(winesRepository, userRepository)).get(WineViewModel.class);

        bottleList = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Carica i vini preferiti
        wineViewModel.getFavoriteWinesListLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.WineSuccess) {
                List<Bottle> favoriteBottles = ((Result.WineSuccess) result).getData().getRecommendedWines();
                Log.d(TAG, "Loaded favorite wines from Firebase: " + favoriteBottles.size() + " items");

                // Crea una mappa dei preferiti per un rapido accesso
                Set<String> favoriteIds = new HashSet<>();
                for (Bottle favorite : favoriteBottles) {
                    favoriteIds.add(favorite.getId());
                }

                // Aggiorna lo stato "liked" per ogni bottiglia in base ai preferiti
                for (Bottle bottle : bottleList) {
                    if (favoriteIds.contains(bottle.getId())) {
                        if (!bottle.getLiked()) {
                            bottle.setLiked(true);
                            Log.d(TAG, "Bottle " + bottle.getTitle() + " set to liked = true");
                        }
                    } else {
                        if (bottle.getLiked()) {
                            bottle.setLiked(false);
                            Log.d(TAG, "Bottle " + bottle.getTitle() + " set to liked = false");
                        }
                    }
                }

                // Notifica l'adapter per aggiornare la vista
                bottleAdapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "Error fetching favorite wines");
            }
        });

        // Carica i vini in base alla selezione, se esiste
        if (selectedWine != null) {
            wineViewModel.getBottles(selectedWine).observe(getViewLifecycleOwner(), result -> {
                if (result instanceof Result.WineSuccess) {
                    Log.d(TAG, "Loaded wines from API: " + bottleList.size() + " items");

                    // Aggiungi i vini ricevuti nella lista
                    this.bottleList.clear();
                    this.bottleList.addAll(((Result.WineSuccess) result).getData().getRecommendedWines());

                    // Sincronizza lo stato di "liked" tra i preferiti e i vini
                    wineViewModel.getFavoriteWinesListLiveData().observe(getViewLifecycleOwner(), favoriteResult -> {
                        if (favoriteResult instanceof Result.WineSuccess) {
                            List<Bottle> favoriteBottles = ((Result.WineSuccess) favoriteResult).getData().getRecommendedWines();

                            Set<String> favoriteIds = new HashSet<>();
                            for (Bottle favorite : favoriteBottles) {
                                favoriteIds.add(favorite.getId());
                            }

                            // Aggiorna lo stato "liked" per ogni bottiglia
                            for (Bottle bottle : bottleList) {
                                if (favoriteIds.contains(bottle.getId())) {
                                    if (!bottle.getLiked()) {
                                        bottle.setLiked(true);
                                        Log.d(TAG, "Bottle " + bottle.getTitle() + " set to liked = true");
                                    }
                                } else {
                                    if (bottle.getLiked()) {
                                        bottle.setLiked(false);
                                        Log.d(TAG, "Bottle " + bottle.getTitle() + " set to liked = false");
                                    }
                                }
                            }

                            // Notifica l'adapter
                            bottleAdapter.notifyDataSetChanged();
                        }
                    });
                } else if (result instanceof Result.Error) {
                    Snackbar.make(getView(), getString(R.string.error_retireving_bottles), Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottle_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        bottleAdapter = new BottleRecyclerAdapter(R.layout.card_bottle, bottleList, selectedWine, true,
                new BottleRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onBottleItemClick(Bottle bottle, String selectedWine) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(Constants.BUNDLE_KEY_CURRENT_BOTTLE, bottle);
                        bundle.putString("selectedWine", selectedWine);
                        Navigation.findNavController(view).navigate(R.id.action_bottleListFragment_to_visualizeBottleFragment, bundle);
                    }

                    @Override
                    public void onFavoriteButtonClick(int position) {
                        Bottle bottle = bottleList.get(position);
                        bottle.setLiked(!bottle.getLiked());
                        wineViewModel.updateWine(bottle);
                        bottle.setSelectedWine(selectedWine);

                        String idToken = userViewModel.getLoggedUser().getIdToken();
                        userViewModel.saveUserFavoriteWines(idToken, bottle);

                        bottleAdapter.notifyDataSetChanged();
                    }
                });

        recyclerView.setAdapter(bottleAdapter);

        return view;
    }
}
