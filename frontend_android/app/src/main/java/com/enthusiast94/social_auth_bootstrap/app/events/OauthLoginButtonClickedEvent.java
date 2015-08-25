package com.enthusiast94.social_auth_bootstrap.app.events;

/**
 * Created by manas on 24-08-2015.
 */
public class OauthLoginButtonClickedEvent {

    private String urlToLoad;

    public OauthLoginButtonClickedEvent(String urlToLoad) {
        this.urlToLoad = urlToLoad;
    }

    public String getUrlToLoad() {
        return urlToLoad;
    }
}
