package it.unimib.winedine.ui.home.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        View view = inflater.inflate(R.layout.fragment_bottle_list, container, false);

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