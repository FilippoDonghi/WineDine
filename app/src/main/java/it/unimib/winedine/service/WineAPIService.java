package it.unimib.winedine.service;

import static it.unimib.winedine.util.Constants.*;

import it.unimib.winedine.model.WineAPIResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface WineAPIService {
    @GET(RECOMMENDATION_ENDPOINT)
    Call<WineAPIResponse> getWines(
            @Query(RECOMMENDATION_WINE_PARAMETER) String wine,
            @Query(RECOMMENDATION_NUMBER_PARAMETER) int number,
            @Query("apiKey") String apiKey);
}
