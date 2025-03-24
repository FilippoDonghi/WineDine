package it.unimib.winedine.repository.user;

import androidx.lifecycle.MutableLiveData;


import java.util.List;
import java.util.Set;

import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.model.User;

public interface IUserRepository {
    MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered);
    MutableLiveData<Result> getGoogleUser(String idToken);
    MutableLiveData<Result> getUserFavoriteWines(String idToken);
    MutableLiveData<Result> getUserPreferences(String idToken);
    MutableLiveData<Result> logout();
    User getLoggedUser();
    void signUp(String email, String password);
    void signIn(String email, String password);
    void signInWithGoogle(String token);
    void saveUserPreferences(String idToken);
    void saveUserFavoriteWines(String idToken, Bottle bottle);
}


