package it.unimib.winedine.model;

import java.util.List;

public class RecipeAPIResponse {
    private List<Recipe> results;
    private int totalResults;

    public List<Recipe> getResults() {
        return results;
    }

    public int getTotalResults() {
        return totalResults;
    }
}

