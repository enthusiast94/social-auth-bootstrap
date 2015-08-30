package com.enthusiast94.social_auth_starter.oauth_strategies;

import com.enthusiast94.social_auth_starter.services.AccessTokenService;
import com.enthusiast94.social_auth_starter.services.LinkedAccountService;
import com.enthusiast94.social_auth_starter.services.UserService;
import com.enthusiast94.social_auth_starter.utils.OauthCredentialsParser;

import java.util.HashMap;

/**
 * Created by manas on 02-08-2015.
 */

public class OAuthStrategyFactory {
    ;
    private HashMap<String, OAuthStrategy> strategies;

    public OAuthStrategyFactory(OauthCredentialsParser oauthCredentialsParser, UserService userService,
                                AccessTokenService accessTokenService, LinkedAccountService linkedAccountService) {
        strategies = new HashMap<>();
        strategies.put(GoogleOAuthStrategy.PROVIDER_NAME, new GoogleOAuthStrategy(oauthCredentialsParser, userService,
                accessTokenService, linkedAccountService));
        strategies.put(GithubOAuthStrategy.PROVIDER_NAME, new GithubOAuthStrategy(oauthCredentialsParser, userService,
                accessTokenService, linkedAccountService));
        strategies.put(FacebookOAuthStrategy.PROVIDER_NAME, new FacebookOAuthStrategy(oauthCredentialsParser, userService,
                accessTokenService, linkedAccountService));
        strategies.put(LinkedinOAuthStrategy.PROVIDER_NAME, new LinkedinOAuthStrategy(oauthCredentialsParser, userService,
                accessTokenService, linkedAccountService));
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
