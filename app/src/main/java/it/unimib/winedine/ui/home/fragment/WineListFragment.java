package it.unimib.winedine.ui.home.fragment;


import android.app.Application;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import it.unimib.winedine.R;
import it.unimib.winedine.adapter.BottleRecyclerAdapter;
import it.unimib.winedine.adapter.WineAdapter;
import it.unimib.winedine.database.WineFireStoreDatabase;
import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.WineAPIResponse;
import it.unimib.winedine.repository.wine.BottleResponseCallback;
import it.unimib.winedine.repository.wine.WinesRepository;
import it.unimib.winedine.ui.home.viewmodel.WineViewModel;
import it.unimib.winedine.ui.home.viewmodel.WineViewModelFactory;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.JSONParserUtils;
import it.unimib.winedine.util.ServiceLocator;

public class WineListFragment extends Fragment{
    public static final String TAG = WineListFragment.class.getName();

    private List<String> categories = new ArrayList<>();
    private HashMap<String, String> categoryMapping = new HashMap<>(); // Mappa per il legame tra nomi originali e formattati
    private HashMap<String, List<String>> winesMap = new HashMap<>();
    private ExpandableListView listView;

    private WineAdapter adapter;
    private WineFireStoreDatabase database;
    private WinesRepository winesRepository;
    private WineViewModel wineViewModel;



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        database = new WineFireStoreDatabase();

        winesRepository = ServiceLocator.getInstance().getWinesRepository(
                requireActivity().getApplication(),
                requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
        );

        wineViewModel = new ViewModelProvider(
                requireActivity(),
                new WineViewModelFactory(winesRepository)).get(WineViewModel.class);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_wine_list, container, false);

            listView = view.findViewById(R.id.expandableListView);

            adapter = new WineAdapter(requireContext(), categories, winesMap);
            listView.setAdapter(adapter);

            // Osserva i dati dal ViewModel
            wineViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
                this.categories.clear();
                this.categories.addAll(categories);
                formatCategoryAndWineNames(); // Chiama il metodo di formattazione
                adapter.updateCategories(this.categories);
            });

            wineViewModel.getWinesMap().observe(getViewLifecycleOwner(), winesMap -> {
                this.winesMap.clear();
                this.winesMap.putAll(winesMap);
                formatCategoryAndWineNames(); // Chiama il metodo di formattazione
                adapter.updateWinesMap(this.winesMap);
            });

            wineViewModel.getError().observe(getViewLifecycleOwner(), errorMessage -> {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            });

            // Gestisci il click sugli elementi della lista
            listView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
                String selectedWine = adapter.getChild(groupPosition, childPosition).toString();
                long aalastUpdate = 0;


                // Naviga verso il fragment successivo
                Bundle bundle = new Bundle();
                bundle.putString("selectedWine", selectedWine);

                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.bottleListFragment, bundle);

                return true;
            });

            // Carica i dati
            wineViewModel.loadCategories();

            return view;
        }

    private void formatCategoryAndWineNames() {
        // Mappa temporanea per la versione formattata
        HashMap<String, List<String>> formattedWinesMap = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : winesMap.entrySet()) {
            String originalCategory = entry.getKey();
            String formattedCategory = formatWineName(originalCategory); // Formatta il nome

            categoryMapping.put(formattedCategory, originalCategory); // Salva il legame tra formattato e originale

            List<String> formattedWines = new ArrayList<>();
            for (String wine : entry.getValue()) {
                formattedWines.add(formatWineName(wine)); // Formatta il nome del vino
            }

            formattedWinesMap.put(formattedCategory, formattedWines);
        }

        // Aggiorna la lista delle categorie per l'UI
        categories.clear();
        categories.addAll(formattedWinesMap.keySet());

        // Usa formattedWinesMap solo per l'UI
        winesMap.clear();
        winesMap.putAll(formattedWinesMap);
    }

    // Funzione di formattazione
    private String formatWineName(String wine) {
        return Arrays.stream(wine.split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
    }