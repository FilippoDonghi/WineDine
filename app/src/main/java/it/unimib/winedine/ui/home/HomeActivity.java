package it.unimib.winedine.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;

import it.unimib.winedine.R;
import it.unimib.winedine.database.WineRoomDatabase;
import it.unimib.winedine.ui.welcome.viewmodel.UserViewModel;
import it.unimib.winedine.util.ServiceLocator;

public class HomeActivity extends AppCompatActivity {

    private NavController navController;
    private UserViewModel userViewModel;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerView);
        navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.wineListFragment, R.id.favoriteWinesFragment
        ).build();

        NavigationUI.setupWithNavController(bottomNav, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Aggiunto dentro il metodo onCreate
        ImageButton profileButton = findViewById(R.id.profile_menu);
        profileButton.setOnClickListener(view -> showProfileMenu(view));

    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp();
    }

    private void showProfileMenu(View anchorView) {
        PopupMenu popupMenu = new PopupMenu(HomeActivity.this, anchorView);
        popupMenu.inflate(R.menu.menu_profile);

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.action_favorites) {
               // navController.navigate(R.id.favoritesFragment);
                return true;
            }
            else if (id == R.id.action_logout) {
               userViewModel.logout();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void performLogout() {
        // Implementa la logica di logout
    }
}