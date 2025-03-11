package it.unimib.winedine.util;

public class Constants {
    public static final int MINIMUM_LENGTH_PASSWORD = 8;

    public static final String RETROFIT_ERROR = "retrofit_error";
    public static final String API_KEY_ERROR = "api_key_error";
    public static final String UNEXPECTED_ERROR = "unexpected_error";
    public static final String INVALID_USER_ERROR = "invalidUserError";
    public static final String INVALID_CREDENTIALS_ERROR = "invalidCredentials";
    public static final String USER_COLLISION_ERROR = "userCollisionError";
    public static final String WEAK_PASSWORD_ERROR = "passwordIsWeak";

    public static final String FIREBASE_REALTIME_DATABASE = "https://winedine-6b29b-default-rtdb.europe-west1.firebasedatabase.app/";
    public static final String FIREBASE_USERS_COLLECTION = "users";


    public static final String SAMPLE_JSON_FILENAME = "sample_api_response.json";

    public static final int DATABASE_VERSION = 1;
    public static final String SAVED_BOTTLES_DATABASE = "saved_db";

    public static final String API_BASE_URL = "https://api.spoonacular.com/food/wine/";
    public static final String RECOMMENDATION_ENDPOINT = "recommendation";
    public static final String RECOMMENDATION_WINE_PARAMETER = "wine";
    public static final String RECOMMENDATION_NUMBER_PARAMETER = "number";
    public static final int RECOMMENDATION_NUMBER_VALUE = 3;
    public static final int FRESH_TIMEOUT = 1000 * 60; // 1 minute in milliseconds
    public static final String WINE_API_KEY = "38d691c02be24ca092d408e95fda6005";

    public static final String BUNDLE_KEY_CURRENT_BOTTLE = "current_bottle";


    public static final String DISHES_ENDPOINT = "dishes";


}
