package com.enthusiast94.social_auth_bootstrap.app.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.enthusiast94.social_auth_bootstrap.app.R;
import com.enthusiast94.social_auth_bootstrap.app.events.DeauthenticatedEvent;
import com.enthusiast94.social_auth_bootstrap.app.events.UserInfoUpdatedEvent;
import com.enthusiast94.social_auth_bootstrap.app.fragments.HomeFragment;
import com.enthusiast94.social_auth_bootstrap.app.fragments.UserProfileFragment;
import com.enthusiast94.social_auth_bootstrap.app.network.AuthManager;
import com.squareup.picasso.Picasso;
import de.greenrobot.event.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class ContentActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private FrameLayout contentLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView nameTextView;
    private TextView emailTextView;
    private ImageView avatarImageView;
    private android.os.Handler handler;
    private int selectedNavMenuItemIndex;
    private static final String SELECTED_NAV_MENU_ITEM_INDEX_KEY = "selectedNavMenuItemIndex";


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
         * Populate navigation view header with user data
         */

        populateNavViewHeader();

        /**
         * Handle navigation view menu item clicks by displaying appropriate fragments.
         */

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectNavMenuItem(menuItem);
                return false;
            }
        });

        if (savedInstanceState == null) {
            selectedNavMenuItemIndex = 0;
        } else {
            selectedNavMenuItemIndex = savedInstanceState.getInt(SELECTED_NAV_MENU_ITEM_INDEX_KEY);
        }

        selectNavMenuItem(navigationView.getMenu().getItem(selectedNavMenuItemIndex));
    }

    private void populateNavViewHeader() {
        JSONObject currentUser = AuthManager.getUserFromCache();
        try {
            nameTextView.setText(currentUser.getString("userName"));
            emailTextView.setText(currentUser.getString("userEmail"));
            Picasso.with(this).load(currentUser.getString("userAvatar")).into(avatarImageView);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(DeauthenticatedEvent event) {
        Intent goToLoginActivity = new Intent(this, LoginActivity.class);
        startActivity(goToLoginActivity);
        finish();
    }

    public void onEventMainThread(UserInfoUpdatedEvent event) {
        populateNavViewHeader();
    }

    private void selectNavMenuItem(MenuItem menuItem) {
        Fragment fragment = null;
        String tag = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                if (getSupportFragmentManager().findFragmentByTag(HomeFragment.TAG) == null) {
                    fragment = new HomeFragment();
                }

                tag = HomeFragment.TAG;
                break;
            case R.id.nav_user:
                if (getSupportFragmentManager().findFragmentByTag(UserProfileFragment.TAG) == null) {
                    fragment = new UserProfileFragment();
                }

                tag = UserProfileFragment.TAG;
                break;
        }

        // only perform fragment transaction if fragment is not already present
        if (fragment != null) {
            // perform fragment transaction after some delay in order to prevent UI lag
            handler.postDelayed(new ReplaceFragmentRunnable(fragment, tag), 300);
        }

        drawerLayout.closeDrawers();
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());

        selectedNavMenuItemIndex = getMenuItemIndex(menuItem, navigationView.getMenu());
    }

    private int getMenuItemIndex(MenuItem menuItem, Menu menu) {
        for (int i=0; i<menu.size(); i++) {
            MenuItem currentMenuItem = menu.getItem(i);
            if (currentMenuItem.getItemId() == menuItem.getItemId()) {
                return i;
            }
        }

        return -1;
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt(SELECTED_NAV_MENU_ITEM_INDEX_KEY, selectedNavMenuItemIndex);
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

    private class ReplaceFragmentRunnable implements Runnable {

        private Fragment fragment;
        private String tag;

        public ReplaceFragmentRunnable(Fragment fragment, String tag) {
            this.fragment = fragment;
            this.tag = tag;
        }

        @Override
        public void run() {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(contentLayout.getId(), fragment, tag)
                    .commit();
        }
    }
}
