package it.unimib.winedine.model;

import java.util.List;

public class PairingAPIResponse {
    private List<String> pairings;
    private String text;


    public List<String> getPairings() {
        return pairings;
    }

    public void setPairings(List<String> pairings) {
        this.pairings = pairings;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
