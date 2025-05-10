package it.unimib.winedine.util;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

import it.unimib.winedine.model.User;


public class SharedPreferencesUtils {

    private final Context context;

    public SharedPreferencesUtils(Context context) {
        this.context = context;
    }

    public void writeStringData(String sharedPreferencesFileName, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(sharedPreferencesFileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void writeStringSetData(String sharedPreferencesFileName, String key, Set<String> value) {
        SharedPreferences sharedPref = context.getSharedPreferences(sharedPreferencesFileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    public String readStringData(String sharedPreferencesFileName, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(sharedPreferencesFileName,
                Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
    }

    public Set<String> readStringSetData(String sharedPreferencesFileName, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(sharedPreferencesFileName,
                Context.MODE_PRIVATE);
        return sharedPref.getStringSet(key, null);
    }
}





/**
    public SharedPreferencesUtils(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }


    public void saveLoginData(String email, String idToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ID_TOKEN, idToken);
        editor.apply(); // Salvataggio asincrono
    }


    public User getSavedUser() {
        String email = sharedPreferences.getString(KEY_EMAIL, null);
        String idToken = sharedPreferences.getString(KEY_ID_TOKEN, null);

        if (email != null && idToken != null) {
            return new User(null, email, idToken);
        } else {
            return null;
        }
    }


 //Cancella i dati di login (logout).

    public void clearLoginData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }


    public void writeStringData(String sharedPreferencesFileName, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(sharedPreferencesFileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void writeStringSetData(String sharedPreferencesFileName, String key, Set<String> value) {
        SharedPreferences sharedPref = context.getSharedPreferences(sharedPreferencesFileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    public String readStringData(String sharedPreferencesFileName, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(sharedPreferencesFileName,
                Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
    }

    public Set<String> readStringSetData(String sharedPreferencesFileName, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(sharedPreferencesFileName,
                Context.MODE_PRIVATE);
        return sharedPref.getStringSet(key, null);
    }
}
*/

