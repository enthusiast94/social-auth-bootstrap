package com.enthusiast94.social_auth_starter.oauth_strategies;

import com.enthusiast94.social_auth_starter.models.AccessToken;
import com.enthusiast94.social_auth_starter.services.AccessTokenService;
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

    private static final String CLIENT_ID = "22650997142-72e53ltr1648it12eqjvlo79fh8l7l7o.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "_Vt6ORzdvJ0K7j_-XQovtotO";
    private static final String REDIRECT_URI = SERVER_REDIRECT_URI_BASE + "/google";
    private static final String AUTH_ENDPOINT = "https://accounts.google.com/o/oauth2/auth";
    private static final String TOKEN_ENDPOINT = "https://www.googleapis.com/oauth2/v3/token";
    private static final String ME_ENDPOINT = "https://www.googleapis.com/plus/v1/people/me";

    public GoogleOAuthStrategy(UserService userService, AccessTokenService accessTokenService) {
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
            postParams.put("grant_type", "authorization_code");

            String tokenResponse = Helpers.httpPost(TOKEN_ENDPOINT, postParams);
            HashMap<String, String> parsedTokenResponse = parseAccessToken(tokenResponse);

            // get user's email address to be used as their username
            String meResponse = Helpers.httpGet(ME_ENDPOINT, parsedTokenResponse);
            HashMap<String, String> parsedMeResponse = parseEmail(meResponse);

            // generate access token for user
            AccessToken accessToken = generateAccessToken(parsedMeResponse.get("email"));

            responseParams.put("accessToken", accessToken.getAccessToken());
            responseParams.put("expiresIn", String.valueOf(accessToken.getExpiresIn()));
            responseParams.put("createdAt", String.valueOf(accessToken.getCreatedAt()));
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

    private HashMap<String, String> parseAccessToken(String response) throws Exception {
        HashMap<String, String> parsed = new HashMap<>();

        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(response);

        parsed.put("access_token", json.get("access_token").getAsString());

        return parsed;
    }

    private HashMap<String, String> parseEmail(String response) throws Exception {
        HashMap<String, String> parsed = new HashMap<>();

        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(response);
        JsonArray emailsJson = json.getAsJsonArray("emails");

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
