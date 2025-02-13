package it.unimib.winedine.model;

import java.util.List;

public class BottleAPIResponse {
    private List<Bottle> bottles;
    private int totalFound;

    public void setBottles(List<Bottle> bottles) {
        this.bottles = bottles;
    }

    public List<Bottle> getBottles() {
        return bottles;
    }

    public void setTotalFound(int totalFound) {
        this.totalFound = totalFound;
    }

    public int getTotalFound() {
        return totalFound;
    }

}


