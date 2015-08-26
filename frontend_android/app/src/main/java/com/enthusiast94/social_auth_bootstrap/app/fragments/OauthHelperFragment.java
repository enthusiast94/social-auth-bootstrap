package com.enthusiast94.social_auth_bootstrap.app.fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.enthusiast94.social_auth_bootstrap.app.R;
import com.enthusiast94.social_auth_bootstrap.app.events.OauthCallbackEvent;
import com.enthusiast94.social_auth_bootstrap.app.network.AuthManager;
import com.enthusiast94.social_auth_bootstrap.app.utils.Helpers;
import de.greenrobot.event.EventBus;

import java.util.Map;

/**
 * Created by manas on 24-08-2015.
 */
public class OauthHelperFragment extends Fragment {

    private static final String TAG = OauthHelperFragment.class.getSimpleName();
    public static final String URL_TO_LOAD_KEY = "urlToLoadKey";
    private WebView webView;
    private ProgressDialog progressDialog;

    public static OauthHelperFragment newInstance(String urlToLoad) {
        OauthHelperFragment oauthHelperFragment = new OauthHelperFragment();
        Bundle bundle = new Bundle();
        bundle.putString(URL_TO_LOAD_KEY, urlToLoad);
        oauthHelperFragment.setArguments(bundle);

        return oauthHelperFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_oauth_helper, container, false);

        // find views
        webView = (WebView) view.findViewById(R.id.webview);

        // configure browser settings
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);

        // setup progress dialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.message_please_wait));
        progressDialog.setCancelable(false);

        // configure the client to use when opening URLs
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(AuthManager.OAUTH_CLIENT_REDIRECT_URI_BASE)) {
                    // extract query params from URL
                    Map<String, String> params = Helpers.parseQueryParams(url);

                    EventBus.getDefault().post(new OauthCallbackEvent(
                            params.get("userId"),
                            params.get("accessToken"),
                            params.get("error")
                    ));

                    // imitate BACK button press in order to pop back stack
                    getActivity().onBackPressed();

                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressDialog.hide();
            }
        });

        // get URL from arguments and load it
        String urlToLoad = getArguments().getString(URL_TO_LOAD_KEY);
        webView.loadUrl(urlToLoad);

        return view;
    }

    @Override
    public void onDestroyView() {
        // make sure to dismiss dialog in order to prevent 'leaked window' error
        progressDialog.dismiss();

        super.onDestroyView();
    }
}
