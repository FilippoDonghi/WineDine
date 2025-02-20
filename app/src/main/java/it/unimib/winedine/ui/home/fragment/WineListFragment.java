package it.unimib.winedine.ui.home.fragment;


import android.app.Application;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unimib.winedine.R;
import it.unimib.winedine.adapter.BottleRecyclerAdapter;
import it.unimib.winedine.adapter.WineAdapter;
import it.unimib.winedine.database.WineFireStoreDatabase;
import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.WineAPIResponse;
import it.unimib.winedine.repository.wine.BottleResponseCallback;
import it.unimib.winedine.repository.wine.WinesRepository;
import it.unimib.winedine.ui.home.viewmodel.WineViewModel;
import it.unimib.winedine.ui.home.viewmodel.WineViewModelFactory;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.JSONParserUtils;
import it.unimib.winedine.util.ServiceLocator;

public class WineListFragment extends Fragment implements BottleResponseCallback{
    public static final String TAG = WineListFragment.class.getName();

    private List<String> categories = new ArrayList<>();
    private HashMap<String, List<String>> winesMap = new HashMap<>();
    private ExpandableListView listView;

    private WineAdapter adapter;
    private WineFireStoreDatabase database;
    private WinesRepository winesRepository;



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        database = new WineFireStoreDatabase();

        winesRepository = ServiceLocator.getInstance().getWinesRepository(
                requireActivity().getApplication(),
                requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
        );

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_wine_list, container, false);

            listView = view.findViewById(R.id.expandableListView);

            adapter = new WineAdapter(requireContext(), categories, winesMap);
            listView.setAdapter(adapter);

            fetchCategories();


            listView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
                String selectedWine = winesMap.get(categories.get(groupPosition)).get(childPosition);
                long lastUpdate = 0;
                winesRepository.fetchWines(selectedWine, Constants.RECOMMENDATION_NUMBER_VALUE, lastUpdate);

                Bundle bundle = new Bundle();
                bundle.putString("selectedWine", selectedWine);

                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.bottleListFragment, bundle);
                return true;
            });
            return view;
        }

    private void fetchCategories() {
        database.getCategoriesFromFirestore(new WineFireStoreDatabase.FirestoreCallback() {
            @Override
            public void onSuccess(List<String> fetchedCategories, HashMap<String, List<String>> fetchedWinesMap) {
                categories.clear();
                categories.addAll(fetchedCategories);

                winesMap.clear();
                winesMap.putAll(fetchedWinesMap);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Errore nel recupero dei dati da Firestore", e);
            }
        });
    }


    @Override
    public void onSuccessFromLocal(List<Bottle> bottleList) {
        Log.i(TAG, "onSuccessFromLocal: " + bottleList.size());
    }


        @Override
        public void onFailureFromRemote(Exception e) {

                Log.e("API_ERROR", "Errore nel recupero delle raccomandazioni: " + e.getMessage());

            Toast.makeText(getContext(), "Errore nel recupero delle raccomandazioni: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSuccessFromRemote(WineAPIResponse wineAPIResponse, long lastUpdate) {

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