package it.unimib.winedine.source.wine;

import static it.unimib.winedine.util.Constants.API_KEY_ERROR;

import java.io.IOException;

import it.unimib.winedine.model.WineAPIResponse;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.JSONParserUtils;

public class BottleMockDataSource extends BaseBottleRemoteDataSource {

    private final JSONParserUtils jsonParserUtil;

    public BottleMockDataSource(JSONParserUtils jsonParserUtil) {
        this.jsonParserUtil = jsonParserUtil;
    }

    @Override
    public void getWines(String wine) {
        if (!"merlot".equalsIgnoreCase(wine)) {
            responseCallback.onFailureFromRemote(
                    new IOException("The demo supports Merlot only"));
            return;
        }
        WineAPIResponse wineAPIResponse= null;
        try{
            wineAPIResponse = jsonParserUtil.parseJSONFileWithGSon(Constants.SAMPLE_JSON_FILENAME);
        } catch (IOException e) {
            responseCallback.onFailureFromRemote(e);
            return;
        }

        if (wineAPIResponse != null) {
            responseCallback.onSuccessFromRemote(wineAPIResponse, System.currentTimeMillis());
        } else {
            responseCallback.onFailureFromRemote(new Exception(API_KEY_ERROR));
        }
    }
}

