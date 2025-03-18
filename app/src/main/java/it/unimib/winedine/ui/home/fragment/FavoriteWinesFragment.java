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
import it.unimib.winedine.database.WineRoomDatabase;
import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.repository.wine.WinesRepository;
import it.unimib.winedine.ui.home.viewmodel.WineViewModel;
import it.unimib.winedine.ui.home.viewmodel.WineViewModelFactory;
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

    public FavoriteWinesFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        winesRepository = ServiceLocator.getInstance().getWinesRepository(
                requireActivity().getApplication(),
                requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
        );

        wineViewModel= new ViewModelProvider(
                requireActivity(),
                new WineViewModelFactory(winesRepository)).get(WineViewModel.class);
                bottleList = new ArrayList<>();
        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite_wines, container, false);

        circularProgressIndicator = view.findViewById(R.id.circularProgressIndicator);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        bottleRecyclerAdapter =
                new BottleRecyclerAdapter(R.layout.card_bottle, bottleList, selectedWine, true,
                        new BottleRecyclerAdapter.OnItemClickListener() {
                            @Override
                            public void onBottleItemClick(Bottle bottle, String selectedWine) {
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(Constants.BUNDLE_KEY_CURRENT_BOTTLE, bottle);
                                bundle.putString("selectedWine", selectedWine);
                                Navigation.findNavController(view).navigate(R.id.action_favoriteWinesFragment_to_visualizeBottleFragment, bundle);
                            }
                            @Override
                            public void onFavoriteButtonClick(int position) {
                                // Toggle liked status
                                bottleList.get(position).setLiked(!bottleList.get(position).getLiked());
                                // Update in database
                                wineViewModel.updateWine(bottleList.get(position));
                                // If we're in favorites and removing from favorites, also remove from list
                                if (!bottleList.get(position).getLiked()) {
                                    bottleList.remove(position);
                                    bottleRecyclerAdapter.notifyItemRemoved(position);
                            }}

                        });

        recyclerView.setAdapter(bottleRecyclerAdapter);

        wineViewModel.getFavoriteWinesListLiveData().observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        int initialSize = this.bottleList.size();
                        this.bottleList.clear();
                        this.bottleList.addAll(((Result.WineSuccess) result).getData().getRecommendedWines());
                        bottleRecyclerAdapter.notifyItemRangeInserted(initialSize, this.bottleList.size());
                        recyclerView.setVisibility(View.VISIBLE);
                        circularProgressIndicator.setVisibility(View.GONE);
                    } else {
                        Snackbar.make(view,
                                "error",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });

        return view;
    }
}