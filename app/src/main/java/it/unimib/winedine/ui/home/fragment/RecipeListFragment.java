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
import it.unimib.winedine.repository.pairing.PairingRepository;
import it.unimib.winedine.repository.wine.WinesRepository;
import it.unimib.winedine.ui.home.viewmodel.pairing.PairingViewModel;
import it.unimib.winedine.ui.home.viewmodel.pairing.PairingViewModelFactory;
import it.unimib.winedine.ui.home.viewmodel.wine.WineViewModel;
import it.unimib.winedine.ui.home.viewmodel.wine.WineViewModelFactory;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.ServiceLocator;

public class RecipeListFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecipeRecyclerAdapter adapter;
    private PairingViewModel pairingViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PairingRepository pairingRepository = ServiceLocator.getInstance().getPairingRepository(
                requireActivity().getApplication(),
                requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode));

        pairingViewModel = new ViewModelProvider(
                requireActivity(),
                new PairingViewModelFactory(pairingRepository)).get(PairingViewModel.class);
    }

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

                    int recipeId = recipe.getId();
                    pairingViewModel.getDishes(recipeId).observe(getViewLifecycleOwner(), result -> {
                        if (result instanceof Result.DishSuccess) {
                            DishAPIResponse dishAPIResponse = ((Result.DishSuccess) result).getDish();
                            Log.d("RECIPE_SUCCESS", "Dettagli della ricetta: " + dishAPIResponse.toString());

                            Bundle bundle = new Bundle();
                            bundle.putParcelable("dishDetails", recipe);
                            Navigation.findNavController(requireView()).navigate(R.id.action_recipeListFragment_to_recipeDetailFragment, bundle);

                        } else if (result instanceof Result.Error) {
                            Toast.makeText(requireContext(), "Nessuna ricetta trovata", Toast.LENGTH_SHORT).show();
                        }
                    });
                    RecipeDetailFragment fragment = new RecipeDetailFragment();
                });

                recyclerView.setAdapter(adapter);
            }
        }

        return view;
        }}




