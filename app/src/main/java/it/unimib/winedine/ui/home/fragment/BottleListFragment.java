package it.unimib.winedine.ui.home.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.List;

import it.unimib.winedine.R;
import it.unimib.winedine.adapter.BottleRecyclerAdapter;
import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.WineAPIResponse;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.JSONParserUtils;

public class BottleListFragment extends Fragment {

    public static final String TAG = BottleListFragment.class.getName();

    private LinearLayout shimmerLinearLayout;
    private RecyclerView recyclerView;
    private FrameLayout noInternetView;

    public BottleListFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wine_list, container, false);

       /* shimmerLinearLayout = view.findViewById(R.id.shimmerLinearLayout);
        noInternetView = view.findViewById(R.id.noInternetMessage);*/
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        JSONParserUtils jsonParserUtil = new JSONParserUtils(getContext());

        try {
            WineAPIResponse wineAPIResponse = jsonParserUtil.parseJSONFileWithGSon(Constants.SAMPLE_JSON_FILENAME);

            Log.i(TAG, wineAPIResponse.getTotalFound() + "aa");
            List<Bottle> bottleList = wineAPIResponse.getRecommendedWines();

            BottleRecyclerAdapter adapter = new BottleRecyclerAdapter(R.layout.card_bottle, bottleList);
            recyclerView.setAdapter(adapter);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        return view;
    }
}