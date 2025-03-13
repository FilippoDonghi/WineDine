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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import it.unimib.winedine.R;
import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.PairingAPIResponse;
import it.unimib.winedine.source.pairing.PairingMockDataSource;
import it.unimib.winedine.util.JSONParserUtils;

public class BottleVisualizeFragment extends Fragment {

    private Bottle currentBottle;

    public BottleVisualizeFragment(){}

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
}

