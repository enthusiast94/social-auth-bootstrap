package com.enthusiast94.social_auth_bootstrap.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.enthusiast94.social_auth_bootstrap.app.App;
import com.enthusiast94.social_auth_bootstrap.app.network.AuthManager;

/**
 * Created by manas on 23-08-2015.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AuthManager.isAuthenticated()) {
            // TODO remove toast and start another activity
            Toast.makeText(App.getAppContext(), "Already logged in", Toast.LENGTH_LONG).show();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish();
    }
}
