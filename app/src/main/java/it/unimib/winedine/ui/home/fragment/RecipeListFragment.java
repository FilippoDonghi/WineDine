package it.unimib.winedine.ui.home.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unimib.winedine.R;
import it.unimib.winedine.adapter.RecipeRecyclerAdapter;
import it.unimib.winedine.model.DishAPIResponse;
import it.unimib.winedine.model.Recipe;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.repository.pairing.DishRepository;
import it.unimib.winedine.repository.pairing.PairingRepository;
import it.unimib.winedine.repository.pairing.RecipeRepository;
import it.unimib.winedine.repository.wine.WinesRepository;
import it.unimib.winedine.ui.home.viewmodel.pairing.DishViewModel;
import it.unimib.winedine.ui.home.viewmodel.pairing.DishViewModelFactory;
import it.unimib.winedine.ui.home.viewmodel.pairing.PairingViewModel;
import it.unimib.winedine.ui.home.viewmodel.pairing.PairingViewModelFactory;
import it.unimib.winedine.ui.home.viewmodel.pairing.RecipeViewModel;
import it.unimib.winedine.ui.home.viewmodel.pairing.RecipeViewModelFactory;
import it.unimib.winedine.ui.home.viewmodel.wine.WineViewModel;
import it.unimib.winedine.ui.home.viewmodel.wine.WineViewModelFactory;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.ServiceLocator;

public class RecipeListFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecipeRecyclerAdapter adapter;
    private RecipeViewModel recipeViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecipeRepository recipeRepository = ServiceLocator.getInstance().getRecipeRepository(
                requireActivity().getApplication(),
                requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
        );
        recipeViewModel = new ViewModelProvider(
                requireActivity(),
                new RecipeViewModelFactory(recipeRepository)
        ).get(RecipeViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_recipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        String[] ingredients = getArguments() != null
                ? getArguments().getStringArray("ingredients")
                : null;

        setupAdapter(ingredients);

        if (ingredients == null || ingredients.length == 0) {
            Toast.makeText(requireContext(), R.string.error_missing_ingredient_filter,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Osservo direttamente la LiveData dal ViewModel
        recipeViewModel.getRecipes(ingredients)
                .observe(getViewLifecycleOwner(), result -> {
                    if (result instanceof Result.RecipesSuccess) {
                        List<Recipe> recipes = ((Result.RecipesSuccess) result).getRecipes();
                        Log.d("RECIPE_DEBUG", "Ricevute " + recipes.size() + " ricette");
                        adapter.updateData(recipes);
                    } else if (result instanceof Result.Error) {
                        Snackbar.make(getView(), getString(R.string.error_retireving_bottles), Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupAdapter(String[] ingredients) {
        adapter = new RecipeRecyclerAdapter(new ArrayList<>(), recipe -> {
            Bundle bundle = new Bundle();
            bundle.putInt("recipe_id", recipe.getId());
            bundle.putStringArray("ingredients", ingredients);
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_recipeListFragment_to_recipeDetailFragment, bundle);
        });
        recyclerView.setAdapter(adapter);
    }
}
