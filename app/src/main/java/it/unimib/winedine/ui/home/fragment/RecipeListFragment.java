package it.unimib.winedine.ui.home.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unimib.winedine.R;
import it.unimib.winedine.adapter.RecipeRecyclerAdapter;
import it.unimib.winedine.model.Recipe;

public class RecipeListFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecipeRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_recipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            Recipe[] recipesArray = (Recipe[]) getArguments().getParcelableArray("recipes");
            if (recipesArray != null) {
                List<Recipe> recipes = Arrays.asList(recipesArray);
                Log.d("RECIPE_DEBUG", "Ricevute " + recipes.size() + " ricette");
                adapter = new RecipeRecyclerAdapter(recipes, recipe -> {
                    // Gestione click
                });
                recyclerView.setAdapter(adapter);
            }
        }
        return view;
    }
}
