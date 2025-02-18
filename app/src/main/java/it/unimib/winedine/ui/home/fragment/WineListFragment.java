package it.unimib.winedine.ui.home.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.WineAPIResponse;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.JSONParserUtils;

public class WineListFragment extends Fragment {
    public static final String TAG = WineListFragment.class.getName();

    private List<String> categories = new ArrayList<>();
    private HashMap<String, List<String>> winesMap = new HashMap<>();
    private WineAdapter adapter;
    private ExpandableListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_wine_list, container, false);

            listView = view.findViewById(R.id.expandableListView);

            adapter = new WineAdapter(requireContext(), categories, winesMap);
            listView.setAdapter(adapter);

            getCategoriesFromFirestore();
            return view;
        }

    private void getCategoriesFromFirestore(){
        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("wines")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                categories.clear();
                                winesMap.clear();

                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    String categoryName = document.getId(); // Prendi l'ID come nome della categoria
                                    categories.add(categoryName);

                                    Log.d(TAG, "Categorie: " + categories);

                                    List<String> wines = (List<String>) document.get("winesList");
                                    if (wines != null) {
                                        winesMap.put(categoryName, wines);
                                    } else {
                                        winesMap.put(categoryName, new ArrayList<>()); // Se non ci sono vini, lista vuota
                                    }

                                    Log.d(TAG, "Categoria: " + categoryName + " -> Vini: " + wines);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Log.e(TAG, "Errore nel recupero dei documenti", task.getException());
                            }
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    }
