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
import androidx.test.runner.permission.RequestPermissionCallable;

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
    private DishViewModel dishViewModel;
    private List<Recipe> currentRecipes = new ArrayList<>();
    private RecipeViewModel recipeViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DishRepository dishRepository = ServiceLocator.getInstance().getDishRepository(
                requireActivity().getApplication(),
                requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode));

        dishViewModel = new ViewModelProvider(requireActivity(),
                new DishViewModelFactory(dishRepository)).get(DishViewModel.class);

        RecipeRepository recipeRepository = ServiceLocator.getInstance().getRecipeRepository(
                requireActivity().getApplication(),
                requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode));

        recipeViewModel = new ViewModelProvider(requireActivity(),
                new RecipeViewModelFactory(recipeRepository)).get(RecipeViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupAdapter();

        String[] ingredients = getArguments() != null
                ? getArguments().getStringArray("ingredients")
                : null;

        if (ingredients == null) {
            Toast.makeText(requireContext(), "Errore: nessun filtro ingredienti", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1) Registro l'observer SUL repository
        recipeViewModel.getRecipes(ingredients)
                .observe(getViewLifecycleOwner(), result -> {
                    if (result instanceof Result.RecipesSuccess) {
                        List<Recipe> recipes = ((Result.RecipesSuccess) result).getRecipes();
                        Log.d("RECIPE_DEBUG", "Ricevute " + recipes.size() + " ricette");
                        adapter.updateData(recipes);
                    } else if (result instanceof Result.Error) {
                        Toast.makeText(requireContext(), "Errore: " + ((Result.Error) result).getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_recipes); // Inizializza la RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
       /* currentRecipes.clear();
        if (adapter != null) {
            adapter.updateData(new ArrayList<>()); // Resetta l'adapter
            adapter = null;
        }
      dishViewModel.getDishResult().removeObservers(getViewLifecycleOwner());
     */}

    private void setupAdapter() {
        adapter = new RecipeRecyclerAdapter(currentRecipes, recipe -> {
            dishViewModel.getDishResult().removeObservers(getViewLifecycleOwner());

            dishViewModel.fetchDish(recipe.getId());
            dishViewModel.getDishResult().observe(getViewLifecycleOwner(), result -> {
                if (result instanceof Result.DishSuccess) {
                    navigateToDetail(((Result.DishSuccess) result).getDish());
                } else if (result instanceof Result.Error) {
                    Toast.makeText(requireContext(), "Errore nel caricamento", Toast.LENGTH_SHORT).show();
                }
            });
        });
        recyclerView.setAdapter(adapter);
    }

    private void navigateToDetail(DishAPIResponse dish) {
        NavController navController = Navigation.findNavController(getView());
        if (navController.getCurrentDestination().getId() == R.id.recipeListFragment) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("dishDetails", dish);
            navController.navigate(R.id.action_recipeListFragment_to_recipeDetailFragment, bundle);
        }
    }
}


