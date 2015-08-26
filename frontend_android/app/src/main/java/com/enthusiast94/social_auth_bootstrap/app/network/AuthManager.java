package com.enthusiast94.social_auth_bootstrap.app.network;

import com.enthusiast94.social_auth_bootstrap.app.App;
import com.enthusiast94.social_auth_bootstrap.app.utils.Helpers;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by manas on 22-08-2015.
 */

public class AuthManager {

    private static final String TAG = AuthManager.class.getSimpleName();
    private static final String API_BASE = "http://ec2-52-28-155-29.eu-central-1.compute.amazonaws.com:3005";
    public static final String OAUTH_CLIENT_REDIRECT_URI_BASE = "http://localhost:4000";
    private static final String PREF_USER = "user";

    public static void basicAuth(Map<String, String> userDetails, String userType, final Callback callback) {
        Map<String, String> userTypes = new HashMap<String, String>();
        userTypes.put("new", API_BASE + "/users/create");
        userTypes.put("existing", API_BASE + "/auth");

        if (!userTypes.containsKey(userType))
            throw new IllegalArgumentException("Invalid userType provided. Allowed values are: " + userTypes.keySet().toString());

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams(userDetails);
        client.post(userTypes.get(userType), params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                try {
                    if (response.getInt("status") == 200) {
                        JSONObject data = response.getJSONObject("data");

                        Map<String, String> userDetails = new HashMap<String, String>();
                        userDetails.put("userId", data.getString("userId"));
                        userDetails.put("accessToken", data.getString("accessToken"));

                        getUser(userDetails, new Callback() {
                            @Override
                            public void onSuccess(JSONObject data) {
                                if (callback != null) callback.onSuccess(null);
                            }

                            @Override
                            public void onFailure(int statusCode, String message) {
                                if (callback != null) callback.onFailure(statusCode, message);
                            }
                        });
                    } else {
                        if (callback != null) try {
                            callback.onFailure(response.getInt("status"), response.getString("error"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static void oauth(Map<String, String> userDetails, final Callback callback) {
        getUser(userDetails, new Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                if (callback != null) callback.onSuccess(null);
            }

            @Override
            public void onFailure(int statusCode, String message) {
                if (callback != null) callback.onFailure(statusCode, message);
            }
        });
    }

    public static void deauth(final Callback callback) {
        JSONObject userJson = getUserFromCache();

        if (userJson == null) throw new IllegalStateException("Current user is not authenticated");

        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", "Token " + userJson.getString("accessToken"));
            client.post(API_BASE + "/deauth", new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if (response.getInt("status") == 200) {
                            Helpers.clearPrefs(App.getAppContext());
                            if (callback != null) callback.onSuccess(null);
                        } else {
                            if (callback != null)
                                callback.onFailure(response.getInt("status"), response.getString("error"));
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets currently authenticated user and saves it in cache. If this method is invoked when there is no authenticated user in cache, then
     * userId and accessToken of the newly authenticated user must be a part of the provided user details map.
     */
    public static void getUser(Map<String, String> optionalUserDetails, final Callback callback) {
        final JSONObject userJson = getUserFromCache();

        if (userJson == null && optionalUserDetails == null)
            throw new IllegalArgumentException("userId and accessToken are both required");

        final String userId;
        final String accessToken;
        try {
            userId = optionalUserDetails != null ? optionalUserDetails.get("userId") : userJson.getString("userId");
            accessToken = optionalUserDetails != null ? optionalUserDetails.get("accessToken") : userJson.getString("accessToken");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "Token " + accessToken);
        client.get(API_BASE + "/users/" + userId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getInt("status") == 200) {
                        JSONObject data = response.getJSONObject("data");

                        JSONObject jsonToSave = new JSONObject();
                        jsonToSave.put("userId", userId);
                        jsonToSave.put("userEmail", data.getString("email"));
                        jsonToSave.put("accessToken", accessToken);
                        jsonToSave.put("userName", data.getString("name"));
                        jsonToSave.put("userAvatar", data.getString("avatar"));

                        Helpers.writeJsonToPrefs(App.getAppContext(), PREF_USER, jsonToSave);

                        if (callback != null) callback.onSuccess(data);
                    } else {
                        if (callback != null)
                            callback.onFailure(response.getInt("status"), response.getString("error"));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static JSONObject getUserFromCache() {
        return Helpers.readJsonFromPrefs(App.getAppContext(), PREF_USER);
    }

    public static boolean isAuthenticated() {
        return getUserFromCache() != null;
    }

    public static void getAllOauth2Urls(final Callback callback) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(API_BASE + "/oauth2-urls", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getInt("status") == 200) {
                        if (callback != null) callback.onSuccess(response.getJSONObject("data"));
                    } else {
                        if (callback != null)
                            callback.onFailure(response.getInt("status"), response.getString("error"));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
