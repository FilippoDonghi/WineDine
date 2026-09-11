package it.unimib.winedine.source.pairing;

import java.io.IOException;

import it.unimib.winedine.model.RecipeAPIResponse;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.JSONParserUtils;

public class RecipeMockDataSource extends BaseRecipeRemoteDataSource {
    private final JSONParserUtils jsonParser;

    public RecipeMockDataSource(JSONParserUtils jsonParser) {
        this.jsonParser = jsonParser;
    }

    @Override
    public void getRecipesForPairings(String[] ingredients) {
        try {
            RecipeAPIResponse response = jsonParser.parseRecipeResponse(
                    Constants.SAMPLE_RECIPE_FILENAME);
            responseCallback.onRecipesSuccess(response.getResults());
        } catch (IOException exception) {
            responseCallback.onFailure(exception);
        }
    }

    @Override
    public void cancelPendingRequests() {
        // Fixture reads complete synchronously, so there is nothing to cancel.
    }
}
