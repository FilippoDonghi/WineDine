package it.unimib.winedine.source.pairing;


import static it.unimib.winedine.util.Constants.API_KEY_ERROR;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;

import it.unimib.winedine.model.PairingAPIResponse;
import it.unimib.winedine.model.WineAPIResponse;
import it.unimib.winedine.repository.wine.WinesRepository;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.JSONParserUtils;

public class PairingMockDataSource extends BasePairingRemoteDataSource {
    public static final String TAG = PairingMockDataSource.class.getName();
    private final JSONParserUtils jsonParserUtil;
    private MutableLiveData<PairingAPIResponse> pairingLiveData = new MutableLiveData<>();

    public PairingMockDataSource(JSONParserUtils jsonParserUtil) {
        this.jsonParserUtil = jsonParserUtil;
    }


@Override
public void getPairings(String wine) {
    PairingAPIResponse pairingAPIResponse= null;
    try{
        pairingAPIResponse = jsonParserUtil.parseJSONFileWithGSonForPairing(Constants.SAMPLE_JSON_FILENAME);
        Log.i(TAG, pairingAPIResponse.getText());
    } catch (IOException e) {
        e.printStackTrace();
    }

    if (pairingAPIResponse != null) {
        responseCallback.onPairingSuccess(pairingAPIResponse, System.currentTimeMillis());
    } else {
        responseCallback.onFailure(new Exception(API_KEY_ERROR));
    }
}
}



