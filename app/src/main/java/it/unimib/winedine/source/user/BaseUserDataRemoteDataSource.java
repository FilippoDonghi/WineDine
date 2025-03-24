package it.unimib.winedine.source.user;

import java.util.List;
import java.util.Set;

import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.User;
import it.unimib.winedine.repository.user.UserResponseCallback;

public abstract class BaseUserDataRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }
    public abstract void saveUserFavoriteWines(String idToken, Bottle bottle);

    public abstract void saveUserData(User user);

    public abstract void getUserFavoriteWines(String idToken);

    public abstract void getUserPreferences(String idToken);

    public abstract void saveUserPreferences(String idToken);
}



