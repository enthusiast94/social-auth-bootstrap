package com.enthusiast94.social_auth_bootstrap.app.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import com.enthusiast94.social_auth_bootstrap.app.R;
import com.enthusiast94.social_auth_bootstrap.app.events.DeauthenticatedEvent;
import com.enthusiast94.social_auth_bootstrap.app.events.UserInfoUpdatedEvent;
import com.enthusiast94.social_auth_bootstrap.app.network.AuthManager;
import com.enthusiast94.social_auth_bootstrap.app.network.Callback;
import com.enthusiast94.social_auth_bootstrap.app.utils.Helpers;
import de.greenrobot.event.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by manas on 25-08-2015.
 */
public class UserProfileFragment extends Fragment {

    public static final String TAG = UserProfileFragment.class.getSimpleName();
    private FrameLayout rootView;
    private LinearLayout contentLayout;
    private ProgressBar progressBar;
    private EditText nameEditText;
    private EditText emailEditText;
    private Button updateButton;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button changePasswordButton;
    private Button deleteAccountButton;
    private LinearLayout linkedAccountsContainer;
    private LinearLayout noLinkedAccountsContainer;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        // enable options menu
        setHasOptionsMenu(true);

        /**
         * Find views
         */

        rootView = (FrameLayout) view.findViewById(R.id.root_view);
        contentLayout = (LinearLayout) view.findViewById(R.id.content_layout);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        nameEditText = (EditText) view.findViewById(R.id.edittext_name);
        emailEditText = (EditText) view.findViewById(R.id.edittext_email);
        updateButton = (Button) view.findViewById(R.id.button_update);
        passwordEditText = (EditText) view.findViewById(R.id.edittext_new_password);
        confirmPasswordEditText = (EditText) view.findViewById(R.id.edittext_confirm_password);
        changePasswordButton = (Button) view.findViewById(R.id.button_change_password);
        linkedAccountsContainer = (LinearLayout) view.findViewById(R.id.linked_accounts_container);
        noLinkedAccountsContainer = (LinearLayout) view.findViewById(R.id.no_linked_accounts_container);
        deleteAccountButton = (Button) view.findViewById(R.id.button_delete_account);

