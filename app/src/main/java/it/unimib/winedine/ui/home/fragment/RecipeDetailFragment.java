package it.unimib.winedine.ui.home.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import it.unimib.winedine.R;
import it.unimib.winedine.model.DishAPIResponse;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.repository.pairing.DishRepository;
import it.unimib.winedine.repository.pairing.PairingRepository;
import it.unimib.winedine.ui.home.viewmodel.pairing.DishViewModel;
import it.unimib.winedine.ui.home.viewmodel.pairing.DishViewModelFactory;
import it.unimib.winedine.ui.home.viewmodel.pairing.PairingViewModel;
import it.unimib.winedine.ui.home.viewmodel.pairing.PairingViewModelFactory;
import it.unimib.winedine.util.ServiceLocator;

public class RecipeDetailFragment extends Fragment {

    private TextView titleTextView;
    private ImageView recipeImageView;
    private TextView readyTimeTextView;
    private TextView servingsTextView;
    private TextView sourceUrlTextView;
    private TextView spoonacularTextView;

    private DishViewModel dishViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DishRepository dishRepository = ServiceLocator.getInstance().getDishRepository(
                requireActivity().getApplication(),
                requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode));

        dishViewModel = new ViewModelProvider(requireActivity(),
                new DishViewModelFactory(dishRepository)).get(DishViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        // 1) Bind delle view
        titleTextView       = view.findViewById(R.id.text_recipe_title);
        recipeImageView     = view.findViewById(R.id.image_recipe);
        readyTimeTextView   = view.findViewById(R.id.text_ready_time);
        servingsTextView    = view.findViewById(R.id.text_servings);
        sourceUrlTextView   = view.findViewById(R.id.text_source_url);
        spoonacularTextView = view.findViewById(R.id.text_spoonecular);

        // 2) Estrazione dell'id dal bundle (deve sempre esserci)
        int recipeId = requireArguments().getInt("recipe_id", -1);
        if (recipeId == -1) {
            throw new IllegalStateException("RecipeDetailFragment richiede sempre un recipe_id nel bundle");
        }

        // 3) Osservo il LiveData “fresco” per questo id
        dishViewModel.getDish(recipeId)
                .observe(getViewLifecycleOwner(), result -> {
                    if (result instanceof Result.DishSuccess) {
                        updateUI(((Result.DishSuccess) result).getDish());
                    } else if (result instanceof Result.Error) {
                        Toast.makeText(requireContext(),
                                        "Errore nel caricamento: " + ((Result.Error) result).getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                });

        return view;
    }


    private void updateUI(DishAPIResponse dish) {
        titleTextView.setText(dish.getTitle());

        Glide.with(this).load(dish.getImage())
                .placeholder(new ColorDrawable(getContext().getColor(R.color.md_theme_onSecondaryContainer)))
                .into(recipeImageView);

        readyTimeTextView.setText("Tempo di preparazione: " + dish.getReadyInMinutes() + " min");
        servingsTextView.setText("Porzioni: " + dish.getServings());

        sourceUrlTextView.setText("Vedi ricetta completa");
        sourceUrlTextView.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dish.getSourceUrl()));
            Log.d("URL_DEBUG", "URL: " + dish.getSourceUrl());
            startActivity(browserIntent);
        });
        spoonacularTextView.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dish.getSpoonacularSourceUrl()));
            startActivity(browserIntent);
        });
    }
}

