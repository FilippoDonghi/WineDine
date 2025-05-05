package it.unimib.winedine.util;

public class Constants {
    public static final int MINIMUM_LENGTH_PASSWORD = 8;

    //Error
    public static final String RETROFIT_ERROR = "retrofit_error";
    public static final String API_KEY_ERROR = "api_key_error";
    public static final String UNEXPECTED_ERROR = "unexpected_error";
    public static final String INVALID_USER_ERROR = "invalidUserError";
    public static final String INVALID_CREDENTIALS_ERROR = "invalidCredentials";
    public static final String USER_COLLISION_ERROR = "userCollisionError";
    public static final String WEAK_PASSWORD_ERROR = "passwordIsWeak";

    //Firebase
    public static final String FIREBASE_REALTIME_DATABASE = "https://winedine-6b29b-default-rtdb.europe-west1.firebasedatabase.app/";
    public static final String FIREBASE_USERS_COLLECTION = "users";
    public static final String FIREBASE_FAVORITE_WINES_COLLECTION = "favorite_wines";

    //Room
    public static final int DATABASE_VERSION = 2;
    public static final String SAVED_BOTTLES_DATABASE = "saved_db";


    //API
    public static final String API_BASE_URL = "https://api.spoonacular.com/";
    public static final String RECOMMENDATION_ENDPOINT = "food/wine/recommendation";
    public static final String RECOMMENDATION_WINE_PARAMETER = "wine";
    public static final String NUMBER_PARAMETER = "number";
    public static final int RECOMMENDATION_NUMBER_VALUE = 6;
    public static final String WINE_API_KEY = "2a8406e466eb4ae1afe2b2ef2df59b08";
    public static final String RECIPE_ENDPOINT = "recipes/complexSearch";
    public static final String QUERY_PARAMETER = "query";
    public static final String MAX_FAT_PARAMETER = "maxFat";
    public static final String PAIRING_ENDPOINT = "food/wine/dishes";

    //Shared Preferences
    public static final String SHARED_PREFERENCES_FILENAME = "it.unimib.winedine.preferences";
    public static final String SHARED_PREFERENCES_ID_TOKEN = "id_token";
    public static final String SHARED_PREFERENCES_EMAIL = "email";

    public static final String SAMPLE_JSON_FILENAME = "sample_api_response.json";
    public static final int FRESH_TIMEOUT = 3000 * 60;
    public static final String BUNDLE_KEY_CURRENT_BOTTLE = "current_bottle";
    public static final String DISH_ENDPOINT = "recipes/{id}/information";
    public static final String ID_PARAMETER = "id";
}
