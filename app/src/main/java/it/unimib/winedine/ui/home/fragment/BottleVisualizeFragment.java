package it.unimib.winedine.ui.home.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import it.unimib.winedine.R;
import it.unimib.winedine.adapter.BottleRecyclerAdapter;
import it.unimib.winedine.model.Bottle;
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

public class BottleVisualizeFragment extends Fragment {

    private PairingViewModel pairingViewModel;
    private WineViewModel wineViewModel;

    BottleRecyclerAdapter bottleRecyclerAdapter;

    private List<Bottle> bottleList;
    private Bottle currentBottle;
    private String selectedWine;
    private Button pairingButton;

    public BottleVisualizeFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WinesRepository winesRepository = ServiceLocator.getInstance().getWinesRepository(
                requireActivity().getApplication(),
                requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode));

        wineViewModel= new ViewModelProvider(
                requireActivity(),
                new WineViewModelFactory(winesRepository)).get(WineViewModel.class);
        bottleList = new ArrayList<>();

        PairingRepository pairingRepository = ServiceLocator.getInstance().getPairingRepository(
                requireActivity().getApplication(),
                requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode));

        pairingViewModel = new ViewModelProvider(
                requireActivity(),
                new PairingViewModelFactory(pairingRepository)).get(PairingViewModel.class);

        if (getArguments() != null) {
            currentBottle = getArguments().getParcelable(Constants.BUNDLE_KEY_CURRENT_BOTTLE);
            selectedWine = getArguments().getString("selectedWine"); // Ricevi il tipo di vino
        }

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(currentBottle.getTitle());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visualize_bottle, container, false);

        ((TextView) view.findViewById(R.id.textViewTitle)).setText(currentBottle.getTitle());
        ((TextView) view.findViewById(R.id.textViewDescription)).setText(currentBottle.getDescription());

        RatingBar ratingBar = view.findViewById(R.id.rating_bar);
        TextView ratingView = view.findViewById(R.id.textViewAverageRating);

        bottleRecyclerAdapter =
                new BottleRecyclerAdapter(R.layout.fragment_visualize_bottle, bottleList, selectedWine, true,
                        new BottleRecyclerAdapter.OnItemClickListener() {
                            @Override
                            public void onBottleItemClick(Bottle bottle, String selectedWine) {
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(Constants.BUNDLE_KEY_CURRENT_BOTTLE,
                                        bottle);
                            }
                            @Override
                            public void onFavoriteButtonClick(int position) {
                                bottleList.get(position).setLiked(!bottleList.get(position).getLiked());
                                wineViewModel.updateWine(bottleList.get(position));
                            }

                        });

        int ratingCount = (int) Double.parseDouble(currentBottle.getRatingCount());
        ((TextView) view.findViewById(R.id.textViewRatingCount)).setText(ratingCount +" reviews");
        double score = Double.parseDouble(currentBottle.getScore()); // Converte la stringa in double
        String formattedScore = String.format("%.1f/100", score * 100); // Formatta con una cifra decimale
        ((TextView) view.findViewById(R.id.textViewScore)).setText(formattedScore);
        ((TextView) view.findViewById(R.id.priceText)).setText(currentBottle.getPrice());

        String originalRating = currentBottle.getAverageRating();
        try {
            float ratingValue = Float.parseFloat(originalRating) * 5; // Scala il rating su 5 stelle
            ratingBar.setRating(ratingValue);
            ratingView.setText(String.format("%.1f", ratingValue));
        } catch (NumberFormatException e) {
            ratingBar.setRating(0);
            ratingView.setText("0.0");
            Log.e("RatingError", "Formato rating non valido: " + originalRating, e);
        }

        ImageView imageView = view.findViewById(R.id.imageView);

        Glide.with(getContext())
                .load(currentBottle.getImageUrl())
                .placeholder(new ColorDrawable(getContext().getColor(R.color.md_theme_onSecondaryContainer)))
                .into(imageView);

        pairingButton = view.findViewById(R.id.button_pairing);

        pairingButton.setOnClickListener(v -> {
            pairingViewModel.getPairingAndRecipes(selectedWine);
        });


        pairingViewModel.getRecipesLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Loading) {
            } else if (result instanceof Result.RecipesSuccess) {
                List<Recipe> recipes = ((Result.RecipesSuccess) result).getRecipes();
                if (recipes.isEmpty()) {
                    Toast.makeText(requireContext(), "Nessuna ricetta trovata", Toast.LENGTH_SHORT).show();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArray("recipes", recipes.toArray(new Recipe[0]));
                    Navigation.findNavController(view).navigate(
                            R.id.action_bottleVisualizeFragment_to_recipeListFragment,
                            bundle
                    );
                }
            } else if (result instanceof Result.Error) {
                Toast.makeText(requireContext(),
                        ((Result.Error) result).getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
    }






