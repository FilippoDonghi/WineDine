package it.unimib.winedine.ui.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.os.HandlerCompat;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import it.unimib.winedine.R;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.repository.user.IUserRepository;
import it.unimib.winedine.ui.welcome.WelcomeActivity;
import it.unimib.winedine.ui.welcome.viewmodel.UserViewModel;
import it.unimib.winedine.ui.welcome.viewmodel.UserViewModelFactory;
import it.unimib.winedine.util.ServiceLocator;


public class HomeActivity extends AppCompatActivity {

    private NavController navController;
    private UserViewModel userViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IUserRepository userRepository = ServiceLocator.getInstance()
                .getUserRepository(getApplication());
        userViewModel = new ViewModelProvider(
                this,
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        if (userViewModel.getLoggedUser() == null) {
            navigateToLoginScreen();
            return;
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerView);
        navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.wineListFragment, R.id.favoriteWinesFragment
        ).build();

        NavigationUI.setupWithNavController(bottomNav, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        ImageButton profileButton = findViewById(R.id.profile_menu);
        profileButton.setOnClickListener(view -> showProfileMenu(view));

    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController != null && navController.navigateUp();
    }

    @Override
    public void onBackPressed() {
        if (navController == null || !navController.navigateUp()) {
            super.onBackPressed();
        }
    }

    private void showProfileMenu(View anchorView) {

        PopupMenu popupMenu = new PopupMenu(this, anchorView);
        popupMenu.inflate(R.menu.menu_profile);

// Cambia il colore dell'icona
        MenuItem logoutItem = popupMenu.getMenu().findItem(R.id.action_logout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            logoutItem.setIconTintList(ColorStateList.valueOf(Color.WHITE));
        }

// Cambia il colore del testo
        logoutItem.setTitle(HtmlCompat.fromHtml(
                "<font color='#FFFFFF'>Logout</font>", HtmlCompat.FROM_HTML_MODE_LEGACY
        ));

        popupMenu.setForceShowIcon(true);

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.action_logout) {
                // Mostra un dialog di conferma
                new AlertDialog.Builder(HomeActivity.this)
                        .setMessage(R.string.logout_confirmation)
                        .setPositiveButton(R.string.yes, (dialog, which) -> performLogout())
                        .setNegativeButton(R.string.no, null)
                        .show();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void performLogout() {
            // 1. Crea il builder usando AndroidX
            androidx.appcompat.app.AlertDialog.Builder builder =
                    new androidx.appcompat.app.AlertDialog.Builder(this);

            // 2. Configura il dialog
            builder.setView(R.layout.dialog_progess);
            builder.setCancelable(false);

            // 3. Crea e mostra il dialog
            androidx.appcompat.app.AlertDialog progressDialog = builder.create();
            progressDialog.show();

        // Aggiungi timeout UI
        HandlerCompat.createAsync(Looper.getMainLooper()).postDelayed(() -> {
            // Il tuo codice timeout qui
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                showLogoutError(getString(R.string.logout_timeout));
            }
        }, 15000);
    // 15 secondi timeout

        userViewModel.logout().observe(this, result -> {
            progressDialog.dismiss();

            if (result instanceof Result.UserSuccess) {
                navigateToLoginScreen();
            } else {
                String errorMsg = result instanceof Result.Error ?
                        ((Result.Error) result).getMessage() : getString(R.string.logout_error);
                showLogoutError(errorMsg);
            }
        });
    }

    private void showLogoutError(String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.error_title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (d, w) -> {
                    // Eventuale azione aggiuntiva
                })
                .show();
    }


    private void navigateToLoginScreen() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
