package com.enthusiast94.social_auth_bootstrap.app.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.enthusiast94.social_auth_bootstrap.app.R;
import com.enthusiast94.social_auth_bootstrap.app.fragments.HomeFragment;
import com.enthusiast94.social_auth_bootstrap.app.fragments.UserProfileFragment;
import com.enthusiast94.social_auth_bootstrap.app.network.AuthManager;
import org.json.JSONException;
import org.json.JSONObject;

public class ContentActivity extends ActionBarActivity {

    private DrawerLayout drawerLayout;
    private FrameLayout contentLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView userNameTextView;
    private TextView emailTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        /**
         * Find views
         */

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        contentLayout = (FrameLayout) findViewById(R.id.content_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        userNameTextView = (TextView) navigationView.findViewById(R.id.textview_user_name);
        emailTextView = (TextView) navigationView.findViewById(R.id.textview_email);

        /**
         * Setup AppBar
         */

        setSupportActionBar(toolbar);

        ActionBar appBar = getSupportActionBar();
        if (appBar != null) {
            appBar.setHomeButtonEnabled(true);
            appBar.setDisplayHomeAsUpEnabled(true);
        }

        /**
         * Setup actionbar drawer toggle to work with app bar
         */

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        actionBarDrawerToggle.syncState();

        /**
         * Setup navigation view
         */

        JSONObject currentUser = AuthManager.getUserFromCache();
        try {
            userNameTextView.setText(currentUser.getString("userName"));
            emailTextView.setText(currentUser.getString("userEmail"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Fragment fragment = null;

                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.nav_user:
                        fragment = new UserProfileFragment();
                        break;
                }

                if (fragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(contentLayout.getId(), fragment)
                            .commit();

                    drawerLayout.closeDrawers();
                    menuItem.setChecked(true);
                    getSupportActionBar().setTitle(menuItem.getTitle());
                }

                return true;
            }
        };

        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);

        // display home fragment on startup
        navigationItemSelectedListener.onNavigationItemSelected(navigationView.getMenu().getItem(0));

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
