package it.unimib.winedine.source.pairing;

import java.io.IOException;

import it.unimib.winedine.model.DishAPIResponse;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.JSONParserUtils;

public class DishMockDataSource extends BaseDishRemoteDataSource {
    private final JSONParserUtils jsonParser;

    public DishMockDataSource(JSONParserUtils jsonParser) {
        this.jsonParser = jsonParser;
    }

    @Override
    public void getDishes(int id) {
        try {
            DishAPIResponse response = jsonParser.parseDishResponse(Constants.SAMPLE_DISH_FILENAME);
            if (response == null || response.getId() != id) {
                responseCallback.onFailure(new IOException("No demo detail for recipe " + id));
                return;
            }
            responseCallback.onDishSuccess(response);
        } catch (IOException exception) {
            responseCallback.onFailure(exception);
        }
    }
}
