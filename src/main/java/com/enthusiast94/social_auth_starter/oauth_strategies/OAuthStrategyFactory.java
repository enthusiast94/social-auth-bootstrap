package com.enthusiast94.social_auth_starter.oauth_strategies;

import com.enthusiast94.social_auth_starter.services.AccessTokenService;
import com.enthusiast94.social_auth_starter.services.UserService;

import java.util.HashMap;

/**
 * Created by manas on 02-08-2015.
 */

public class OAuthStrategyFactory {

    private UserService userService;
    private AccessTokenService accessTokenService;
    private HashMap<String, OAuthStrategy> strategies;

    public OAuthStrategyFactory(UserService userService, AccessTokenService accessTokenService) {
        this.userService = userService;
        this.accessTokenService = accessTokenService;

        strategies = new HashMap<>();
        strategies.put("google", new GoogleOAuthStrategy(userService, accessTokenService));
        strategies.put("github", new GithubOAuthStrategy(userService, accessTokenService));
        strategies.put("facebook", new FacebookOAuthStrategy(userService, accessTokenService));
    }

    public OAuthStrategy getStrategy(String name) {
        if (!strategies.containsKey(name)) throw new IllegalArgumentException("No provider with name '" + name + "' found");

        return strategies.get(name);
    }

    public HashMap<String, String> getAllAuthUrls() {
        HashMap<String, String> authUrls = new HashMap<>();

        for (String key : strategies.keySet()) {
            authUrls.put(key, getStrategy(key).getAuthUrl());
        }

        return authUrls;
    }

}
