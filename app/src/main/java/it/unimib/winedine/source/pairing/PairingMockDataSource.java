package it.unimib.winedine.source.pairing;

import java.io.IOException;

import it.unimib.winedine.model.PairingAPIResponse;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.JSONParserUtils;

public class PairingMockDataSource extends BasePairingRemoteDataSource {
    private final JSONParserUtils jsonParser;

    public PairingMockDataSource(JSONParserUtils jsonParser) {
        this.jsonParser = jsonParser;
    }

    @Override
    public void getPairing(String wine) {
        try {
            PairingAPIResponse response = jsonParser.parseJSONFileWithGSonForPairing(
                    Constants.SAMPLE_PAIRING_FILENAME);
            responseCallback.onPairingSuccess(response);
        } catch (IOException exception) {
            responseCallback.onFailure(exception);
        }
    }
}
