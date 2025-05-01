package it.unimib.winedine.ui.home.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import it.unimib.winedine.R;
import it.unimib.winedine.adapter.WineAdapter;
import it.unimib.winedine.database.WineFireStoreDatabase;
import it.unimib.winedine.repository.user.IUserRepository;
import it.unimib.winedine.repository.wine.WinesRepository;
import it.unimib.winedine.ui.home.viewmodel.wine.WineViewModel;
import it.unimib.winedine.ui.home.viewmodel.wine.WineViewModelFactory;
import it.unimib.winedine.ui.welcome.viewmodel.UserViewModel;
import it.unimib.winedine.ui.welcome.viewmodel.UserViewModelFactory;
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
    private UserViewModel userViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        database = new WineFireStoreDatabase();

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());

        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        winesRepository = ServiceLocator.getInstance().getWinesRepository(
                requireActivity().getApplication(),
                requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
        );

        wineViewModel = new ViewModelProvider(
                requireActivity(),
                new WineViewModelFactory(winesRepository, userRepository)).get(WineViewModel.class);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_wine_list, container, false);

            listView = view.findViewById(R.id.expandableListView);

            adapter = new WineAdapter(requireContext(), categories, winesMap);
            listView.setAdapter(adapter);

            wineViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
                this.categories.clear();
                this.categories.addAll(categories);
                formatCategoryAndWineNames();
                adapter.updateCategories(this.categories);
            });

            wineViewModel.getWinesMap().observe(getViewLifecycleOwner(), winesMap -> {
                this.winesMap.clear();
                this.winesMap.putAll(winesMap);
                formatCategoryAndWineNames();
                adapter.updateWinesMap(this.winesMap);
            });

            wineViewModel.getError().observe(getViewLifecycleOwner(), errorMessage -> {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            });

            // Gestisci il click sugli elementi della lista
            listView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
                String selectedWine = adapter.getChild(groupPosition, childPosition).toString();

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