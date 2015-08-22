package com.enthusiast94.social_auth_bootstrap.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.enthusiast94.social_auth_bootstrap.app.R;

/**
 * Created by manas on 22-08-2015.
 */
public class LoginFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_login, container, false);

        /**
         * Find views
         */

        EditText emailEditText = (EditText) view.findViewById(R.id.edittext_email);
        EditText passwordEditText = (EditText) view.findViewById(R.id.edittext_password);

        return view;
    }
}
