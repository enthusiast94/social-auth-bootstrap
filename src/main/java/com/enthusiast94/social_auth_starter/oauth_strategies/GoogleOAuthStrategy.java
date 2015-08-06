package com.enthusiast94.social_auth_starter.oauth_strategies;

import com.enthusiast94.social_auth_starter.models.AccessToken;
import com.enthusiast94.social_auth_starter.services.AccessTokenService;
import com.enthusiast94.social_auth_starter.services.LinkedAccountService;
import com.enthusiast94.social_auth_starter.services.UserService;
import com.enthusiast94.social_auth_starter.utils.Helpers;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ManasB on 8/1/2015.
 */
public class GoogleOAuthStrategy extends OAuthStrategy {

    public static final String PROVIDER_NAME = "google";
    private static final String CLIENT_ID = "22650997142-72e53ltr1648it12eqjvlo79fh8l7l7o.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "_Vt6ORzdvJ0K7j_-XQovtotO";
    private static final String REDIRECT_URI = SERVER_REDIRECT_URI_BASE + "/google";
    private static final String AUTH_ENDPOINT = "https://accounts.google.com/o/oauth2/auth";
    private static final String TOKEN_ENDPOINT = "https://www.googleapis.com/oauth2/v3/token";
    private static final String USER_ENDPOINT = "https://www.googleapis.com/plus/v1/people/me";

    public GoogleOAuthStrategy(UserService userService, AccessTokenService accessTokenService, LinkedAccountService linkedAccountService) {
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
            postParams.put("code", code);
            postParams.put("client_id", CLIENT_ID);
            postParams.put("client_secret", CLIENT_SECRET);
            postParams.put("redirect_uri", REDIRECT_URI);
            postParams.put("grant_type", "authorization_code");

            String tokenResponse = Helpers.httpPost(TOKEN_ENDPOINT, postParams);
            HashMap<String, String> parsedTokenResponse = parseAccessToken(tokenResponse);

            // get required user info
            String userResponse = Helpers.httpGet(USER_ENDPOINT, parsedTokenResponse, null);
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
        queryParams.put("scope", "email");
        queryParams.put("access_type", "online");
        queryParams.put("approval_prompt", "auto");

        return AUTH_ENDPOINT + "?" + Helpers.stringifyParams(queryParams);
    }

    protected HashMap<String, String> parseAccessToken(String response) {
        HashMap<String, String> parsed = new HashMap<>();

        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(response);

        parsed.put("access_token", json.get("access_token").getAsString());

        return parsed;
    }

     protected HashMap<String, String> parseUserInfo(String response) {
        HashMap<String, String> parsed = new HashMap<>();

        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(response);
        JsonArray emailsJson = json.getAsJsonArray("emails");

        parsed.put("name", json.get("displayName").getAsString());

        for (JsonElement element : emailsJson) {
            JsonObject emailJson = (JsonObject) element;
            if (emailJson.get("type").getAsString().equals("account")) {
                parsed.put("email", emailJson.get("value").getAsString());
                break;
            }
        }

        return parsed;
    }
}
