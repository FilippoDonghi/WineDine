package it.unimib.winedine.model;

import java.util.List;

public class WineAPIResponse {
    private List<Bottle> recommendedWines;
    private int totalFound;

    public WineAPIResponse(List<Bottle> recommendedWines) {
        this.recommendedWines = recommendedWines;
    }

    public void setTotalFound(int totalFound) {
        this.totalFound = totalFound;
    }

    public int getTotalFound() {
        return totalFound;
    }

    public List<Bottle> getRecommendedWines() {
        return recommendedWines;
    }

    public void setRecommendedWines(List<Bottle> recommendedWines) {
        this.recommendedWines = recommendedWines;
    }
}


