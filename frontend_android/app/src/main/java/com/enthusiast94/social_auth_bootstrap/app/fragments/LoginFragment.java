package com.enthusiast94.social_auth_bootstrap.app.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
public class LoginFragment extends Fragment {

    private View rootView;
    private ProgressBar progressBar;
    private LinearLayout formLayout;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_login, container, false);

        /**
         * Find views
         */

        rootView = view.getRootView();
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        formLayout = (LinearLayout) view.findViewById(R.id.form_layout);
        emailEditText = (EditText) view.findViewById(R.id.edittext_email);
        passwordEditText = (EditText) view.findViewById(R.id.edittext_password);
        loginButton = (Button) view.findViewById(R.id.button_login);

        /**
         * Fetch OAuth2 urls and then make the form visible. These OAuth2 urls will be used for the social sign in
         * buttons.
         */

        AuthManager.getAllOauth2Urls(new Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                System.out.println(data.toString());
                progressBar.setVisibility(View.GONE);
                formLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, String message) {
                Snackbar.make(
                        rootView,
                        getResources().getString(R.string.error_base) + message + " [" + statusCode + "]",
                        Snackbar.LENGTH_LONG
                );
            }
        });

        /**
         * Setup event listeners
         */

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Helpers.hideSoftKeyboard(getActivity(), rootView.getWindowToken());

                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                String emailError = Helpers.validateEmail(email, getResources());
                String passwordError = Helpers.validatePassword(password, getResources());

                if (emailError != null) {
                    emailEditText.setError(emailError);
                }

                if (passwordError != null) {
                    passwordEditText.setError(passwordError);
                }

                // if all input validations pass, send request to server
                if (emailError == null && passwordError == null) {
                    Map<String, String> userDetails = new HashMap<String, String>();
                    userDetails.put("email", email);
                    userDetails.put("password", password);

                    AuthManager.basicAuth(userDetails, "existing", new Callback() {
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
        });

        return view;
    }
}
