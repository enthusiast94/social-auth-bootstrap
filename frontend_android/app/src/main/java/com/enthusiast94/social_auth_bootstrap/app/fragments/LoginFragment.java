package com.enthusiast94.social_auth_bootstrap.app.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.enthusiast94.social_auth_bootstrap.app.R;
import com.enthusiast94.social_auth_bootstrap.app.events.AuthenticatedEvent;
import com.enthusiast94.social_auth_bootstrap.app.events.OauthLoginButtonClickedEvent;
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
public class LoginFragment extends Fragment {

    private View rootView;
    private ProgressBar progressBar;
    private LinearLayout formLayout;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button googleButton;
    private Button facebookButton;
    private Button githubButton;
    private Button linkedinButton;
    private JSONObject oauth2Urls;
    private ProgressDialog progressDialog;

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
        googleButton = (Button) view.findViewById(R.id.button_google);
        facebookButton = (Button) view.findViewById(R.id.button_facebook);
        githubButton = (Button) view.findViewById(R.id.button_github);
        linkedinButton = (Button) view.findViewById(R.id.button_linkedin);

        /**
         * Setup progress dialog which will be displayed while performing network operations
         */

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.message_please_wait));
        progressDialog.setCancelable(false);

        /**
         * Fetch OAuth2 urls and then make the form visible. These OAuth2 urls will be used for the social sign in
         * buttons.
         */

        AuthManager.getAllOauth2Urls(new Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                oauth2Urls = data;

                progressBar.setVisibility(View.GONE);
                formLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, String message) {
                Helpers.showSnackbar(rootView, "error", message, getResources());
            }
        });

        /**
         * Handle login button click by validation the form input and sending the authentication request to the server
         * if validation passes.
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
                    progressDialog.show();

                    Map<String, String> userDetails = new HashMap<String, String>();
                    userDetails.put("email", email);
                    userDetails.put("password", password);

                    AuthManager.basicAuth(userDetails, "existing", new Callback() {
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

        /**
         * Handle oauth button click events by posting an event along with the appropriate URL. This event is then
         * used by the parent activity to load the provided URL into the web view fragment so that user can proceed
         * with the login process.
         */

        View.OnClickListener oauthLoginButtonClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    String urlToLoad = null;

                    switch (view.getId()) {
                        case R.id.button_google:
                            urlToLoad = oauth2Urls.getString("google");
                            break;
                        case R.id.button_facebook:
                            urlToLoad = oauth2Urls.getString("facebook");
                            break;
                        case R.id.button_github:
                            urlToLoad = oauth2Urls.getString("github");
                            break;
                        case R.id.button_linkedin:
                            urlToLoad = oauth2Urls.getString("linkedin");
                            break;
                    }

                    EventBus.getDefault().post(new OauthLoginButtonClickedEvent(urlToLoad));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        googleButton.setOnClickListener(oauthLoginButtonClickListener);
        facebookButton.setOnClickListener(oauthLoginButtonClickListener);
        githubButton.setOnClickListener(oauthLoginButtonClickListener);
        linkedinButton.setOnClickListener(oauthLoginButtonClickListener);

        return view;
    }

    @Override
    public void onDestroyView() {
        // make sure to dismiss dialog in order to prevent 'leaked window' error
        progressDialog.dismiss();

        super.onDestroyView();
    }
}
