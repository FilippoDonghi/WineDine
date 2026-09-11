package it.unimib.winedine.repository.user;

import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Set;

import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.model.User;
import it.unimib.winedine.model.WineAPIResponse;
import it.unimib.winedine.repository.wine.BottleResponseCallback;
import it.unimib.winedine.source.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.winedine.source.user.BaseUserDataRemoteDataSource;
import it.unimib.winedine.source.wine.BaseBottleLocalDataSource;

public class UserRepository implements IUserRepository, UserResponseCallback, BottleResponseCallback {
    private static final String TAG = UserRepository.class.getSimpleName();

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final BaseBottleLocalDataSource bottleLocalDataSource;
    private MutableLiveData<Result> userMutableLiveData;
    private MutableLiveData<Result> logoutLiveData;
    private boolean authenticationInFlight;
    private final MutableLiveData<Result> userFavoriteWinesMutableLiveData;
    private final MutableLiveData<Result> userPreferencesMutableLiveData;

    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
                          BaseUserDataRemoteDataSource userDataRemoteDataSource,
                          BaseBottleLocalDataSource bottleLocalDataSource) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.bottleLocalDataSource = bottleLocalDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        this.userPreferencesMutableLiveData = new MutableLiveData<>();
        this.userFavoriteWinesMutableLiveData = new MutableLiveData<>();
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
        this.bottleLocalDataSource.setBottleCallback(this);
    }


    @Override
    public MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered) {
        if (authenticationInFlight) {
            return userMutableLiveData;
        }
        beginAuthentication();
        if (isUserRegistered) {
            signIn(email, password);
        } else {
            signUp(email, password);
        }
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getGoogleUser(String idToken) {
        if (authenticationInFlight) {
            return userMutableLiveData;
        }
        beginAuthentication();
        signInWithGoogle(idToken);
        return userMutableLiveData;
    }


    @Override
    public MutableLiveData<Result> getUserFavoriteWines(String idToken) {
        userDataRemoteDataSource.getUserFavoriteWines(idToken);
        return userFavoriteWinesMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getUserPreferences(String idToken) {
        userDataRemoteDataSource.getUserPreferences(idToken);
        return userPreferencesMutableLiveData;
    }

    @Override
    public User getLoggedUser() {
        return userRemoteDataSource.getLoggedUser();
    }

    @Override
    public MutableLiveData<Result> logout() {
        logoutLiveData = new MutableLiveData<>();
        userRemoteDataSource.setUserResponseCallback(this);
        userRemoteDataSource.logout();
        return logoutLiveData;
    }

    private void beginAuthentication() {
        userMutableLiveData = new MutableLiveData<>();
        logoutLiveData = null;
        authenticationInFlight = true;
        userRemoteDataSource.setUserResponseCallback(this);
        userDataRemoteDataSource.setUserResponseCallback(this);
    }

    @Override
    public void signUp(String email, String password) {
        userRemoteDataSource.signUp(email, password);
    }

    @Override
    public void signIn(String email, String password) {
        userRemoteDataSource.signIn(email, password);
    }

    @Override
    public void signInWithGoogle(String token) {
        userRemoteDataSource.signInWithGoogle(token);
    }

    @Override
    public void saveUserPreferences(String idToken) {
        userDataRemoteDataSource.saveUserPreferences(idToken);
    }

    @Override
    public void saveUserFavoriteWines(String idToken, Bottle bottle){
        userDataRemoteDataSource.saveUserFavoriteWines(idToken, bottle);
    }

    @Override
    public void getUserFavoriteWines(String idToken, FavoriteWinesCallback callback) {
        userDataRemoteDataSource.setUserResponseCallback(new UserResponseCallback() {
            @Override
            public void onSuccessFromAuthentication(User user) {
            }

            @Override
            public void onFailureFromAuthentication(String message) {
            }

            @Override
            public void onSuccessFromRemoteDatabase(User user) {
            }

            @Override
            public void onSuccessFromRemoteDatabase(List<Bottle> bottles) {
                callback.onSuccess(bottles);
            }

            @Override
            public void onSuccessFromRemoteDatabaseFavorites() {
            }

            @Override
            public void onSuccessFromGettingUserPreferences() {
            }

            @Override
            public void onFailureFromRemoteDatabase(String errorMessage) {
                callback.onFailure(errorMessage);
            }

            @Override
            public void onSuccessLogout() {
            }
        });
                userDataRemoteDataSource.getUserFavoriteWines(idToken);
    }


            @Override
    public void onSuccessFromAuthentication(User user) {
        if (user != null) {
            userDataRemoteDataSource.saveUserData(user);
        }
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        Result.Error result = new Result.Error(message);
        authenticationInFlight = false;
        if (logoutLiveData != null) {
            logoutLiveData.postValue(result);
        }
        userMutableLiveData.postValue(result);
    }


    @Override
    public void onSuccessFromRemoteDatabase(User user) {
        Result.UserSuccess result = new Result.UserSuccess(user);
        authenticationInFlight = false;
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(List<Bottle> bottleList) {
        bottleLocalDataSource.insertWines(bottleList);
    }

    public void onSuccessFromRemoteDatabaseFavorites() {

    }

    @Override
    public void onSuccessFromGettingUserPreferences() {
        userPreferencesMutableLiveData.postValue(new Result.UserSuccess(null));
    }

    @Override
    public void onFailureFromRemoteDatabase(String message) {
        Result.Error result = new Result.Error(message);
        authenticationInFlight = false;
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessLogout() {
        if (logoutLiveData != null) {
            logoutLiveData.postValue(new Result.UserSuccess(null));
        }
    }


    //@Override
    public void onFailureFromCloud(Exception exception) {

    }

    @Override
    public void onSuccessFromRemote(WineAPIResponse wineAPIResponse, long lastUpdate) {

    }

    @Override
    public void onFailureFromRemote(Exception exception) {

    }

    @Override
    public void onSuccessFromLocal(List<Bottle> bottlesList) {

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

    @Override
    public void deleteFavoriteWines(Bottle bottle) {

    }

    @Override
    public void insertWine(Bottle bottle) {

    }
}
