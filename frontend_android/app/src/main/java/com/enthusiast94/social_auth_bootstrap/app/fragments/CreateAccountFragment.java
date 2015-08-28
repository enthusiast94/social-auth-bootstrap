package com.enthusiast94.social_auth_bootstrap.app.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.enthusiast94.social_auth_bootstrap.app.R;
import com.enthusiast94.social_auth_bootstrap.app.events.AuthenticatedEvent;
import com.enthusiast94.social_auth_bootstrap.app.network.AuthManager;
import com.enthusiast94.social_auth_bootstrap.app.network.Callback;
import com.enthusiast94.social_auth_bootstrap.app.utils.Helpers;
import de.greenrobot.event.EventBus;
import org.json.JSONException;
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
    private ProgressDialog progressDialog;

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
         * Setup progress dialog which will be displayed while performing network operations
         */

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.message_please_wait));
        progressDialog.setCancelable(false);

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

                boolean doPasswordsMatch = password.equals(confirmPassword);

                if (!doPasswordsMatch) {
                    confirmPasswordEditText.setError(getResources().getString(R.string.error_passwords_do_not_match));
                }

                // if all input validations pass, send request to server
                if (nameError == null && emailError == null && passwordError == null && doPasswordsMatch) {
                    progressDialog.show();

                    Map<String, String> userDetails = new HashMap<String, String>();
                    userDetails.put("name", name);
                    userDetails.put("email", email);
                    userDetails.put("password", password);

                    AuthManager.basicAuth(userDetails, "new", new Callback() {
                        @Override
                        public void onSuccess(JSONObject data) {
                            try {
                                progressDialog.hide();

                                String userName = AuthManager.getUserFromCache().getString("userName");
                                EventBus.getDefault().post(new AuthenticatedEvent(userName));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, String message) {
                            progressDialog.hide();

                            Helpers.showSnackbar(rootView, "error", message, getResources());
                        }
                    });
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        // make sure to dismiss dialog in order to prevent 'leaked window' error
        progressDialog.dismiss();

        super.onDestroyView();
    }
}
