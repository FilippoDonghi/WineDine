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
import it.unimib.winedine.ui.home.HomeActivity;
import it.unimib.winedine.ui.welcome.WelcomeActivity;
import it.unimib.winedine.ui.welcome.viewmodel.UserViewModel;
import it.unimib.winedine.ui.welcome.viewmodel.UserViewModelFactory;
import it.unimib.winedine.util.Constants;
import it.unimib.winedine.util.ServiceLocator;
import it.unimib.winedine.util.SharedPreferencesUtils;

import android.os.Bundle;
import android.widget.Button;

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
                        .setServerClientId(getString(R.string.default_web_client_id))

                        .setFilterByAuthorizedAccounts(false)
                        .build())

                .setAutoSelectEnabled(true)
                .build();
        startIntentSenderForResult = new ActivityResultContracts.StartIntentSenderForResult();


        activityResultLauncher = registerForActivityResult(startIntentSenderForResult, activityResult -> {
            if (activityResult.getResultCode() == Activity.RESULT_OK) {
                Log.d(TAG, "result.getResultCode() == Activity.RESULT_OK");

                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(activityResult.getData());
                    String idToken = credential.getGoogleIdToken();

                    if (idToken !=  null) {
                        userViewModel.getGoogleUserMutableLiveData(idToken).observe(getViewLifecycleOwner(), authenticationResult -> {
                            if (authenticationResult.isSuccess()) {
                                User user = ((Result.UserSuccess) authenticationResult).getData();
                                //saveLoginData(user.getEmail(), null, user.getIdToken());
                                Log.i(TAG, "Logged as: " + user.getEmail());
                                userViewModel.setAuthenticationError(false);
                                goToNextPage(getView());
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (userViewModel.getLoggedUser() != null) {
            goToNextPage(view);
        }

        editTextEmail = view.findViewById(R.id.textInputEmail);
        editTextPassword = view.findViewById(R.id.textInputPassword);

        Button loginButton = view.findViewById(R.id.loginButton);
        Button signupButton = view.findViewById(R.id.buttonNewAccount);
        Button loginGoogleButton = view.findViewById(R.id.loginGoogleButton);

        loginButton.setOnClickListener(v -> {
            String email = editTextEmail.getText() != null ? editTextEmail.getText().toString() : "";
            String password = editTextPassword.getText() != null ? editTextPassword.getText().toString() : "";

            if (!isEmailOk(email)) {
                editTextEmail.setError(getString(R.string.error_email_login));
                return;
            }

            if (!isPasswordOk(password)) {
                editTextPassword.setError(getString(R.string.error_password_login));
                return;
            }

            // Usa getUser() con isUserRegistered=true per il login
            userViewModel.getUser(email, password, true)
                    .observe(getViewLifecycleOwner(), result -> {
                        if (result != null) {
                            if (result.isSuccess()) {
                                // Login riuscito
                                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_homeActivity);
                            } else {
                                // Mostra errore e stampa log
                                String errorMessage = getErrorMessage(((Result.Error) result).getMessage());

                                // Stampa il log dell'errore
                                Log.e("LoginError", "Credenziali errate: " + errorMessage);

                                Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
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

    private void goToNextPage(View view) {
        SharedPreferencesUtils sharedPreferencesUtil =
                new SharedPreferencesUtils(requireActivity().getApplication());

        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILENAME,
                SHARED_PREFERENCES_ID_TOKEN) != null){
            startActivity(new Intent(getContext(), HomeActivity.class));
            //Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeActivity);

        } else {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeActivity);
        }
    }

    private boolean isEmailOk(String email) {

        if (!EmailValidator.getInstance().isValid((email))) {
            editTextEmail.setError(getString(R.string.error_email_login));
            return false;
        } else {
            editTextEmail.setError(null);
            return true;
        }
    }

    private boolean isPasswordOk(String password) {
        if (password.isEmpty() || password.length() < Constants.MINIMUM_LENGTH_PASSWORD) {
            editTextPassword.setError(getString(R.string.error_invalid_password));
            return false;
        } else {
            editTextPassword.setError(null);
            return true;
        }
    }

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
    }



