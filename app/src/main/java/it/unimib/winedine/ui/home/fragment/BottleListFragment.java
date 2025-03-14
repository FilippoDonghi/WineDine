package it.unimib.winedine.ui.home.fragment;

import static com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.LOG;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unimib.winedine.R;
import it.unimib.winedine.adapter.BottleRecyclerAdapter;
import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.model.WineAPIResponse;
import it.unimib.winedine.repository.wine.BottleResponseCallback;
import it.unimib.winedine.repository.wine.WinesRepository;
import it.unimib.winedine.ui.home.viewmodel.WineViewModel;
import it.unimib.winedine.ui.home.viewmodel.WineViewModelFactory;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.JSONParserUtils;
import it.unimib.winedine.util.ServiceLocator;

public class BottleListFragment extends Fragment{

    public static final String TAG = BottleListFragment.class.getName();

    private RecyclerView recyclerView;
    private String selectedWine;
    private WinesRepository winesRepository;
    private List<Bottle> bottleList;
    private BottleRecyclerAdapter bottleAdapter;
    private WineViewModel wineViewModel;


    public BottleListFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedWine = getArguments().getString("selectedWine");
        }

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

        // Inizializza l'adapter con una lista vuota
        bottleAdapter = new BottleRecyclerAdapter(R.layout.card_bottle, bottleList,
                new BottleRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onBottleItemClick(Bottle bottle, String selectedWine) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(Constants.BUNDLE_KEY_CURRENT_BOTTLE, bottle); // Passa la bottiglia
                        bundle.putString("selectedWine", selectedWine); // Passa il tipo di vino

                        Navigation.findNavController(view).navigate(R.id.action_bottleListFragment_to_visualizeBottleFragment, bundle);
                    }
                }, selectedWine); // Passa selectedWine qui

        recyclerView.setAdapter(bottleAdapter);

        // Se un vino è stato selezionato, esegui la chiamata API
        if (selectedWine != null) {
            long lastUpdate = 0; // Se hai un sistema di cache, sostituisci con un valore persistente

            wineViewModel.getBottles(selectedWine, lastUpdate).observe(getViewLifecycleOwner(),
                    result -> {
                        if (result.isSuccess()) {
                            int initialSize = this.bottleList.size();
                            this.bottleList.clear();
                            this.bottleList.addAll(((Result.WineSuccess) result).getData().getRecommendedWines());
                            bottleAdapter.notifyDataSetChanged();
                        } else {
                            Snackbar.make(view,
                                    getString(R.string.error_retireving_bottles),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    });
        }
        return view;
    }

    }
