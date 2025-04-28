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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

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

public class BottleListFragment extends Fragment{

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

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());

        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        winesRepository = ServiceLocator.getInstance().getWinesRepository(
                        requireActivity().getApplication(),
                        requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
                );

        wineViewModel = new ViewModelProvider(
                requireActivity(),
                new WineViewModelFactory(winesRepository)).get(WineViewModel.class);

        bottleList = new ArrayList<>();
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
                        bundle.putParcelable(Constants.BUNDLE_KEY_CURRENT_BOTTLE, bottle); // Passa la bottiglia
                        bundle.putString("selectedWine", selectedWine); // Passa il tipo di vino

                        Navigation.findNavController(view).navigate(R.id.action_bottleListFragment_to_visualizeBottleFragment, bundle);
                    }
                    @Override
                    public void onFavoriteButtonClick(int position) {
                        Bottle bottle = bottleList.get(position);
                        bottle.setLiked(!bottle.getLiked());
                        wineViewModel.updateWine(bottle);
                        // Recupero l'idToken dell'utente loggato
                        String idToken = userViewModel.getLoggedUser().getIdToken();

                        // Salvo la bottiglia nei preferiti su Firebase
                      userViewModel.saveUserFavoriteWines(idToken, bottle);
                    }
                });

        recyclerView.setAdapter(bottleAdapter);

        // Se un vino è stato selezionato, esegui la chiamata API
        if (selectedWine != null) {
            long lastUpdate = 0;

            wineViewModel.getBottles(selectedWine, lastUpdate).observe(getViewLifecycleOwner(),
                    result -> {
                            if (result instanceof Result.WineSuccess) {
                                this.bottleList.clear();
                                this.bottleList.addAll(((Result.WineSuccess) result).getData().getRecommendedWines());
                                bottleAdapter.notifyDataSetChanged();
                            } else if (result instanceof Result.Error) {
                                Snackbar.make(view, getString(R.string.error_retireving_bottles), Snackbar.LENGTH_SHORT).show();
                            }
                    });
        }

  /*      wineViewModel.getFavoriteWinesListLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.WineSuccess) {
                List<Bottle> favoriteBottles = ((Result.WineSuccess) result).getData().getRecommendedWines();

                for (Bottle bottle : bottleList) {
                    bottle.setLiked(false); // Reset di default
                    for (Bottle favorite : favoriteBottles) {
                        if (bottle.getId().equals(favorite.getId())) {
                            bottle.setLiked(true);
                        }
                    }
                }
                bottleAdapter.notifyDataSetChanged(); // 🔥 FORZA IL REFRESH
            }
        }); */
        // Recupero dell'idToken e dell'email dall'utente loggato
            String idToken = userViewModel.getLoggedUser().getIdToken();
            String email = userViewModel.getLoggedUser().getEmail();

        // Chiamata per salvare idToken ed email nelle SharedPreferences
            saveUserInfo(idToken, email);
        return view;
    }

    public void saveUserInfo(String idToken, String email) {
        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(getContext());
        sharedPreferencesUtils.writeStringData(SHARED_PREFERENCES_FILENAME, SHARED_PREFERENCES_ID_TOKEN, idToken );
        sharedPreferencesUtils.writeStringData(SHARED_PREFERENCES_FILENAME, SHARED_PREFERENCES_EMAIL, email );
        userViewModel.saveUserPreferences(
                sharedPreferencesUtils.readStringData(Constants.SHARED_PREFERENCES_FILENAME,
                        Constants.SHARED_PREFERENCES_ID_TOKEN)
        );
    }



}
