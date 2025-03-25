package it.unimib.winedine.model;

import java.util.List;

public class PairingAPIResponse {
    private String[] pairings;
    private String text;


    public String[] getPairings() {

        return pairings;
    }

    public void setPairings(String[] pairings) {
        this.pairings = pairings;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
