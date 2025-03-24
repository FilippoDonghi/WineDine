package it.unimib.winedine.ui.home.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.unimib.winedine.R;
import it.unimib.winedine.adapter.RecipeRecyclerAdapter;
import it.unimib.winedine.model.Recipe;

public class RecipeListFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecipeRecyclerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        List<Recipe> recipes = getArguments().getParcelableArrayList("recipes");

        recyclerView = view.findViewById(R.id.recyclerView_recipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RecipeRecyclerAdapter(recipes, recipe -> {
            // Gestione click sulla ricetta
            Bundle bundle = new Bundle();
            bundle.putParcelable("recipe", recipe);
          //  Navigation.findNavController(view).navigate(R.id.action_recipeListFragment_to_recipeDetailFragment, bundle);
        });

        recyclerView.setAdapter(adapter);

        return view;
    }
}

