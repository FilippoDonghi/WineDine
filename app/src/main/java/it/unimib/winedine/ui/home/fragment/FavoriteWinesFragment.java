package it.unimib.winedine.ui.home.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import it.unimib.winedine.R;
import it.unimib.winedine.adapter.BottleRecyclerAdapter;
import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.model.User;
import it.unimib.winedine.repository.user.IUserRepository;
import it.unimib.winedine.repository.wine.WinesRepository;
import it.unimib.winedine.ui.home.viewmodel.wine.WineViewModel;
import it.unimib.winedine.ui.home.viewmodel.wine.WineViewModelFactory;
import it.unimib.winedine.ui.welcome.viewmodel.UserViewModel;
import it.unimib.winedine.ui.welcome.viewmodel.UserViewModelFactory;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.ServiceLocator;

public class FavoriteWinesFragment extends Fragment {
    private WinesRepository winesRepository;
    private List<Bottle> bottleList;
    private BottleRecyclerAdapter bottleRecyclerAdapter;
    private WineViewModel wineViewModel;
    private RecyclerView recyclerView;
    private CircularProgressIndicator circularProgressIndicator;
    private String selectedWine;
    private UserViewModel userViewModel;

    public FavoriteWinesFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        winesRepository = ServiceLocator.getInstance().getWinesRepository(
                requireActivity().getApplication(),
                requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
        );

        wineViewModel= new ViewModelProvider(
                requireActivity(),
                new WineViewModelFactory(winesRepository, userRepository)).get(WineViewModel.class);
        bottleList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite_wines, container, false);

        circularProgressIndicator = view.findViewById(R.id.circularProgressIndicator);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        bottleRecyclerAdapter = new BottleRecyclerAdapter(R.layout.card_bottle, bottleList, selectedWine, true,
                new BottleRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onBottleItemClick(Bottle bottle, String selectedWine) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(Constants.BUNDLE_KEY_CURRENT_BOTTLE, bottle);
                        String selectedBottle = bottle.getSelectedWine();
                        bundle.putString("selectedWine", selectedBottle);
                        Navigation.findNavController(view).navigate(R.id.action_favoriteWinesFragment_to_visualizeBottleFragment, bundle);
                    }

                    @Override
                    public void onFavoriteButtonClick(int position) {
                        User user = userViewModel.getLoggedUser();
                        if (user == null || user.getIdToken() == null) {
                            Snackbar.make(view, R.string.error_sign_in_required,
                                    Snackbar.LENGTH_SHORT).show();
                            bottleRecyclerAdapter.notifyDataSetChanged();
                            return;
                        }
                        if (position < 0 || position >= bottleList.size()) {
                            return;
                        }
                        Bottle bottle = bottleList.get(position);
                        bottle.setLiked(!bottle.getLiked());

                        // Aggiorna il database locale
                        wineViewModel.updateWine(bottle);

                        // Aggiorna Firebase
                        String idToken = user.getIdToken();
                        userViewModel.saveUserFavoriteWines(idToken, bottle);

                        // Rimuove il vino dalla lista se non è più nei preferiti
                        if (!bottle.getLiked()) {
                            bottleList.remove(position);
                            bottleRecyclerAdapter.notifyItemRemoved(position);
                        }
                    }
                });

        recyclerView.setAdapter(bottleRecyclerAdapter);

        // Osserva il LiveData di wineViewModel per i vini preferiti
        wineViewModel.getFavoriteWinesListLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.WineSuccess) {
                bottleList.clear();
                bottleList.addAll(((Result.WineSuccess) result).getData().getRecommendedWines());
                bottleRecyclerAdapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
            } else {
                Snackbar.make(view, R.string.error_loading_wines, Snackbar.LENGTH_SHORT).show();
            }
        });

        // Recupera i vini preferiti tramite il ViewModel dell'utente
        refreshFavorites();


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshFavorites();
    }

    private void refreshFavorites() {
        User user = userViewModel.getLoggedUser();
        if (user == null || user.getIdToken() == null) {
            bottleList.clear();
            bottleRecyclerAdapter.notifyDataSetChanged();
            circularProgressIndicator.setVisibility(View.GONE);
            return;
        }
        wineViewModel.refreshFavoriteWines(user.getIdToken());
    }
}
