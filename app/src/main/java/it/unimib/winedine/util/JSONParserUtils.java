package it.unimib.winedine.util;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import it.unimib.winedine.model.DishAPIResponse;
import it.unimib.winedine.model.PairingAPIResponse;
import it.unimib.winedine.model.RecipeAPIResponse;
import it.unimib.winedine.model.WineAPIResponse;

public class JSONParserUtils {
    public Context context;

    public JSONParserUtils(Context context) {
        this.context = context;
    }

    public <T> T parseAsset(String filename, Class<T> responseType) throws IOException {
        try (InputStream inputStream = context.getAssets().open(filename);
             BufferedReader bufferedReader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return new Gson().fromJson(bufferedReader, responseType);
        }
    }

    public WineAPIResponse parseJSONFileWithGSon(String filename) throws IOException {
        return parseAsset(filename, WineAPIResponse.class);
    }

    public PairingAPIResponse parseJSONFileWithGSonForPairing(String filename) throws IOException {
        return parseAsset(filename, PairingAPIResponse.class);
    }

    public RecipeAPIResponse parseRecipeResponse(String filename) throws IOException {
        return parseAsset(filename, RecipeAPIResponse.class);
    }

    public DishAPIResponse parseDishResponse(String filename) throws IOException {
        return parseAsset(filename, DishAPIResponse.class);
    }
}
