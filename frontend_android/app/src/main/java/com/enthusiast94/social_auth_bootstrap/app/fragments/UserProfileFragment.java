package com.enthusiast94.social_auth_bootstrap.app.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import com.enthusiast94.social_auth_bootstrap.app.R;
import com.enthusiast94.social_auth_bootstrap.app.events.DeauthenticatedEvent;
import com.enthusiast94.social_auth_bootstrap.app.network.AuthManager;
import com.enthusiast94.social_auth_bootstrap.app.network.Callback;
import de.greenrobot.event.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

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

        /**
         * Setup progress dialog which will be displayed for several network actions
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
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(int statusCode, String message) {
                Snackbar.make(
                        rootView,
                        getResources().getString(R.string.error_base) + message + " [" + statusCode + "]",
                        Snackbar.LENGTH_LONG
                ).show();
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

                        Snackbar.make(
                                rootView,
                                getResources().getString(R.string.error_base) + message + " [" + statusCode + "]",
                                Snackbar.LENGTH_LONG
                        ).show();
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
