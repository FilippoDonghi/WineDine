package it.unimib.winedine.model;

import static org.junit.Assert.*;

import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

public class DemoFixturesTest {
    @Test public void everyDemoRecipeHasMatchingDetail() throws Exception {
        Gson gson = new Gson();
        RecipeAPIResponse recipes = gson.fromJson(read("sample_recipe_response.json"),
                RecipeAPIResponse.class);
        DishAPIResponse detail = gson.fromJson(read("sample_dish_response.json"),
                DishAPIResponse.class);
        assertEquals(1, recipes.getResults().size());
        assertEquals(recipes.getResults().get(0).getId(), detail.getId());
        assertEquals(recipes.getResults().get(0).getTitle(), detail.getTitle());
    }

    private String read(String name) throws Exception {
        return new String(Files.readAllBytes(Paths.get("src/main/assets", name)),
                StandardCharsets.UTF_8);
    }
}
