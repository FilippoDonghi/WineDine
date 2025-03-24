package it.unimib.winedine.ui.welcome.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Set;

import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.model.User;
import it.unimib.winedine.repository.user.IUserRepository;


public class UserViewModel extends ViewModel {
    private static final String TAG = UserViewModel.class.getSimpleName();

    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;
    private MutableLiveData<Result> userFavoriteNewsMutableLiveData;
    private MutableLiveData<Result> userPreferencesMutableLiveData;
    private boolean authenticationError;

    public UserViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;
        authenticationError = false;
    }

    public MutableLiveData<Result> getUserMutableLiveData(
            String email, String password, boolean isUserRegistered) {
        if (userMutableLiveData == null) {
            getUserData(email, password, isUserRegistered);
        }
        return userMutableLiveData;
    }

    public MutableLiveData<Result> getGoogleUserMutableLiveData(String token) {
        if (userMutableLiveData == null) {
            getUserData(token);
        }
        return userMutableLiveData;
    }

    public MutableLiveData<Result> getUserFavoriteNewsMutableLiveData(String idToken) {
        if (userFavoriteNewsMutableLiveData == null) {
            getUserFavoriteNews(idToken);
        }
        return userFavoriteNewsMutableLiveData;
    }

    public void saveUserPreferences(String idToken) {
        if (idToken != null) {
            userRepository.saveUserPreferences(idToken);
        }
    }

    public void saveUserFavoriteWines(String idToken, Bottle bottle) {
        if (idToken != null) {
            userRepository.saveUserFavoriteWines(idToken, bottle);
        }
    }

    public MutableLiveData<Result> getUserPreferences(String idToken) {
        if (idToken != null) {
            userPreferencesMutableLiveData = userRepository.getUserPreferences(idToken);
        }
        return userPreferencesMutableLiveData;
    }

    public User getLoggedUser() {
        return userRepository.getLoggedUser();
    }

    public LiveData<Result> logout() {
        MutableLiveData<Result> resultLiveData = new MutableLiveData<>();

        userRepository.logout().observeForever(result -> {
            if (result != null) {
                resultLiveData.postValue(result);
            } else {
                resultLiveData.postValue(new Result.Error("Errore sconosciuto durante il logout"));
            }
        });

        return resultLiveData;
    }

    private void getUserFavoriteNews(String idToken) {
        userFavoriteNewsMutableLiveData = userRepository.getUserFavoriteWines(idToken);
    }

    public LiveData<Result> getUser(String email, String password, boolean isUserRegistered) {
        // Crea un nuovo LiveData per questa specifica chiamata
        MutableLiveData<Result> resultLiveData = new MutableLiveData<>();

        // Osserva il LiveData del repository
        userRepository.getUser(email, password, isUserRegistered)
                .observeForever(new Observer<Result>() {
                    @Override
                    public void onChanged(Result result) {
                        if (result != null) {
                            resultLiveData.postValue(result);
                            // Rimuovi l'observer dopo aver ricevuto il risultato
                            userRepository.getUser(email, password, isUserRegistered).removeObserver(this);
                        }
                    }
                });

        return resultLiveData;
    }

    public boolean isAuthenticationError() {
        return authenticationError;
    }

    public void setAuthenticationError(boolean authenticationError) {
        this.authenticationError = authenticationError;
    }

    private void getUserData(String email, String password, boolean isUserRegistered) {
        userMutableLiveData = userRepository.getUser(email, password, isUserRegistered);
    }

    private void getUserData(String token) {
        userMutableLiveData = userRepository.getGoogleUser(token);
    }
}
