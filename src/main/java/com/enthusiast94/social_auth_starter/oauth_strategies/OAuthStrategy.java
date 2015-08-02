package com.enthusiast94.social_auth_starter.oauth_strategies;

import com.enthusiast94.social_auth_starter.models.AccessToken;
import com.enthusiast94.social_auth_starter.models.User;
import com.enthusiast94.social_auth_starter.services.AccessTokenService;
import com.enthusiast94.social_auth_starter.services.UserService;

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

    protected AccessToken generateAccessToken(String username) {
        // if another user with same username already exists, just create a new auth token
        // else create a new user and then a new auth token
        User user = userService.getUserByUsername(username);
        if (user != null) {
            // if an access token already exists, delete it and create a new one, else simply create a new one
            AccessToken existingAccessToken = accessTokenService.getAccessTokenByUserId(user.getId());
            if (existingAccessToken != null) accessTokenService.deleteAccessToken(existingAccessToken);
        } else {
            user = userService.createUser(username, USER_PASSWORD);
        }

        return accessTokenService.createAccessToken(user.getId());
    }
}
