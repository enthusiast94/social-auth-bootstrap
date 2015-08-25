package com.enthusiast94.social_auth_bootstrap.app.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.enthusiast94.social_auth_bootstrap.app.R;
import com.enthusiast94.social_auth_bootstrap.app.events.AuthenticatedEvent;
import com.enthusiast94.social_auth_bootstrap.app.events.OauthCallbackEvent;
import com.enthusiast94.social_auth_bootstrap.app.events.OauthLoginButtonClickedEvent;
import com.enthusiast94.social_auth_bootstrap.app.fragments.CreateAccountFragment;
import com.enthusiast94.social_auth_bootstrap.app.fragments.LoginFragment;
import com.enthusiast94.social_auth_bootstrap.app.fragments.OauthHelperFragment;
import com.enthusiast94.social_auth_bootstrap.app.network.AuthManager;
import com.enthusiast94.social_auth_bootstrap.app.network.Callback;
import de.greenrobot.event.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();
    private FrameLayout rootView;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AuthManager.isAuthenticated()) {
            startContentActivity();
        } else {
            setContentView(R.layout.activity_login);

            /**
             * Find views
             */

            rootView = (FrameLayout) findViewById(R.id.root_view);
            toolbar = (Toolbar) findViewById(R.id.app_bar);
            tabLayout = (TabLayout) findViewById(R.id.tab_layout);
            viewPager = (ViewPager) findViewById(R.id.view_pager);

            /**
             * Setup AppBar
             */

            setSupportActionBar(toolbar);

            ActionBar appBar = getSupportActionBar();
            if (appBar != null) {
                appBar.setHomeButtonEnabled(true);
            }

            /**
             * Setup view pager to work with tabs
             */

            viewPager.setAdapter(new LoginPagerAdapter(getSupportFragmentManager(), getResources()));
            tabLayout.setupWithViewPager(viewPager);
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

    public void onEventMainThread(OauthLoginButtonClickedEvent event) {
        OauthHelperFragment oauthHelperFragment = OauthHelperFragment.newInstance(event.getUrlToLoad());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.root_view, oauthHelperFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void onEventMainThread(OauthCallbackEvent event) {
        if (event.getError() == null) {
            Map<String, String> userDetails = new HashMap<String, String>();
            userDetails.put("userId", event.getUserId());
            userDetails.put("accessToken", event.getAccessToken());

            AuthManager.oauth(userDetails, new Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    try {
                        String userName = AuthManager.getUserFromCache().getString("userName");
                        EventBus.getDefault().post(new AuthenticatedEvent(userName));
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
        } else {
            Snackbar.make(
                    rootView,
                    getResources().getString(R.string.error_base) + event.getError(),
                    Snackbar.LENGTH_SHORT
            ).show();
        }
    }

    public void onEventMainThread(AuthenticatedEvent event) {
        Toast.makeText(this, getResources().getString(R.string.success_login_base) + event.getUserName(), Toast.LENGTH_LONG)
                .show();
        startContentActivity();
    }

    private void startContentActivity() {
        Intent goToContentActivity = new Intent(this, ContentActivity.class);
        startActivity(goToContentActivity);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    private static class LoginPagerAdapter extends FragmentPagerAdapter {

        private static final int NUM_TABS = 2;
        private Resources res;

        public LoginPagerAdapter(FragmentManager fm, Resources res) {
            super(fm);
            this.res = res;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new LoginFragment();
                case 1:
                    return new CreateAccountFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return res.getString(R.string.tab_login);
                case 1:
                    return res.getString(R.string.tab_create_account);
                default:
                    return null;
            }
        }
    }
}
