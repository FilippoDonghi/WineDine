package it.unimib.winedine.repository.user;

import java.util.List;

import it.unimib.winedine.model.User;

public interface IUserResponseCallback {
    void onSuccessFromAuthentication(User user);
    void onFailureFromAuthentication(String message);
    void onSuccessFromRemoteDatabase(User user);
//qui manca una riga sull' articlelist
    void onSuccessFromGettingUserPreferences();
    void onFailureFromRemoteDatabase(String message);
    void onSuccessLogout();
}

