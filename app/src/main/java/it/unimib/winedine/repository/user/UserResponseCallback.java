package it.unimib.winedine.repository.user;

import java.util.List;

import it.unimib.winedine.model.User;

public interface UserResponseCallback {
    void onSuccessFromAuthentication(User user);
    void onFailureFromAuthentication(String message);
    void onSuccessFromRemoteDatabase(User user);

//qui manca una riga sull' articlelist
//void onSuccessFromRemoteDatabase(List<Article> articlesList);

    void onSuccessFromGettingUserPreferences();
    void onFailureFromRemoteDatabase(String message);
    void onSuccessLogout();
}

