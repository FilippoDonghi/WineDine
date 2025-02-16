package it.unimib.winedine.source.wine;

import it.unimib.winedine.util.JSONParserUtils;

public class BottleMockDataSource extends BaseBottleRemoteDataSource {

    private final JSONParserUtils jsonParserUtil;

    public BottleMockDataSource(JSONParserUtils jsonParserUtil) {
        this.jsonParserUtil = jsonParserUtil;
    }

/**
    @Override
    public void getArticles() {
        BottleAPIResponse bottleAPIResponse = null;

        try {
            bottleAPIResponse = jsonParserUtil.parseJSONFileWithGSon(Constants.SAMPLE_JSON_FILENAME);
            List<Bottle> bottleList = bottleAPIResponse.getBottles();
            int a=0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bottleAPIResponse != null) {
            bottleCallback.onSuccessFromRemote(bottleAPIResponse, System.currentTimeMillis());
        } else {
            bottleCallback.onFailureFromRemote(new Exception(API_KEY_ERROR));
        }
    }
    */
}



