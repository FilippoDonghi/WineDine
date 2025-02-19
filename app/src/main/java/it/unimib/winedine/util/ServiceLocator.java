package it.unimib.winedine.util;

import android.app.Application;


import it.unimib.winedine.database.WineRoomDatabase;
import it.unimib.winedine.repository.user.IUserRepository;
import it.unimib.winedine.repository.user.UserRepository;
import it.unimib.winedine.service.WineAPIService;
import it.unimib.winedine.source.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.winedine.source.user.BaseUserDataRemoteDataSource;
import it.unimib.winedine.source.user.UserAuthenticationFirebaseDataSource;
import it.unimib.winedine.source.user.UserFirebaseDataSource;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceLocator {
    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {}

    /**
     * Returns an instance of ServiceLocator class.
     * @return An instance of ServiceLocator.
     */
    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized(ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .build();
                return chain.proceed(request);
            })
            .build();


    public IUserRepository getUserRepository(Application application) {
        SharedPreferencesUtils sharedPreferencesUtil = new SharedPreferencesUtils(application);

        BaseUserAuthenticationRemoteDataSource userRemoteAuthenticationDataSource =
                new UserAuthenticationFirebaseDataSource();

        BaseUserDataRemoteDataSource userDataRemoteDataSource =
                new UserFirebaseDataSource(sharedPreferencesUtil);

        // BaseArticleLocalDataSource newsLocalDataSource =
         //       new ArticleLocalDataSource(getNewsDao(application), sharedPreferencesUtil);

        return new UserRepository(userRemoteAuthenticationDataSource,
                userDataRemoteDataSource);
    }

    public WineAPIService getWinesAPIService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(WineAPIService.class);
    }

public WineRoomDatabase getWinesDAO(Application application){
        return WineRoomDatabase.getDatabase(application);
}
}

