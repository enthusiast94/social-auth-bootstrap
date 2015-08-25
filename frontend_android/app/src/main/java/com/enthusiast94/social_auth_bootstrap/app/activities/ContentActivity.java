package com.enthusiast94.social_auth_bootstrap.app.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.enthusiast94.social_auth_bootstrap.app.R;
import com.enthusiast94.social_auth_bootstrap.app.fragments.HomeFragment;
import com.enthusiast94.social_auth_bootstrap.app.fragments.UserProfileFragment;
import com.enthusiast94.social_auth_bootstrap.app.network.AuthManager;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

public class ContentActivity extends ActionBarActivity {

    private DrawerLayout drawerLayout;
    private FrameLayout contentLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView nameTextView;
    private TextView emailTextView;
    private ImageView avatarImageView;
    private android.os.Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        // bind handler to main thread's message queue
        handler = new Handler();

        /**
         * Find views
         */

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        contentLayout = (FrameLayout) findViewById(R.id.content_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        nameTextView = (TextView) navigationView.findViewById(R.id.textview_name);
        emailTextView = (TextView) navigationView.findViewById(R.id.textview_email);
        avatarImageView = (ImageView) navigationView.findViewById(R.id.imageview_avatar);

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
            nameTextView.setText(currentUser.getString("userName"));
            emailTextView.setText(currentUser.getString("userEmail"));
            Picasso.with(this).load(currentUser.getString("userAvatar")).into(avatarImageView);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem) {
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
                    drawerLayout.closeDrawers();

                    // close the drawer after some delay in order to prevent UI lag
                    final Fragment finalFragment = fragment;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(android.R.anim.fade_in, 0)
                                    .replace(contentLayout.getId(), finalFragment)
                                    .commit();
                            getSupportActionBar().setTitle(menuItem.getTitle());
                        }
                    }, 300);

                    menuItem.setChecked(true);
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        // noinspection SimplifiableIfStatement
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}
