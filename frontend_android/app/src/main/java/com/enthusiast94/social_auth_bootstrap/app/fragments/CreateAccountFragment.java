package com.enthusiast94.social_auth_bootstrap.app.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.enthusiast94.social_auth_bootstrap.app.R;
import com.enthusiast94.social_auth_bootstrap.app.network.AuthManager;
import com.enthusiast94.social_auth_bootstrap.app.network.Callback;
import com.enthusiast94.social_auth_bootstrap.app.utils.Helpers;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by manas on 22-08-2015.
 */
public class CreateAccountFragment extends Fragment {

    private View rootView;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button createAccountButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);

        /**
         * Find views
         */

        rootView = view.getRootView();
        nameEditText = (EditText) view.findViewById(R.id.edittext_name);
        emailEditText = (EditText) view.findViewById(R.id.edittext_email);
        passwordEditText = (EditText) view.findViewById(R.id.edittext_password);
        confirmPasswordEditText = (EditText) view.findViewById(R.id.edittext_confirm_password);
        createAccountButton = (Button) view.findViewById(R.id.button_create_account);

        /**
         * Setup event listeners
         */

        createAccountButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Helpers.hideSoftKeyboard(getActivity(), rootView.getWindowToken());

                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                String nameError = Helpers.validateName(name, getResources());
                String emailError = Helpers.validateEmail(email, getResources());
                String passwordError = Helpers.validatePassword(password, getResources());

                if (nameError != null) {
                    nameEditText.setError(nameError);
                }

                if (emailError != null) {
                    emailEditText.setError(emailError);
                }

                if (passwordError != null) {
                    passwordEditText.setError(passwordError);
                }

                if (!password.equals(confirmPassword)) {
                    confirmPasswordEditText.setError(getResources().getString(R.string.error_passwords_do_not_match));
                } else {
                    // if all input validations pass, send request to server
                    if (nameError == null && emailError == null && passwordError == null) {
                        Map<String, String> userDetails = new HashMap<String, String>();
                        userDetails.put("name", name);
                        userDetails.put("email", email);
                        userDetails.put("password", password);

                        AuthManager.basicAuth(userDetails, "new", new Callback() {
                            @Override
                            public void onSuccess(JSONObject data) {
                                Snackbar.make(rootView, "success", Snackbar.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int statusCode, String message) {
                                Snackbar.make(rootView, getResources().getString(R.string.error_base) + message, Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });

        return view;
    }
}
