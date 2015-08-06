package com.enthusiast94.social_auth_starter.oauth_strategies;

import com.enthusiast94.social_auth_starter.models.AccessToken;
import com.enthusiast94.social_auth_starter.services.AccessTokenService;
import com.enthusiast94.social_auth_starter.services.LinkedAccountService;
import com.enthusiast94.social_auth_starter.services.UserService;
import com.enthusiast94.social_auth_starter.utils.Helpers;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by manas on 06-08-2015.
 */
public class LinkedinOAuthStrategy extends OAuthStrategy {

    public static final String PROVIDER_NAME = "linkedin";
    private static final String CLIENT_ID = "75vf5mbs1olw7k";
    private static final String CLIENT_SECRET = "PKXuNWIranFvRRVv";
    private static final String REDIRECT_URI = SERVER_REDIRECT_URI_BASE + "/linkedin";
    private static final String AUTH_ENDPOINT = "https://www.linkedin.com/uas/oauth2/authorization";
    private static final String TOKEN_ENDPOINT = "https://www.linkedin.com/uas/oauth2/accessToken";
    private static final String USER_ENDPOINT = "https://api.linkedin.com/v1/people/~:(email-address,first-name,last-name)";

    public LinkedinOAuthStrategy(UserService userService, AccessTokenService accessTokenService, LinkedAccountService linkedAccountService) {
        super(userService, accessTokenService, linkedAccountService);
    }

    @Override
    public String authorize(String code, String error) throws Exception {
        Map<String, String> responseParams = new HashMap<>();

        if (error != null) {
            responseParams.put("error", error);
        } else {
            // get access token
            Map<String, String> postParams = new HashMap<>();
            postParams.put("grant_type", "authorization_code");
            postParams.put("code", code);
            postParams.put("redirect_uri", REDIRECT_URI);
            postParams.put("client_id", CLIENT_ID);
            postParams.put("client_secret", CLIENT_SECRET);

            String tokenResponse = Helpers.httpPost(TOKEN_ENDPOINT, postParams);
            HashMap<String, String> parsedTokenResponse = parseAccessToken(tokenResponse);

            // get required user info
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put("format", "json");
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + parsedTokenResponse.get("access_token"));
            String userResponse = Helpers.httpGet(USER_ENDPOINT, queryParams, headers);
            HashMap<String, String> parsedUserResponse = parseUserInfo(userResponse);

            // generate access token for user
            AccessToken accessToken = generateAccessToken(PROVIDER_NAME, parsedTokenResponse.get("access_token"), parsedUserResponse.get("email"), parsedUserResponse.get("name"));

            responseParams.put("userId", accessToken.getUserId());
            responseParams.put("accessToken", accessToken.getValue());
        }

        return CLIENT_REDIRECT_URI + "?" + Helpers.stringifyParams(responseParams);
    }

    @Override
    public String getAuthUrl() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("response_type", "code");
        queryParams.put("client_id", CLIENT_ID);
        queryParams.put("redirect_uri", REDIRECT_URI);
        queryParams.put("state", "to_be_implemented");
        queryParams.put("scope", "r_basicprofile r_emailaddress");

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

        parsed.put("email", json.get("emailAddress").getAsString());
        parsed.put("name", json.get("firstName").getAsString() + " " + json.get("lastName").getAsString());

        return parsed;
    }
}