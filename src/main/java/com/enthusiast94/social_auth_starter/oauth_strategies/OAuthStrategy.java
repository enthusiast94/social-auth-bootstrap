package com.enthusiast94.social_auth_starter.oauth_strategies;

import com.enthusiast94.social_auth_starter.models.AccessToken;
import com.enthusiast94.social_auth_starter.models.User;
import com.enthusiast94.social_auth_starter.services.AccessTokenService;
import com.enthusiast94.social_auth_starter.services.UserService;

import java.util.HashMap;

/**
 * Created by ManasB on 8/1/2015.
 */
public abstract class OAuthStrategy {

    protected static String SERVER_REDIRECT_URI_BASE = "http://localhost:3000/oauth2-callback";
    protected static final String CLIENT_REDIRECT_URI = "http://localhost:4000/#oauth2-callback";
    protected static final String USER_PASSWORD = "this is a secret";

    protected UserService userService;
    protected AccessTokenService accessTokenService;

    public OAuthStrategy(UserService userService, AccessTokenService accessTokenService) {
        this.userService = userService;
        this.accessTokenService = accessTokenService;
    }

    public abstract String authorize(String code, String error) throws Exception;

    public abstract String getAuthUrl();

    protected abstract HashMap<String, String> parseAccessToken(String response);

    protected abstract HashMap<String, String> parseUserInfo(String response);

    protected AccessToken generateAccessToken(String email, String name) {
        AccessToken accessToken;

        // if user already exists, just create a new auth token
        // else create a new user and then a new auth token
        User user = userService.getUserByEmail(email);
        if (user != null) {
            // if an access token already exists, simply return it, else create a new one
            accessToken = accessTokenService.getAccessTokenByUserId(user.getId());
            if (accessToken == null) {
                accessToken = accessTokenService.createAccessToken(user.getId());
            }
        } else {
            user = userService.createUser(email, name, USER_PASSWORD);
            accessToken = accessTokenService.createAccessToken(user.getId());
        }

        return accessToken;
    }
}
