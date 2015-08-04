package com.enthusiast94.social_auth_starter.oauth_strategies;

import com.enthusiast94.social_auth_starter.models.AccessToken;
import com.enthusiast94.social_auth_starter.services.AccessTokenService;
import com.enthusiast94.social_auth_starter.services.UserService;
import com.enthusiast94.social_auth_starter.utils.Helpers;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by manas on 05-08-2015.
 */
public class GithubOAuthStrategy extends OAuthStrategy {

    private static final String CLIENT_ID = "1ffc3eb1fa0c7faa6f86";
    private static final String CLIENT_SECRET = "eca5e03e495ad7c2413ef84d1269d2badc175c7d";
    private static final String REDIRECT_URI = SERVER_REDIRECT_URI_BASE + "/github";
    private static final String AUTH_ENDPOINT = "https://github.com/login/oauth/authorize";
    private static final String TOKEN_ENDPOINT = "https://github.com/login/oauth/access_token";
    private static final String USER_ENDPOINT = "https://api.github.com/user";

    public GithubOAuthStrategy(UserService userService, AccessTokenService accessTokenService) {
        super(userService, accessTokenService);
    }

    @Override
    public String authorize(String code, String error) throws Exception {
        Map<String, String> responseParams = new HashMap<>();

        if (error != null) {
            responseParams.put("error", error);
        } else {
            // get access token
            Map<String, String> postParams = new HashMap<>();
            postParams.put("code", code);
            postParams.put("client_id", CLIENT_ID);
            postParams.put("client_secret", CLIENT_SECRET);
            postParams.put("redirect_uri", REDIRECT_URI);

            String tokenResponse = Helpers.httpPost(TOKEN_ENDPOINT, postParams);
            HashMap<String, String> parsedTokenResponse = parseAccessToken(tokenResponse);

            // get required user info
            String userResponse = Helpers.httpGet(USER_ENDPOINT, parsedTokenResponse);
            HashMap<String, String> parsedMeResponse = parseUserInfo(userResponse);

            // generate access token for user
            AccessToken accessToken = generateAccessToken(parsedMeResponse.get("email"), parsedMeResponse.get("name"));

            responseParams.put("userId", accessToken.getUserId());
            responseParams.put("accessToken", accessToken.getValue());
        }

        return CLIENT_REDIRECT_URI + "?" + Helpers.stringifyParams(responseParams);
    }

    @Override
    public String getAuthUrl() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("client_id", CLIENT_ID);
        queryParams.put("redirect_uri", REDIRECT_URI);
        queryParams.put("scope", "user:email");

        return AUTH_ENDPOINT + "?" + Helpers.stringifyParams(queryParams);
    }

    @Override
    protected HashMap<String, String> parseAccessToken(String response) {
        HashMap<String, String> parsed = new HashMap<>();

        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(response);

        parsed.put("access_token", json.get("access_token").getAsString());

        return parsed;
    }

    @Override
    protected HashMap<String, String> parseUserInfo(String response) {
        HashMap<String, String> parsed = new HashMap<>();

        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(response);

        parsed.put("email", json.get("email").getAsString());
        parsed.put("name", json.get("name").getAsString());

        return parsed;
    }
}
