package it.unimib.winedine.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WineFireStoreDatabase {
    private static final String TAG = WineFireStoreDatabase.class.getName();
    private final FirebaseFirestore db;

    public WineFireStoreDatabase() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void getCategoriesFromFirestore(final FirestoreCallback callback){
        try {
            db.collection("wines")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<String> categories = new ArrayList<>();
                                HashMap<String, List<String>> winesMap = new HashMap<>();

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
                                callback.onSuccess(categories, winesMap);
                            } else {
                                Log.e(TAG, "Errore nel recupero dei documenti", task.getException());
                                callback.onFailure(task.getException());
                            }
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public interface FirestoreCallback {
        void onSuccess(List<String> categories, HashMap<String, List<String>> winesMap);
        void onFailure(Exception e);
    }
}
