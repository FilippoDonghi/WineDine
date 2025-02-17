package it.unimib.winedine.ui.home.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unimib.winedine.R;
import it.unimib.winedine.adapter.BottleRecyclerAdapter;
import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.WineAPIResponse;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.JSONParserUtils;

public class WineListFragment extends Fragment {
    public static final String TAG = WineListFragment.class.getName();

    private Set<String> categories = new HashSet<>();
    private ArrayAdapter<String> adapter;
    private Spinner spinnerCategory;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_wine_list, container, false);

            spinnerCategory = view.findViewById(R.id.spinnerCategory);

            adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(adapter);

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
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    categories.add(document.getString("category"));
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    Log.d(TAG, "Categorie: " + categories);
                                }
                                adapter.clear();
                                adapter.addAll(categories);
                                adapter.notifyDataSetChanged();
                                Log.d(TAG, "Dati nell'adapter: " + adapter.getCount());
                                for (int i = 0; i < adapter.getCount(); i++) {
                                    Log.d(TAG, "Item " + i + ": " + adapter.getItem(i));
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    }
