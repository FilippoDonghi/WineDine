package it.unimib.winedine.ui.home.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import it.unimib.winedine.R;
import it.unimib.winedine.adapter.BottleRecyclerAdapter;
import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.WineAPIResponse;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.JSONParserUtils;

public class WineListFragment extends Fragment {
    public static final String TAG = WineListFragment.class.getName();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_wine_list, container, false);


            try {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("wines").document("HreseKkLSRhPPgn1FGxw");

// Source can be CACHE, SERVER, or DEFAULT.
                Source source = Source.CACHE;

// Get the document, forcing the SDK to use the offline cache
                docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Document found in the offline cache
                            DocumentSnapshot document = task.getResult();
                            Log.d(TAG, "Cached document data: " + document.getData());
                            TextView textView = view.findViewById(R.id.textViewWine);
                            Map<String, Object> data = document.getData();

                            // Estrai i valori
                            String name = data.get("name").toString();
                            String type = data.get("type").toString();
                            String category = data.get("category").toString();

                            // Formatta il testo da mostrare
                            String displayText = "Name: " + name + "\nType: " + type + "\nCategory: " + category;
                            Log.d(TAG, "VINO: " + displayText);
                            Log.d(TAG, "TEXTVIEW: " + textView);
                            textView.setText(displayText);

                        } else {
                            Log.d(TAG, "Cached get failed: ", task.getException());
                        }
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return view;
        }
    }
