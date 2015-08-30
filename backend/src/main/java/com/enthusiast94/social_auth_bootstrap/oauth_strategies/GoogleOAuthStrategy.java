package com.enthusiast94.social_auth_bootstrap.oauth_strategies;

import com.enthusiast94.social_auth_bootstrap.models.AccessToken;
import com.enthusiast94.social_auth_bootstrap.services.AccessTokenService;
import com.enthusiast94.social_auth_bootstrap.services.LinkedAccountService;
import com.enthusiast94.social_auth_bootstrap.services.UserService;
import com.enthusiast94.social_auth_bootstrap.utils.Helpers;
import com.enthusiast94.social_auth_bootstrap.utils.OauthCredentialsParser;
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
    private static final String AUTH_ENDPOINT = "https://accounts.google.com/o/oauth2/auth";
    private static final String TOKEN_ENDPOINT = "https://www.googleapis.com/oauth2/v3/token";
    private static final String USER_ENDPOINT = "https://www.googleapis.com/plus/v1/people/me";
    private Map<String, String> credentialsMap;

    public GoogleOAuthStrategy(OauthCredentialsParser oauthCredentialsParser, UserService userService, AccessTokenService accessTokenService, LinkedAccountService linkedAccountService) {
        super(oauthCredentialsParser, userService, accessTokenService, linkedAccountService);

        credentialsMap = oauthCredentialsParser.getOauth2Credentials(PROVIDER_NAME);
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
            postParams.put("client_id", credentialsMap.get("id"));
            postParams.put("client_secret", credentialsMap.get("secret"));
            postParams.put("redirect_uri", credentialsMap.get("redirect_uri"));
            postParams.put("grant_type", "authorization_code");

            String tokenResponse = Helpers.httpPost(TOKEN_ENDPOINT, postParams);
            HashMap<String, String> parsedTokenResponse = parseAccessToken(tokenResponse);

            // get required user info
            String userResponse = Helpers.httpGet(USER_ENDPOINT, parsedTokenResponse, null);
            HashMap<String, String> parsedUserResponse = parseUserInfo(userResponse);

            // generate access token for user
            AccessToken accessToken = generateAccessToken(PROVIDER_NAME, parsedTokenResponse.get("access_token"), parsedUserResponse.get("email"), parsedUserResponse.get("name"), parsedUserResponse.get("avatar"));

            responseParams.put("userId", accessToken.getUserId());
            responseParams.put("accessToken", accessToken.getValue());
        }

        return oauthCredentialsParser.getClientRedirectUri() + "?" + Helpers.stringifyParams(responseParams);
    }

    @Override
    public String getAuthUrl() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("response_type", "code");
        queryParams.put("client_id", credentialsMap.get("id"));
        queryParams.put("redirect_uri", credentialsMap.get("redirect_uri"));
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

         JsonObject imageJson = json.getAsJsonObject("image");
         if (imageJson != null) {
             JsonElement avatar = imageJson.get("url");
             if (avatar != null) {
                 parsed.put("avatar", avatar.getAsString());
             }
         }

        return parsed;
    }
}
