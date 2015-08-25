package com.enthusiast94.social_auth_bootstrap.app.events;

/**
 * Created by manas on 24-08-2015.
 */
public class OauthCallbackEvent {

    private String userId;
    private String accessToken;
    private String error;

    public OauthCallbackEvent(String userId, String accessToken, String error) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.error = error;
    }

    public String getUserId() {
        return userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getError() {
        return error;
    }
}