        /**
         * Setup progress dialog which will be displayed while performing network operations
         */

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.message_please_wait));
        progressDialog.setCancelable(false);

        /**
         * Fetch and display user info
         */

        AuthManager.getUser(null, new Callback() {

            @Override
            public void onSuccess(JSONObject data) {
                try {
                    progressBar.setVisibility(View.INVISIBLE);
                    contentLayout.setVisibility(View.VISIBLE);

                    nameEditText.setText(data.getString("name"));
                    emailEditText.setText(data.getString("email"));

                    // populate linked accounts
                    final JSONArray linkedAccounts = data.getJSONArray("linkedAccounts");

                    if (linkedAccounts.length() == 0) {
                        linkedAccountsContainer.setVisibility(View.INVISIBLE);
                        noLinkedAccountsContainer.setVisibility(View.VISIBLE);
                    } else {
                        for (int i=0; i<linkedAccounts.length(); i++) {
                            JSONObject linkedAccount = linkedAccounts.getJSONObject(i);
                            final View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_linked_accounts,
                                    linkedAccountsContainer, false);

                            final TextView providerNameTextView = (TextView) itemView.findViewById(R.id.textview_provider_info);
                            final String providerName = linkedAccount.getString("providerName");
                            String userEmail = linkedAccount.getString("userEmail");
                            providerNameTextView.setText(Character.toUpperCase(providerName.charAt(0)) +
                                    providerName.substring(1, providerName.length()) + " (" + userEmail + ")");

                            ImageButton deleteButton = (ImageButton) itemView.findViewById(R.id.button_delete_linked_account);
                            deleteButton.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {
                                    final Callback unlinAccountCallback = new Callback() {

                                        @Override
                                        public void onSuccess(JSONObject data) {
                                            progressDialog.hide();

                                            linkedAccountsContainer.removeView(itemView);
                                            Helpers.showSnackbar(rootView, "success", providerName + " " +
                                                    getResources().getString(R.string.success_account_unlinked), getResources());

                                            // if there's only one child view left (heading label), then show 'no linked
                                            // accounts' message
                                            if (linkedAccountsContainer.getChildCount() == 1) {
                                                linkedAccountsContainer.setVisibility(View.INVISIBLE);
                                                noLinkedAccountsContainer.setVisibility(View.VISIBLE);
                                            }
                                        }

                                        @Override
                                        public void onFailure(int statusCode, String message) {
                                            progressDialog.hide();

                                            Helpers.showSnackbar(rootView, "error", message, getResources());
                                        }
                                    };

                                    // display a confirmation dialog if the last linked account is being unlinked
                                    if (linkedAccountsContainer.getChildCount() == 2) {
                                        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                                .setMessage(R.string.warning_last_linked_account)
                                                .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        progressDialog.show();

                                                        AuthManager.unlinkAccount(providerName, unlinAccountCallback);
                                                    }
                                                })
                                                .setNegativeButton(R.string.action_cancel, null)
                                                .setTitle(R.string.label_warning)
                                                .create();
                                        dialog.show();
                                    } else {
                                        progressDialog.show();

                                        AuthManager.unlinkAccount(providerName, unlinAccountCallback);
                                    }
                                }
                            });

                            linkedAccountsContainer.addView(itemView);
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(int statusCode, String message) {
                Helpers.showSnackbar(rootView, "error", message, getResources());
            }
        });

        /**
         * Bind event listeners
         */

        // handle update button click event
        updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Helpers.hideSoftKeyboard(getActivity(), rootView.getWindowToken());

                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();

                String nameError = Helpers.validateName(name, getResources());
                String emailError = Helpers.validateEmail(email, getResources());

                if (nameError != null) {
                    nameEditText.setError(nameError);
                }

                if (emailError != null) {
                    emailEditText.setError(emailError);
                }

                if (nameError == null && emailError == null) {
                    progressDialog.show();

                    Map<String, String> userDetails = new HashMap<String, String>();
                    userDetails.put("name", name);
                    userDetails.put("email", email);

                    AuthManager.updateAccount(userDetails, new Callback() {
                        @Override
                        public void onSuccess(JSONObject data) {
                            progressDialog.hide();

                            Helpers.showSnackbar(rootView, "success", R.string.success_account_information_updated, getResources());

                            EventBus.getDefault().post(new UserInfoUpdatedEvent());

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

        // handle change password button click event
        changePasswordButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Helpers.hideSoftKeyboard(getActivity(), rootView.getWindowToken());

                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                String passwordError = Helpers.validatePassword(password, getResources());

                if (passwordError != null) {
                    passwordEditText.setError(passwordError);
                }

                boolean doPasswordsMatch = password.equals(confirmPassword);

                if (!doPasswordsMatch) {
                    confirmPasswordEditText.setError(getResources().getString(R.string.error_passwords_do_not_match));
                }

                if (passwordError == null && doPasswordsMatch) {
                    progressDialog.show();

                    Map<String, String> userDetails = new HashMap<String, String>();
                    userDetails.put("password", password);

                    AuthManager.updateAccount(userDetails, new Callback() {
                        @Override
                        public void onSuccess(JSONObject data) {
                            progressDialog.hide();

                            Helpers.showSnackbar(rootView, "success", R.string.success_password_changed, getResources());
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

        // handle delete account button click event
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                progressDialog.show();

                AuthManager.deleteAccount(new Callback() {

                    @Override
                    public void onSuccess(JSONObject data) {
                        progressDialog.hide();

                        Toast.makeText(getActivity(), R.string.success_account_deleted, Toast.LENGTH_SHORT)
                                .show();

                        EventBus.getDefault().post(new DeauthenticatedEvent());
                    }

                    @Override
                    public void onFailure(int statusCode, String message) {
                        progressDialog.hide();

                        Helpers.showSnackbar(rootView, "error", message, getResources());
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_user_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                progressDialog.show();

                AuthManager.deauth(new Callback() {

                    @Override
                    public void onSuccess(JSONObject data) {
                        progressDialog.hide();

                        EventBus.getDefault().post(new DeauthenticatedEvent());
                    }

                    @Override
                    public void onFailure(int statusCode, String message) {
                        progressDialog.hide();

                        Helpers.showSnackbar(rootView, "error", message, getResources());
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroyView() {
        // make sure to dismiss dialog in order to prevent 'leaked window' error
        progressDialog.dismiss();

        super.onDestroyView();
    }
}
