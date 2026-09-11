package it.unimib.winedine.util;

import android.app.Application;


import it.unimib.winedine.database.WineRoomDatabase;
import it.unimib.winedine.repository.pairing.DishRepository;
import it.unimib.winedine.repository.pairing.PairingRepository;
import it.unimib.winedine.repository.pairing.RecipeRepository;
import it.unimib.winedine.repository.user.IUserRepository;
import it.unimib.winedine.repository.user.UserRepository;
import it.unimib.winedine.repository.wine.WinesRepository;
import it.unimib.winedine.service.WineAPIService;
import it.unimib.winedine.source.pairing.BaseDishRemoteDataSource;
import it.unimib.winedine.source.pairing.BasePairingRemoteDataSource;
import it.unimib.winedine.source.pairing.BaseRecipeRemoteDataSource;
import it.unimib.winedine.source.pairing.DishRemoteDataSource;
import it.unimib.winedine.source.pairing.DishMockDataSource;
import it.unimib.winedine.source.pairing.PairingMockDataSource;
import it.unimib.winedine.source.pairing.PairingRemoteDataSource;
import it.unimib.winedine.source.pairing.RecipeMockDataSource;
import it.unimib.winedine.source.pairing.RecipeRemoteDataSource;
import it.unimib.winedine.source.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.winedine.source.user.BaseUserDataRemoteDataSource;
import it.unimib.winedine.source.user.UserAuthenticationFirebaseDataSource;
import it.unimib.winedine.source.user.UserFirebaseDataSource;
import it.unimib.winedine.source.wine.BaseBottleLocalDataSource;
import it.unimib.winedine.source.wine.BaseBottleRemoteDataSource;
import it.unimib.winedine.source.wine.BottleLocalDataSource;
import it.unimib.winedine.source.wine.BottleMockDataSource;
import it.unimib.winedine.source.wine.BottleRemoteDataSource;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceLocator {
    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {}

    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized(ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }}
        }
        return INSTANCE; }

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

        BaseBottleLocalDataSource bottleLocalDataSource =
                new BottleLocalDataSource(getWineDAO(application), sharedPreferencesUtil);


        return new UserRepository(userRemoteAuthenticationDataSource,
                userDataRemoteDataSource, bottleLocalDataSource);
    }


    public WinesRepository getWinesRepository(Application application, boolean debugMode) {
        BaseBottleRemoteDataSource bottleRemoteDataSource;
        BaseBottleLocalDataSource bottleLocalDataSource;
        SharedPreferencesUtils sharedPreferencesUtil = new SharedPreferencesUtils(application);

        if (debugMode) {
            JSONParserUtils jsonParserUtil = new JSONParserUtils(application);
            bottleRemoteDataSource = new BottleMockDataSource(jsonParserUtil);
        } else {
            bottleRemoteDataSource = new BottleRemoteDataSource();
        }
        bottleLocalDataSource = new BottleLocalDataSource(getWineDAO(application), sharedPreferencesUtil);

        return new WinesRepository(bottleRemoteDataSource, bottleLocalDataSource);
    }

    public RecipeRepository getRecipeRepository(Application application, boolean debugMode) {
        BaseRecipeRemoteDataSource recipeRemoteDataSource = debugMode
                ? new RecipeMockDataSource(new JSONParserUtils(application))
                : new RecipeRemoteDataSource();
        return new RecipeRepository(recipeRemoteDataSource);
    }

    public DishRepository getDishRepository(Application application, boolean debugMode) {
        BaseDishRemoteDataSource dishRemoteDataSource = debugMode
                ? new DishMockDataSource(new JSONParserUtils(application))
                : new DishRemoteDataSource();
        return new DishRepository(dishRemoteDataSource);
    }


    public PairingRepository getPairingRepository(Application application, boolean debugMode) {
        BasePairingRemoteDataSource pairingRemoteDataSource = debugMode
                ? new PairingMockDataSource(new JSONParserUtils(application))
                : new PairingRemoteDataSource();
        return new PairingRepository(pairingRemoteDataSource);
    }



    public WineAPIService getWinesAPIService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(WineAPIService.class);
    }

public WineRoomDatabase getWineDAO(Application application){
        return WineRoomDatabase.getDatabase(application);
}

}
