package it.unimib.winedine.ui.home.fragment;

import static it.unimib.winedine.util.Constants.BUNDLE_KEY_CURRENT_BOTTLE;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import it.unimib.winedine.R;
import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.PairingAPIResponse;
import it.unimib.winedine.source.pairing.PairingMockDataSource;
import it.unimib.winedine.ui.home.viewmodel.PairingViewModel;
import it.unimib.winedine.util.JSONParserUtils;

public class BottleVisualizeFragment extends Fragment {
    private PairingViewModel pairingViewModel;
    private Button pairingButton;

    private Bottle currentBottle;

    public BottleVisualizeFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentBottle = getArguments().getParcelable(BUNDLE_KEY_CURRENT_BOTTLE);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(currentBottle.getTitle());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visualize_bottle, container, false);

        ((TextView) view.findViewById(R.id.textViewTitle)).setText(currentBottle.getTitle());
        ((TextView) view.findViewById(R.id.textViewDescription)).setText(currentBottle.getDescription());
        TextView ratingView = view.findViewById(R.id.textViewAverageRating);

        String originalRating = currentBottle.getAverageRating();
        pairingButton = view.findViewById(R.id.button_pairing);
        setupPairingViewModel();
        setupButton();
        try {

            // 2. Troncamento a 4 caratteri
            String truncatedRating = originalRating.length() > 4 ?
                    originalRating.substring(0, 4) :
                    originalRating;

            // 3. Conversione e scaling
            double ratingValue = Double.parseDouble(truncatedRating);
            double scaledRating = ratingValue * 5;

            // 4. Formattazione con 1 decimale
            String formattedRating = String.format("%.1f ★", scaledRating);
            ratingView.setText(formattedRating);

        } catch (NumberFormatException e) {
            // Gestione errori di conversione
            ratingView.setText("0.0 ★");
            Log.e("RatingError", "Formato rating non valido: " + originalRating, e);
        }
        ((TextView) view.findViewById(R.id.textViewRatingCount)).setText(currentBottle.getRatingCount());
        ((TextView) view.findViewById(R.id.textViewScore)).setText(currentBottle.getScore());
        ((TextView) view.findViewById(R.id.textViewPrice)).setText(currentBottle.getPrice());
        ImageView imageView = view.findViewById(R.id.imageView);

        Glide.with(getContext())
                .load(currentBottle.getImageUrl())
                .placeholder(new ColorDrawable(getContext().getColor(R.color.md_theme_error)))
                .into(imageView);

        return view;
    }
    private void setupPairingViewModel() {
        pairingViewModel = new ViewModelProvider(this).get(PairingViewModel.class);

        pairingViewModel.getRecipesLiveData().observe(getViewLifecycleOwner(), recipes -> {
            // Creazione corretta del Bundle
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("recipes", new ArrayList<>(recipes)); // 1. Modifica il Bundle

            // 2. Passa il Bundle creato
            Navigation.findNavController(requireView()).navigate(
                    R.id.action_bottleVisualizeFragment_to_recipeListFragment,
                    bundle // <-- Qui viene passato il Bundle completo
            );
        });

        pairingViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(getContext(), "Errore: " + error, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupButton() {
        pairingButton.setOnClickListener(v -> {
            pairingViewModel.fetchPairingInfo(currentBottle.getTitle());
        });
    }
}



