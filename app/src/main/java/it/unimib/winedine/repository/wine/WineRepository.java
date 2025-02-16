package it.unimib.winedine.repository.wine;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.unimib.winedine.database.WineRoomDatabase;
import it.unimib.winedine.model.Wine;

public class WineRepository {
    private final WineRoomDatabase db;
    private final FirebaseFirestore firestore;

    public WineRepository(WineRoomDatabase db) {
        this.db = db;
        this.firestore = FirebaseFirestore.getInstance();
    }

    public List<Wine> getWinesByCategory(String category) {
        return db.wineDao().getWinesByCategory(category);
    }

    public CompletableFuture<Void> syncWithFirestore() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        firestore.collection("wines").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Wine> wineList = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Wine wine = new Wine(
                            doc.getId(),
                            doc.getString("name"),
                            doc.getString("type"),
                            doc.getString("category")
                    );
                    wineList.add(wine);
                }
                new Thread(() -> {
                    db.wineDao().clearAll();
                    db.wineDao().insertAll(wineList);
                    future.complete(null);
                }).start();
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }
}
