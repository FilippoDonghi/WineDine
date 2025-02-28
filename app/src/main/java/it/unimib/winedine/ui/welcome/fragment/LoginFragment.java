package it.unimib.winedine.ui.welcome.fragment;

import static it.unimib.winedine.util.Constants.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import org.apache.commons.validator.routines.EmailValidator;

import it.unimib.winedine.R;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.model.User;
import it.unimib.winedine.repository.user.IUserRepository;
import it.unimib.winedine.ui.welcome.WelcomeActivity;
import it.unimib.winedine.ui.welcome.viewmodel.UserViewModel;
import it.unimib.winedine.ui.welcome.viewmodel.UserViewModelFactory;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.ServiceLocator;
import it.unimib.winedine.util.SharedPreferencesUtils;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginFragment extends Fragment {

    private final static String TAG = LoginFragment.class.getSimpleName();

    private TextInputEditText editTextEmail, editTextPassword;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private ActivityResultContracts.StartIntentSenderForResult startIntentSenderForResult;
    private UserViewModel userViewModel;

    public LoginFragment() {}

    private String getErrorMessage(String errorType) {
        switch (errorType) {
            case INVALID_CREDENTIALS_ERROR:
                return requireActivity().getString(R.string.error_password_login);
            case INVALID_USER_ERROR:
                return requireActivity().getString(R.string.error_email_login);
            default:
                return requireActivity().getString(R.string.error_unexpected);
        }
    }
    private void retrieveUserInformationAndStartActivity(User user, View view) {
        userViewModel.getUserPreferences(user.getIdToken()).observe(
                getViewLifecycleOwner(), userPreferences -> {
                    //The viewmodel updated sharedprefs
                    goToNextPage(view);
                }
        );
    }

    private void goToNextPage(View view) {
        //questa riga l'ho aggiunta io
        //startActivity(new Intent(getContext(), WelcomeActivity.class ));
        /**
        SharedPreferencesUtils sharedPreferencesUtil =
                new SharedPreferencesUtils(requireActivity().getApplication());

        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILENAME,
                SHARED_PREFERENCES_COUNTRY_OF_INTEREST) != null &&
                sharedPreferencesUtil.readStringSetData(SHARED_PREFERENCES_FILENAME,
                        SHARED_PREFERENCES_CATEGORIES_OF_INTEREST) != null) {

            startActivity(new Intent(getContext(), HomeActivity.class));
        } else {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_pickCountryFragment);
        }**/
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        oneTapClient = Identity.getSignInClient(requireActivity());
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();
        startIntentSenderForResult = new ActivityResultContracts.StartIntentSenderForResult();

        //questo serve per farmi restituire l'account che ho scelto nel login con google
        activityResultLauncher = registerForActivityResult(startIntentSenderForResult, activityResult -> {
            if (activityResult.getResultCode() == Activity.RESULT_OK) {
                Log.d(TAG, "result.getResultCode() == Activity.RESULT_OK");

                //quando seleziono l'account entro in questo try catch
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(activityResult.getData());
                    String idToken = credential.getGoogleIdToken();

                    if (idToken !=  null) {
                       //restituiscimi i dati relativi all'account di google
                        userViewModel.getGoogleUserMutableLiveData(idToken).observe(getViewLifecycleOwner(), authenticationResult -> {
                            if (authenticationResult.isSuccess()) {
                                User user = ((Result.UserSuccess) authenticationResult).getData();
                                //saveLoginData(user.getEmail(), null, user.getIdToken());
                                Log.i(TAG, "Logged as: " + user.getEmail());
                                userViewModel.setAuthenticationError(false);
                                retrieveUserInformationAndStartActivity(user, getView());
                            } else {
                                userViewModel.setAuthenticationError(true);
                                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                        getErrorMessage(((Result.Error) authenticationResult).getMessage()),
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (ApiException e) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            requireActivity().getString(R.string.error_unexpected),
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }





    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://winedine-6b29b-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

         if (userViewModel.getLoggedUser() != null) {
           goToNextPage(view);}

        editTextEmail = view.findViewById(R.id.textInputEmail);
        editTextPassword = view.findViewById(R.id.textInputPassword);

        Button loginButton = view.findViewById(R.id.loginButton);
        Button signupButton = view.findViewById(R.id.buttonNewAccount);
        Button loginGoogleButton = view.findViewById(R.id.loginGoogleButton);

        loginButton.setOnClickListener(v -> {
            String email = editTextEmail.getText() != null ? editTextEmail.getText().toString().trim() : "";
            String password = editTextPassword.getText() != null ? editTextPassword.getText().toString().trim() : "";

            boolean isValid = true;

            if (!isEmailOk(email)) {
                editTextEmail.setError(getString(R.string.error_email_login));
                isValid = false;
            }

            if (!isPasswordOk(password)) {
                editTextPassword.setError(getString(R.string.error_password_login));
                isValid = false;
            }

            if (isValid) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Login avvenuto con successo, naviga alla Home
                                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_homeActivity);
                            } else {
                                // Controlla il tipo di errore
                                Exception e = task.getException();
                                if (e instanceof FirebaseAuthInvalidUserException) {
                                    // L'account non esiste
                                    editTextEmail.setError("Account non trovato. Registrati prima di accedere.");
                                } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    // Password errata
                                    editTextPassword.setError("Password errata. Riprova.");
                                } else {
                                    // Altro errore (es. connessione assente)
                                    Toast.makeText(v.getContext(), "Errore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });


        loginGoogleButton.setOnClickListener(v -> oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        Log.d(TAG, "onSuccess from oneTapClient.beginSignIn(BeginSignInRequest)");
                        IntentSenderRequest intentSenderRequest =
                                new IntentSenderRequest.Builder(result.getPendingIntent()).build();
                        activityResultLauncher.launch(intentSenderRequest);
                    }
                })
                .addOnFailureListener(requireActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.d(TAG, e.getLocalizedMessage());

                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                requireActivity().getString(R.string.error_unexpected),
                                Snackbar.LENGTH_SHORT).show();
                    }
                }));
        signupButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_signupFragment);
        });
    }

    private boolean isEmailOk(String email) {
        // Check if the email is valid through the use of this library:
        // https://commons.apache.org/proper/commons-validator/

        if (!EmailValidator.getInstance().isValid((email))) {
            editTextEmail.setError(getString(R.string.error_email_login));
            return false;
        } else {
            editTextEmail.setError(null);
            return true;
        }
    }

    /**
     * Checks if the password is not empty.
     * @param password The password to be checked
     * @return True if the password has at least 6 characters, false otherwise
     */
    private boolean isPasswordOk(String password) {
        // Check if the password length is correct
        if (password.isEmpty() || password.length() < Constants.MINIMUM_LENGTH_PASSWORD) {
            editTextPassword.setError(getString(R.string.error_password_login));
            return false;
        } else {
            editTextPassword.setError(null);
            return true;
        }
    }
    }



