package it.unimib.winedine.ui.home.fragment;

import static com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.LOG;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import it.unimib.winedine.model.WineAPIResponse;
import it.unimib.winedine.repository.wine.BottleResponseCallback;
import it.unimib.winedine.repository.wine.WinesRepository;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.JSONParserUtils;

public class BottleListFragment extends Fragment implements BottleResponseCallback {

    public static final String TAG = BottleListFragment.class.getName();

    private RecyclerView recyclerView;
    private String selectedWine;
    private WinesRepository winesRepository;
    private List<Bottle> bottleList = new ArrayList<>();
    private BottleRecyclerAdapter bottleAdapter;

    public BottleListFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedWine = getArguments().getString("selectedWine");

        }
        winesRepository = new WinesRepository(requireActivity().getApplication());
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottle_list, container, false);


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        /*
        JSONParserUtils jsonParserUtil = new JSONParserUtils(getContext());

        try {
            WineAPIResponse wineAPIResponse = jsonParserUtil.parseJSONFileWithGSon(Constants.SAMPLE_JSON_FILENAME);

            Log.i(TAG, wineAPIResponse.getTotalFound() + "aa");
            List<Bottle> bottleList = wineAPIResponse.getRecommendedWines();

            BottleRecyclerAdapter adapter = new BottleRecyclerAdapter(R.layout.card_bottle, bottleList);
            recyclerView.setAdapter(adapter);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
*/
        // Inizializza l'adapter con una lista vuota
        bottleAdapter = new BottleRecyclerAdapter(R.layout.card_bottle, bottleList);
        recyclerView.setAdapter(bottleAdapter);

        // Se un vino è stato selezionato, esegui la chiamata API
        if (selectedWine != null) {
            long lastUpdate = 0; // Se hai un sistema di cache, sostituisci con un valore persistente
            winesRepository.fetchWines(selectedWine, Constants.RECOMMENDATION_NUMBER_VALUE, lastUpdate, this);
        }


        return view;
    }

    @Override
    public void onSuccessFromLocal(List<Bottle> bottlesList) {
        if (!bottlesList.isEmpty()) {
            bottleList.clear();
            bottleList.addAll(bottlesList);
            bottleAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSuccessFromRemote(WineAPIResponse wineAPIResponse, long lastUpdate) {

    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Log.e(TAG, "Errore nel recupero dei dati dall'API", exception);
    }



    @Override
    public void onFailureFromLocal(Exception exception) {

    }

    @Override
    public void onWinesFavoriteStatusChanged(Bottle bottles, List<Bottle> favoriteBottles) {

    }

    @Override
    public void onWinesFavoriteStatusChanged(List<Bottle> bottles) {

    }

    @Override
    public void onDeleteFavoriteWinesSuccess(List<Bottle> favoriteBottles) {

    }

    }
