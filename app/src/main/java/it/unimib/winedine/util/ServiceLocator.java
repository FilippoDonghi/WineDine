package it.unimib.winedine.util;

import android.app.Application;



import it.unimib.winedine.repository.user.IUserRepository;
import it.unimib.winedine.repository.user.UserRepository;
import it.unimib.winedine.source.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.winedine.source.user.BaseUserDataRemoteDataSource;
import it.unimib.winedine.source.user.UserAuthenticationFirebaseDataSource;
import it.unimib.winedine.source.user.UserFirebaseDataSource;

public class ServiceLocator {
    /**
    public IUserRepository getUserRepository(Application application) {
        SharedPreferencesUtils sharedPreferencesUtil = new SharedPreferencesUtils(application);

        BaseUserAuthenticationRemoteDataSource userRemoteAuthenticationDataSource =
                new UserAuthenticationFirebaseDataSource();

        BaseUserDataRemoteDataSource userDataRemoteDataSource =
                new UserFirebaseDataSource(sharedPreferencesUtil);

       BaseArticleLocalDataSource newsLocalDataSource =
               new ArticleLocalDataSource(getNewsDao(application), sharedPreferencesUtil);

      return new UserRepository(userRemoteAuthenticationDataSource,
             userDataRemoteDataSource, newsLocalDataSource);
    } **/
}

