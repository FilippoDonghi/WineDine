package it.unimib.winedine.source.user;

import static it.unimib.winedine.util.Constants.*;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.User;
import it.unimib.winedine.util.SharedPreferencesUtils;

public class UserFirebaseDataSource extends BaseUserDataRemoteDataSource {

    private static final String TAG = UserFirebaseDataSource.class.getSimpleName();

    private final DatabaseReference databaseReference;
    private final SharedPreferencesUtils sharedPreferencesUtil;

    public UserFirebaseDataSource(SharedPreferencesUtils sharedPreferencesUtil) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }

    @Override
    public void saveUserData(User user) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG, "User already present in Firebase Realtime Database");
                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                } else {
                    Log.d(TAG, "User not present in Firebase Realtime Database");
                    databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken()).setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    userResponseCallback.onFailureFromRemoteDatabase(e.getLocalizedMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userResponseCallback.onFailureFromRemoteDatabase(error.getMessage());
            }
        });
    }

    @Override
    public void getUserFavoriteWines(String idToken) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken)
                .child(FIREBASE_FAVORITE_WINES_COLLECTION)
                .addValueEventListener(new ValueEventListener() { // Usa ValueEventListener invece di get()
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Bottle> bottlesList = new ArrayList<>();
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            Bottle bottle = ds.getValue(Bottle.class);
                            bottlesList.add(bottle);
                        }
                        userResponseCallback.onSuccessFromRemoteDatabase(bottlesList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        userResponseCallback.onFailureFromRemoteDatabase(error.getMessage());
                    }
                });
    }


    @Override
    public void getUserPreferences(String idToken) {

    }

    @Override
    public void saveUserPreferences(String idToken) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).setValue(idToken).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.i(TAG, "fattoooo");
            }
        });
    }

    @Override
    public void saveUserFavoriteWines(String idToken, Bottle bottle) {
        DatabaseReference favoriteWinesRef = databaseReference
                .child(FIREBASE_USERS_COLLECTION)
                .child(idToken)
                .child(FIREBASE_FAVORITE_WINES_COLLECTION);

        if (bottle.getLiked()) {
            // Aggiungi la bottiglia ai preferiti
            favoriteWinesRef.child(bottle.getId()).setValue(bottle)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "Error saving favorite wine", task.getException());
                            userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
                        } else {
                            Log.d(TAG, "Successfully saved favorite wine: " + bottle.getId());
                            userResponseCallback.onSuccessFromRemoteDatabaseFavorites();
                        }
                    });
        } else {
            // Rimuovi la bottiglia dai preferiti se è stata deselezionata
            favoriteWinesRef.child(bottle.getId()).removeValue()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "Error removing favorite wine", task.getException());
                            userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
                        } else {
                            Log.d(TAG, "Successfully removed favorite wine: " + bottle.getId());
                            userResponseCallback.onSuccessFromRemoteDatabaseFavorites();
                        }
                    });
        }

    }}

