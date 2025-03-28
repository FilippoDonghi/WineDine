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
import it.unimib.winedine.repository.pairing.PairingRepository;
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
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        titleTextView = view.findViewById(R.id.text_recipe_title);
        recipeImageView = view.findViewById(R.id.image_recipe);
        readyTimeTextView = view.findViewById(R.id.text_ready_time);
        servingsTextView = view.findViewById(R.id.text_servings);
        sourceUrlTextView = view.findViewById(R.id.text_source_url);
        spoonacularTextView = view.findViewById(R.id.text_spoonecular);


        if (getArguments() != null) {
            int recipeId = getArguments().getInt("recipe_id");
            pairingViewModel.getDishes(recipeId).observe(getViewLifecycleOwner(), result -> {
                if (result instanceof Result.DishSuccess) {
                    DishAPIResponse dishAPIResponse = ((Result.DishSuccess) result).getDish();

                    // Popola la UI con i dettagli della ricetta
                    titleTextView.setText(dishAPIResponse.getTitle());

                    Glide.with(this)
                            .load(dishAPIResponse.getImage())
                            .placeholder(new ColorDrawable(getContext().getColor(R.color.md_theme_onSecondaryContainer)))
                            .into(recipeImageView);

                    readyTimeTextView.setText("Tempo di preparazione: " + dishAPIResponse.getReadyInMinutes() + " min");
                    servingsTextView.setText("Porzioni: " + dishAPIResponse.getServings());

                    // Imposta il link alla fonte
                    sourceUrlTextView.setText("Vedi ricetta completa");
                    sourceUrlTextView.setOnClickListener(v -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dishAPIResponse.getSourceUrl()));
                        Log.d("URL_DEBUG", "URL: " + dishAPIResponse.getSourceUrl());
                        startActivity(browserIntent);
                    });

                    spoonacularTextView.setText("Vedi valori nutrizionali");
                    spoonacularTextView.setOnClickListener(v -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dishAPIResponse.getSpoonacularSourceUrl()));
                        startActivity(browserIntent);
                    });

                }
            });
        }

        return view;
    }
}

