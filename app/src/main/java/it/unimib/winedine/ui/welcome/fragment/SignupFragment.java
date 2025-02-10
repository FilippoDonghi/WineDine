package it.unimib.winedine.ui.welcome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;

import it.unimib.winedine.R;
import it.unimib.winedine.ui.welcome.viewmodel.UserViewModel;

public class SignupFragment {

    private UserViewModel userViewModel;
    private TextInputEditText textInputEmail, textInputPassword;


    public SignupFragment() {
        // Required empty public constructor
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.signup_fragment, container, false);
    }
}