package com.enthusiast94.social_auth_bootstrap.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.enthusiast94.social_auth_bootstrap.app.R;

/**
 * Created by manas on 25-08-2015.
 */
public class UserProfileFragment extends Fragment {

    public static final String TAG = UserProfileFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        return view;
    }

}
