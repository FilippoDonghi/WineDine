package it.unimib.winedine.ui.home.fragment;

import static it.unimib.winedine.util.Constants.BUNDLE_KEY_CURRENT_BOTTLE;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import it.unimib.winedine.R;
import it.unimib.winedine.model.Bottle;

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
        View view = inflater.inflate(R.layout.fragment_bottle_visualize, container, false);

        ((TextView) view.findViewById(R.id.textViewTitle)).setText(currentBottle.getTitle());
        ((TextView) view.findViewById(R.id.textViewBody)).setText(currentBottle.getScore());
        ImageView imageView = view.findViewById(R.id.imageView);

        Glide.with(getContext())
                .load(currentBottle.getImageUrl())
                .placeholder(new ColorDrawable(getContext().getColor(R.color.placeholder_gray)))
                .into(imageView);


        return view;
    }

}
